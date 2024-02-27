package com.example.currencyexchanger.presentation.exchanger.models

import com.example.currencyexchanger.domain.models.CurrencyItem

sealed interface ExchangerScreenState{
    data object Error: ExchangerScreenState
    data class Content(val topList: List<CurrencyItem>, val bottomList: List<CurrencyItem>, val topRate: String): ExchangerScreenState
    data object Loading: ExchangerScreenState
}