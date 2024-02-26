package com.example.currencyexchanger.data.network

import com.example.currencyexchanger.data.network.dto.Response

interface NetworkClient {

    suspend fun getCurrencies(): Response
}