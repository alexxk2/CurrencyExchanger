package com.example.currencyexchanger.data.converters

import com.example.currencyexchanger.data.local_storage.dto.AccountDto
import com.example.currencyexchanger.data.network.dto.Rates
import com.example.currencyexchanger.domain.models.CurrencyItem
import javax.inject.Inject

class NetworkConverter @Inject constructor() {

    fun map(rates: Rates, accountDto: AccountDto): List<CurrencyItem> {
        return listOf(
            CurrencyItem(name = EUR, rate = rates.EUR, balance = accountDto.eurBalance),
            CurrencyItem(name = USD, rate = rates.USD, balance = accountDto.usdBalance),
            CurrencyItem(name = GBP, rate = rates.GBP, balance = accountDto.gbpBalance)
        )
    }

    companion object{
        const val EUR = "EUR"
        const val USD = "USD"
        const val GBP = "GBP"
    }
}