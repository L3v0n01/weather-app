package com.la.weather.core.common.connectivity.di

import com.la.weather.core.common.connectivity.ConnectivityObserver
import com.la.weather.core.common.connectivity.ConnectivityObserverImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    abstract fun bindConnectivityObserver(impl: ConnectivityObserverImpl): ConnectivityObserver
}
