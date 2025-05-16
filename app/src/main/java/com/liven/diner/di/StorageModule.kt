package com.liven.diner.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    private const val AUTH_PREFS_NAME = "auth_prefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext contexts: Context): SharedPreferences {
        return contexts.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
    }
}