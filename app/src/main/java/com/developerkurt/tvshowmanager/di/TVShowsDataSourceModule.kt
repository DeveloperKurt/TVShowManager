package com.developerkurt.tvshowmanager.di

import com.developerkurt.tvshowmanager.data.source.RemoteTVShowsSource
import com.developerkurt.tvshowmanager.data.source.TVShowsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class TVShowsDataSourceModule
{
    @Binds
    abstract fun bindTVShowsDataSource(remoteTVShowsSource: RemoteTVShowsSource): TVShowsDataSource
}