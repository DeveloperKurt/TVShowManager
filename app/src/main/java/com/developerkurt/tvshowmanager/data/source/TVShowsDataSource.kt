package com.developerkurt.tvshowmanager.data.source

import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.ResultListener
import com.developerkurt.tvshowmanager.model.Movie
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Main entry point for accessing TV show related data. By making it an interface, the data source can easily by switched in between
 * local, remote, alternative remote and mock test sources.
 */
interface TVShowsDataSource
{
    fun fetchTVShows(listener: ResultListener<Result<List<Movie>>>): Disposable
    fun createTVShow(title: String, seasons: Double? = null, releaseDate: Date? = null, listener: ResultListener<Result<Any>>): Disposable
}

