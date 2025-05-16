package com.liven.diner.di

import com.liven.diner.data.remote.AuthTokenProvider
import com.liven.diner.data.remote.SharedPreferencesAuthTokenProvider
import com.liven.diner.data.repository.AuthRepository
import com.liven.diner.data.repository.AuthRepositoryImpl
import com.liven.diner.data.repository.VenuesMenuItemsRepository
import com.liven.diner.data.repository.VenuesMenuItemsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVenuesMenuItemsRepository(
        venuesMenuItemsRepositoryImpl: VenuesMenuItemsRepositoryImpl
    ): VenuesMenuItemsRepository


    @Binds
    @Singleton
    abstract fun bindAuthTokenProvider(
        sharedPreferencesAuthTokenProvider: SharedPreferencesAuthTokenProvider
    ): AuthTokenProvider

}