package com.la.weather.core.data.di

import com.la.weather.core.data.repository.CitySearchRepositoryImpl
import com.la.weather.core.data.repository.WeatherRepositoryImpl
import com.la.weather.core.domain.repository.CitySearchRepository
import com.la.weather.core.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCitySearchRepository(impl: CitySearchRepositoryImpl): CitySearchRepository
}
