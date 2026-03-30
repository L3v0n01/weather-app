package com.la.weather.core.network.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class WeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class GeocodingRetrofit