package com.example.currencyexchanger.domain.exchanger

import com.example.currencyexchanger.domain.models.Account
import com.example.currencyexchanger.domain.models.CurrencyItem
import com.example.currencyexchanger.domain.repositories.ExchangerRepository
import javax.inject.Inject

class ExchangerInteractorImpl @Inject constructor(
    private val exchangerRepository: ExchangerRepository
) : ExchangerInteractor {

    override suspend fun getCurrencies(): List<CurrencyItem> {
        return exchangerRepository.getCurrencies()
    }

    override suspend fun getBalance(): Account {
        return exchangerRepository.getBalance()
    }

    override suspend fun updateBalance(account: Account) {
        exchangerRepository.updateBalance(account)
    }
}