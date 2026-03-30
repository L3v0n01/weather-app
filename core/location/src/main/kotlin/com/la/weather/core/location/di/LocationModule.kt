package com.la.weather.core.location.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.la.weather.core.location.LocationProvider
import com.la.weather.core.location.LocationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindsLocationProvider(impl: LocationProviderImpl): LocationProvider

    companion object {
        @Provides
        @Singleton
        fun providesFusedLocationClient(
            @ApplicationContext context: Context,
        ): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
    }
}
