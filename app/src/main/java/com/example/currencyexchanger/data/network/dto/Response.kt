package com.example.currencyexchanger.data.network.dto

sealed interface Response{
    data class Success(val rates: Rates): Response
    data object Error: Response
}