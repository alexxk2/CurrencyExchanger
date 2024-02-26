package com.example.currencyexchanger.di

import com.example.currencyexchanger.data.converters.LocalStorageConverter
import com.example.currencyexchanger.data.converters.NetworkConverter
import com.example.currencyexchanger.data.local_storage.LocalStorage
import com.example.currencyexchanger.data.network.NetworkClient
import com.example.currencyexchanger.data.network.OnlineRatesApi
import com.example.currencyexchanger.data.network.impl.NetworkClientImpl
import com.example.currencyexchanger.data.repositories_impl.ExchangerRepositoryImpl
import com.example.currencyexchanger.domain.repositories.ExchangerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val baseUrl = "https://www.cbr-xml-daily.ru"
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun provideOnlineRatesApi(retrofit: Retrofit): OnlineRatesApi {
        return retrofit.create(OnlineRatesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkClient(onlineRatesApi: OnlineRatesApi): NetworkClient {
        return NetworkClientImpl(onlineRatesApi)
    }


    @Provides
    @Singleton
    fun provideExchangerRepository(
        networkClient: NetworkClient,
        networkConverter: NetworkConverter,
        localStorage: LocalStorage,
        localStorageConverter: LocalStorageConverter
    ): ExchangerRepository {
        return ExchangerRepositoryImpl(
            networkClient,
            networkConverter,
            localStorage,
            localStorageConverter
        )
    }
}