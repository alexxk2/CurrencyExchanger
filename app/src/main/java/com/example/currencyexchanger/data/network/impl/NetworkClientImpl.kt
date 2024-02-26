package com.example.currencyexchanger.data.network.impl

import com.example.currencyexchanger.data.network.NetworkClient
import com.example.currencyexchanger.data.network.OnlineRatesApi
import com.example.currencyexchanger.data.network.dto.Response
import javax.inject.Inject

class NetworkClientImpl @Inject constructor(
    private val onlineRatesApi: OnlineRatesApi
) : NetworkClient {

    override suspend fun getCurrencies(): Response {

        val response = onlineRatesApi.getCurrencies()
        return if (response.isSuccessful) {

            response.body()?.let {
                Response.Success(it.rates)
            } ?: Response.Error

        } else {
            Response.Error
        }
    }
}