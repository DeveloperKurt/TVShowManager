package com.developerkurt.tvshowmanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.ResultListener
import com.developerkurt.gamedatabase.data.source.succeeded
import com.developerkurt.tvshowmanager.data.source.TVShowsDataSource
import com.developerkurt.tvshowmanager.model.Movie
import com.developerkurt.tvshowmanager.model.ShowcaseMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TVShowViewModel @Inject constructor(
        val handle: SavedStateHandle,
        private val tvShowsDataSource: TVShowsDataSource) : ViewModel()
{

    private val moviesLiveData = MutableLiveData<Result<List<ShowcaseMovie>>>(Result.Loading)
    private val createMovieResultLiveData = MutableLiveData<Boolean>()


    private var fetchMoviesDisposable: Disposable? = null
    private var createMovieDisposable: Disposable? = null


    private val dateTimeFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    /**
     * Fetches the TV Shows from a data source and transforms the [Movie]] type to [ShowcaseMovie] in order to get the [Movie.releaseDate] fields
     * as a formatted String
     */
    internal fun fetchTVShows(): LiveData<Result<List<ShowcaseMovie>>>
    {

        fetchMoviesDisposable = tvShowsDataSource.fetchTVShows(object : ResultListener<Result<List<Movie>>>
        {
            override fun onResult(result: Result<List<Movie>>)
            {

                when (result)
                {
                    is Result.Success<List<Movie>> ->
                    {

                        val showcaseMovieList = mutableListOf<ShowcaseMovie>()
                        result.data.forEach {

                            //Format the date to string
                            var date = ""
                            if (it.releaseDate != null)
                            {
                                try
                                {
                                    date = dateTimeFormat.format(it.releaseDate).toString()
                                }
                                catch (e: ParseException)
                                {
                                    Log.w("TvShowViewModel", "fetchMovies: Couldn't parse this date: ${it.releaseDate}", e)
                                }
                            }

                            showcaseMovieList.add(ShowcaseMovie(it.title, date, it.seasons))
                        }
                        moviesLiveData.value = Result.Success(showcaseMovieList)
                    }
                    is Result.Error -> moviesLiveData.value = result
                    is Result.Loading -> moviesLiveData.value = result
                }
            }

        })


        return moviesLiveData
    }


    /**
     * Requests to create a new TV show.
     *
     * @return returns true if succeeds
     */
    internal fun createTVShow(
            title: String,
            seasons: Double? = null,
            releaseDate: Date? = null): LiveData<Boolean>
    {
        createMovieDisposable = tvShowsDataSource.createTVShow(title, seasons, releaseDate, object : ResultListener<Result<Any>>
        {
            override fun onResult(result: Result<Any>)
            {
                createMovieResultLiveData.value = result.succeeded
            }
        })
        return createMovieResultLiveData
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
        //Disposes any observables if applicable
        disposeIfNotNullAndDisposed(createMovieDisposable, fetchMoviesDisposable)

    }
}