package com.example.currencyexchanger.domain.repositories

import com.example.currencyexchanger.domain.models.Account
import com.example.currencyexchanger.domain.models.CurrencyItem

interface ExchangerRepository {
    suspend fun getCurrencies(): List<CurrencyItem>
    suspend fun getBalance(): Account
    suspend fun updateBalance(account: Account)
}