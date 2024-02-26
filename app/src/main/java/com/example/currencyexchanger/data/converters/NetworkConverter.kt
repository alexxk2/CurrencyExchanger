package com.example.currencyexchanger.data.converters

import com.example.currencyexchanger.data.network.dto.Rates
import com.example.currencyexchanger.domain.models.CurrencyItem
import javax.inject.Inject

class NetworkConverter @Inject constructor() {

    fun map(rates: Rates): List<CurrencyItem> {
        return listOf(
            CurrencyItem(name = EUR, rate = rates.EUR),
            CurrencyItem(name = USD, rate = rates.USD),
            CurrencyItem(name = GBP, rate = rates.GBP)
        )
    }

    companion object{
        const val EUR = "EUR"
        const val USD = "USD"
        const val GBP = "GBP"

    }
}