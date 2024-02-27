package com.example.currencyexchanger.data.repositories_impl

import com.example.currencyexchanger.data.converters.LocalStorageConverter
import com.example.currencyexchanger.data.converters.NetworkConverter
import com.example.currencyexchanger.data.local_storage.LocalStorage
import com.example.currencyexchanger.data.network.NetworkClient
import com.example.currencyexchanger.data.network.dto.Response
import com.example.currencyexchanger.domain.models.Account
import com.example.currencyexchanger.domain.models.CurrencyItem
import com.example.currencyexchanger.domain.repositories.ExchangerRepository
import javax.inject.Inject

class ExchangerRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val networkConverter: NetworkConverter,
    private val localStorage: LocalStorage,
    private val localStorageConverter: LocalStorageConverter
) : ExchangerRepository {


    override suspend fun getCurrencies(): List<CurrencyItem> {

         val balanceResponse = localStorage.getBalance()

        return when (val currenciesResponse = networkClient.getCurrencies()) {
            Response.Error -> emptyList()
            is Response.Success -> networkConverter.map(currenciesResponse.rates, balanceResponse)
        }
    }

    override suspend fun getBalance(): Account {
        val response = localStorage.getBalance()
        return localStorageConverter.map(response)
    }

    override suspend fun updateBalance(account: Account) {
        localStorage.updateBalance(
            localStorageConverter.map(account)
        )
    }
}