package com.example.currencyexchanger.data.converters

import com.example.currencyexchanger.data.local_storage.dto.AccountDto
import com.example.currencyexchanger.domain.models.Account
import javax.inject.Inject

class LocalStorageConverter @Inject constructor() {

    fun map(account: Account): AccountDto{

        with(account){
            return AccountDto(
                eurBalance = eurBalance,
                usdBalance = usdBalance,
                gbpBalance = gbpBalance
            )
        }
    }

    fun map(accountDto: AccountDto): Account{

        with(accountDto){
            return Account(
                eurBalance = eurBalance,
                usdBalance = usdBalance,
                gbpBalance = gbpBalance
            )
        }
    }
}