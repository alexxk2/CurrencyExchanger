package com.example.currencyexchanger.data.local_storage

import com.example.currencyexchanger.data.local_storage.dto.AccountDto

interface LocalStorage {
    suspend fun getBalance(): AccountDto
    suspend fun updateBalance(accountDto: AccountDto)
}