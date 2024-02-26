package com.example.currencyexchanger.di

import android.content.Context
import com.example.currencyexchanger.data.local_storage.LocalStorage
import com.example.currencyexchanger.data.local_storage.impl.LocalStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class LocalStorageModule {

    @Provides
    @Singleton
    fun provideLocalStorage(@ApplicationContext context: Context): LocalStorage{
        return LocalStorageImpl(context)
    }
}