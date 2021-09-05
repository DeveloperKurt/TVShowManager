package com.developerkurt.tvshowmanager.data.source

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.rx
import com.apollographql.apollo.rx2.rxQuery
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.ResultListener
import com.developerkurt.tvshowmanager.CreateMovieMutation
import com.developerkurt.tvshowmanager.FetchMoviesQuery
import com.developerkurt.tvshowmanager.model.Movie
import com.developerkurt.tvshowmanager.type.CreateMovieFieldsInput
import com.developerkurt.tvshowmanager.type.CreateMovieInput
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A remote GraphQL TVShowsDataSource
 */
class RemoteTVShowsSource(private val apolloClient: ApolloClient) : TVShowsDataSource
{
    private val TAG = "DefaultTVShowsRepository"

    private var lastCursor = Input.absent<String>()

    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    /**
     * Fetches the TV Shows with pagination. Each time it is called, it returns the next page until no page is left.
     */
    override fun fetchTVShows(listener: ResultListener<Result<List<Movie>>>): Disposable
    {
        val query = FetchMoviesQuery(lastCursor)

        val maybe = apolloClient.rxQuery(query).singleElement()

        return maybe
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null || it.data == null || it.data!!.movies.edges == null)
                {
                    listener.onResult(Result.Error())
                }
                else
                {
                    val list: MutableList<Movie> = mutableListOf()

                    //Do error checks, and format the date before emitting it to the LiveData
                    it.data?.movies?.edges?.forEach {

                        val movie = it!!.node!!.fragments.movieFragment
                        var date: Date? = null

                        if (movie.releaseDate != null)
                        {
                            try
                            {
                                date = dateTimeFormat.parse(movie.releaseDate.toString())
                            }
                            catch (e: ParseException)
                            {
                                Log.e(TAG, "fetchMovies: Couldn't parse the date", e)
                            }
                        }

                        list.add(Movie(movie.title, date, movie.seasons))
                    }

                    if (!it.hasErrors())
                    {
                        //Pagination
                        val hasNextPage = it.data?.movies?.pageInfo?.hasNextPage
                        if (hasNextPage != null)
                        {
                            lastCursor = Input.fromNullable(it.data?.movies?.pageInfo?.endCursor)
                            listener.onResult(Result.Success(list))
                        }
                        else
                        {
                            Log.w(
                                    TAG, "fetchMovies: Couldn't read the 'hasNextPage' field, not emitting the fetched lists " +
                                    "to avoid duplication.")

                        }
                    }
                    else
                    {
                        listener.onResult(Result.Error())
                    }
                }
            },
                    {//If the request fails
                        Log.w(TAG, "fetchMovies: ", it)
                        listener.onResult(Result.Error())
                    })

    }

    /**
     * Creates a new TV show.
     *
     * The state of the operation can be listen through the [ResultListener] argument
     */
    override fun createTVShow(title: String, seasons: Double?, releaseDate: Date?, listener: ResultListener<Result<Any>>): Disposable
    {
        val createMovie = CreateMovieMutation(
                CreateMovieInput(
                        Input.fromNullable(
                                CreateMovieFieldsInput(
                                        title = title,
                                        releaseDate = Input.fromNullable(releaseDate),
                                        seasons = Input.fromNullable(seasons)))))


        return apolloClient.mutate(createMovie)
            .rx()
            .singleElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (!it.hasErrors() && it.data != null && it.data!!.createMovie != null)
                    listener.onResult(Result.Success(it))
                else
                    listener.onResult(Result.Error())

            },//If the request fails
                    {
                        Log.e(TAG, "createTVShow: ", it)
                        listener.onResult(Result.Error())
                    })
    }
}