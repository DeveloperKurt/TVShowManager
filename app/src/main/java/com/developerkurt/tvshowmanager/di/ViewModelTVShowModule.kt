package com.developerkurt.tvshowmanager.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideApolloClient(okHttpClient: OkHttpClient): ApolloClient
    {

        return ApolloClient.builder()
            .serverUrl("https://tv-show-manager.combyne.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(@ApplicationContext applicationContext: Context): OkHttpClient
    {

        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(applicationContext))
            .build()
    }


    private class AuthorizationInterceptor(val context: Context) : Interceptor
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