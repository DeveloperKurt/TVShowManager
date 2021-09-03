package com.developerkurt.tvshowmanager.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rxQuery
import com.developerkurt.tvshowmanager.FetchAllMoviesQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TVShowViewModel @Inject constructor(val handle: SavedStateHandle, private val apolloClient: ApolloClient) : ViewModel()
{

    fun fetchMovies()
    {
        val query = FetchAllMoviesQuery()


        val observable = apolloClient.rxQuery(query)

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                it.data?.movies?.edges?.forEach { println(it?.node?.fragments?.movieFragment.toString()) }
            }
            .subscribe()

    }

}