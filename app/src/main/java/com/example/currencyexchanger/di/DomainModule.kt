package com.example.currencyexchanger.di

import com.example.currencyexchanger.domain.exchanger.ExchangerInteractor
import com.example.currencyexchanger.domain.exchanger.ExchangerInteractorImpl
import com.example.currencyexchanger.domain.repositories.ExchangerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {


    @Provides
    fun provideExchangerInteractor(exchangerRepository: ExchangerRepository): ExchangerInteractor {
        return ExchangerInteractorImpl(exchangerRepository)
    }
}