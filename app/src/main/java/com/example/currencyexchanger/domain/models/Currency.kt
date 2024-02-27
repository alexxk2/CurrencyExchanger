package com.example.currencyexchanger.domain.models

data class CurrencyItem(
    val name: String,
    val rate: Double,
    val value: Double = 0.0,
    val exchangeRate: String = "",
    val balance: Double = 0.0
)
