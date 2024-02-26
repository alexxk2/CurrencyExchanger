package com.example.currencyexchanger.data.local_storage.impl

import android.content.Context
import com.example.currencyexchanger.data.local_storage.LocalStorage
import com.example.currencyexchanger.data.local_storage.dto.AccountDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalStorageImpl @Inject constructor(
    @ApplicationContext context: Context
) : LocalStorage {

    private val prefs = context.getSharedPreferences(PREFS, 0)

    override suspend fun getBalance(): AccountDto {
        val eurBalance = prefs.getFloat(EUR_BALANCE, 100.0F)
        val usdBalance = prefs.getFloat(USD_BALANCE, 100.0F)
        val gbpBalance = prefs.getFloat(GBP_BALANCE, 100.0F)


       return AccountDto(
           eurBalance = eurBalance.toDouble(),
           usdBalance =usdBalance.toDouble(),
           gbpBalance = gbpBalance.toDouble()
       )
    }

    override suspend fun updateBalance(accountDto: AccountDto) {

        prefs.edit().putFloat(EUR_BALANCE,accountDto.eurBalance.toFloat()).apply()
        prefs.edit().putFloat(EUR_BALANCE,accountDto.eurBalance.toFloat()).apply()
        prefs.edit().putFloat(EUR_BALANCE,accountDto.eurBalance.toFloat()).apply()

    }


    companion object{
        const val PREFS = "shared_prefs"
        const val EUR_BALANCE = "eur_balance"
        const val USD_BALANCE = "usd_balance"
        const val GBP_BALANCE = "gbp_balance"
    }
}