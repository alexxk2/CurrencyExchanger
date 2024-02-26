package com.example.currencyexchanger.domain.exchanger

import com.example.currencyexchanger.domain.models.Account
import com.example.currencyexchanger.domain.models.CurrencyItem

interface ExchangerInteractor {

    suspend fun getCurrencies(): List<CurrencyItem>
    suspend fun getBalance(): Account
    suspend fun updateBalance(account: Account)

}