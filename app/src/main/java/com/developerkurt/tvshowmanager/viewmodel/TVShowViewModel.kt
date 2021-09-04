package com.developerkurt.tvshowmanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rxQuery
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.tvshowmanager.FetchAllMoviesQuery
import com.developerkurt.tvshowmanager.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TVShowViewModel @Inject constructor(val handle: SavedStateHandle, private val apolloClient: ApolloClient) : ViewModel()
{

    private val moviesLiveData = MutableLiveData<Result<List<Movie>>>(Result.Loading)

    private var fetchMoviesDisposable: Disposable? = null

    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN)

    internal fun fetchMovies(): LiveData<Result<List<Movie>>>
    {
        val query = FetchAllMoviesQuery()


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
                            date = dateTimeFormat.format(dateTimeFormat.parse(movie.releaseDate.toString())!!).toString()
                        }

                        list.add(
                                Movie(
                                        movie.title,
                                        date,
                                        movie.seasons!!))

                    }

                    if (it.errors == null || it.errors!!.size == 0)
                    {
                        moviesLiveData.value = Result.Success(list)
                    }
                    else
                    {
                        moviesLiveData.value = Result.Error()

                    }

                }

            }, {
                Log.w("TvShowViewModel", "fetchMovies: ", it)
                moviesLiveData.value = Result.Error()
            })

        return moviesLiveData
    }


    override fun onCleared()
    {
        super.onCleared()

        with(fetchMoviesDisposable) {
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