package com.example.currencyexchanger.data.network

import com.example.currencyexchanger.data.network.dto.CurrenciesResponse
import retrofit2.Response
import retrofit2.http.GET

interface OnlineRatesApi {

    @GET("/latest.js")
    suspend fun getCurrencies(): Response<CurrenciesResponse>
}