package com.developerkurt.tvshowmanager.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.developerkurt.tvshowmanager.data.source.RemoteTVShowsSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response


@InstallIn(ViewModelComponent::class)
@Module
object ViewModelTVShowModule
{
    @Provides
    @ViewModelScoped
    fun provideDefaultTVShowsRepository(apolloClient: ApolloClient): RemoteTVShowsSource
    {
        return RemoteTVShowsSource(apolloClient)
    }

    @Provides
    @ViewModelScoped
    fun provideApolloClient(okHttpClient: OkHttpClient): ApolloClient
    {

        return ApolloClient.builder()
            .serverUrl("https://tv-show-manager.combyne.com/graphql")
            .okHttpClient(okHttpClient)
            .defaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(): OkHttpClient
    {

        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor())
            .build()
    }


    private class AuthorizationInterceptor() : Interceptor
    {
        override fun intercept(chain: Interceptor.Chain): Response
        {
            val request = chain.request().newBuilder()
                .addHeader("X-Parse-Client-Key", "yiCk1DW6WHWG58wjj3C4pB/WyhpokCeDeSQEXA5HaicgGh4pTUd+3/rMOR5xu1Yi")
                .addHeader("X-Parse-Application-Id", "AaQjHwTIQtkCOhtjJaN/nDtMdiftbzMWW5N8uRZ+DNX9LI8AOziS10eHuryBEcCI")
                .build()

            return chain.proceed(request)
        }
    }
}