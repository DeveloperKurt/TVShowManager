package com.developerkurt.tvshowmanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.rx
import com.apollographql.apollo.rx2.rxQuery
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.tvshowmanager.CreateMovieMutation
import com.developerkurt.tvshowmanager.FetchMoviesQuery
import com.developerkurt.tvshowmanager.data.CreateTVShowListener
import com.developerkurt.tvshowmanager.model.Movie
import com.developerkurt.tvshowmanager.type.CreateMovieFieldsInput
import com.developerkurt.tvshowmanager.type.CreateMovieInput
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TVShowViewModel @Inject constructor(
        val handle: SavedStateHandle,
        private val apolloClient: ApolloClient) : ViewModel()
{

    private val moviesLiveData = MutableLiveData<Result<List<Movie>>>(Result.Loading)

    private var fetchMoviesDisposable: Disposable? = null
    private var createMovieDisposable: Disposable? = null

    private var lastCursor = Input.absent<String>()

    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    internal fun fetchTVShows(): LiveData<Result<List<Movie>>>
    {

        val query = FetchMoviesQuery(lastCursor)

        val maybe = apolloClient.rxQuery(query).singleElement()

        fetchMoviesDisposable = maybe
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == null || it.data == null || it.data!!.movies.edges == null)
                {
                    moviesLiveData.value = Result.Error()
                }
                else
                {
                    val list: MutableList<Movie> = mutableListOf()

                    //Do error checks, and format the date before emitting it to the LiveData
                    it.data?.movies?.edges?.forEach {

                        val movie = it!!.node!!.fragments.movieFragment

                        //Check if the date is null before trying to format or use it
                        var date = ""
                        if (movie.releaseDate != null)
                        {
                            try
                            {
                                date = dateTimeFormat.format(dateTimeFormat.parse(movie.releaseDate.toString())!!).toString()
                            }
                            catch (e: ParseException)
                            {
                                Log.w("TvShowViewModel", "fetchMovies: Couldn't parse this date: $movie.releaseDate.toString()", e)
                            }
                        }

                        list.add(Movie(movie.title, date, movie.seasons!!))
                    }

                    if (!it.hasErrors())
                    {
                        //Pagination
                        val hasNextPage = it.data?.movies?.pageInfo?.hasNextPage
                        if (hasNextPage != null)
                        {
                            lastCursor = Input.fromNullable(it.data?.movies?.pageInfo?.endCursor)
                            moviesLiveData.value = Result.Success(list)
                        }
                        else
                        {
                            Log.w(
                                    "TvShowViewModel", "fetchMovies: Couldn't read the 'hasNextPage' field, not emitting the fetched lists " +
                                    "to avoid duplication.")

                        }
                    }
                    else
                    {
                        moviesLiveData.value = Result.Error()
                    }
                }
            },
                    {//If the request fails
                        Log.w("TvShowViewModel", "fetchMovies: ", it)
                        moviesLiveData.value = Result.Error()
                    })


        return moviesLiveData
    }


    internal fun createTVShow(title: String, seasons: Double? = null, releaseDate: Date? = null, createTVShowListener: CreateTVShowListener? = null)
    {


        val createMovie = CreateMovieMutation(
                CreateMovieInput(
                        Input.fromNullable(
                                CreateMovieFieldsInput(
                                        title = title,
                                        releaseDate = Input.fromNullable(releaseDate),
                                        seasons = Input.fromNullable(seasons))))
                                             )
        createMovieDisposable = apolloClient.mutate(createMovie)
            .rx()
            .singleElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (!it.hasErrors() && it.data != null && it.data!!.createMovie != null)
                    createTVShowListener?.onSuccess()
                else
                    createTVShowListener?.onError()

            },//If the request fails
                    {
                        Log.e("TvShowViewModel", "createTVShow: ", it)
                        createTVShowListener?.onError()
                    })

    }

    /**
     * If the passed disposables are not null and not already disposed, it disposes them
     */
    private fun disposeIfNotNullAndDisposed(vararg disposables: Disposable?)
    {
        disposables.forEach {
            with(it)
            {
                if (this != null)
                {
                    if (!isDisposed)
                    {
                        dispose()
                    }
                }
            }
        }
    }

    override fun onCleared()
    {
        super.onCleared()
        disposeIfNotNullAndDisposed(createMovieDisposable, fetchMoviesDisposable)

    }
}