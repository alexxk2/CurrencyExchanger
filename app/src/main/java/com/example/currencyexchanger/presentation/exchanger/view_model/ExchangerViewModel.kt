package com.example.currencyexchanger.presentation.exchanger.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchanger.R
import com.example.currencyexchanger.domain.exchanger.ExchangerInteractor
import com.example.currencyexchanger.domain.models.Account
import com.example.currencyexchanger.domain.models.CurrencyItem
import com.example.currencyexchanger.presentation.exchanger.models.ExchangerScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExchangerViewModel @Inject constructor(
    private val exchangerInteractor: ExchangerInteractor
) : ViewModel() {

    private var topList: MutableList<CurrencyItem> = mutableListOf()
    private var bottomList: MutableList<CurrencyItem> = mutableListOf()

    private var topRate: Double = 1.0
    private var bottomRate: Double = 1.0

    private var topCurrency: String = ""
    private var bottomCurrency: String = ""

    private var topActivePosition = 0
    private var bottomActivePosition = 0

    private var topValue: Double = 0.0
    private var bottomValue: Double = 0.0

    private lateinit var account: Account

    private var usdRate = 0.0
    private var eurRate = 0.0
    private var gbpRate = 0.0

    private var updateJob: Job? = null

    private val _exchangerScreenState = MutableLiveData<ExchangerScreenState>()
    val exchangerScreenState: LiveData<ExchangerScreenState> = _exchangerScreenState

    private val _errorMessageSender = MutableSharedFlow<String>()
    val errorMessageSender = _errorMessageSender.asSharedFlow()

    private val _receiptSender = MutableSharedFlow<String>()
    val receiptSender = _receiptSender.asSharedFlow()

    private val _updateSender = MutableSharedFlow<Int>()
    val updateSender = _updateSender.asSharedFlow()

    fun getCurrencies() {
        _exchangerScreenState.postValue(ExchangerScreenState.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currencies = exchangerInteractor.getCurrencies()
                if (currencies.isNotEmpty()) {
                    currencies.forEach {
                        when (it.name) {
                            USD -> usdRate = it.rate
                            EUR -> eurRate = it.rate
                            GBP -> gbpRate = it.rate
                        }
                    }
                    topCurrency = currencies.first().name
                    bottomCurrency = currencies.first().name

                    topList.addAll(currencies)
                    bottomList.addAll(currencies)

                    emitNewValues()

                } else {
                    _exchangerScreenState.postValue(ExchangerScreenState.Error)
                }
            } catch (e: Exception) {
                _exchangerScreenState.postValue(ExchangerScreenState.Error)
            }
        }
    }

    private fun updateRates(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currencies = exchangerInteractor.getCurrencies()
                if (currencies.isNotEmpty()) {
                    currencies.forEach {
                        when (it.name) {
                            USD -> usdRate = it.rate
                            EUR -> eurRate = it.rate
                            GBP -> gbpRate = it.rate
                        }
                    }
                    emitNewValues()
                } else {
                    _exchangerScreenState.postValue(ExchangerScreenState.Error)
                }
            } catch (e: Exception) {
                _exchangerScreenState.postValue(ExchangerScreenState.Error)
            }
        }
    }

    fun startRatesUpdate(){
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.IO) {
            updateRates()
            _updateSender.emit(0)
            delay(UPDATE_DELAY)
            startRatesUpdate()
        }
    }

    fun stopRatesUpdate(){
        updateJob?.cancel()
    }

    fun getAccountData(){
        viewModelScope.launch(Dispatchers.IO) {
            account = exchangerInteractor.getBalance()
        }
    }

    fun updateAccountData(context: Context){
        if (topCurrency == bottomCurrency){
            showErrorMessage(context.getString(R.string.impossible_to_make_transaction_same))
            return
        }
        if (topValue == 0.0){
            showErrorMessage(context.getString(R.string.nothing_to_exchange))
            return
        }
        if (!(::account.isInitialized)){
            showErrorMessage(context.getString(R.string.not_possible))
            return
        }
        if (validateExchange(topValue)) {

            viewModelScope.launch(Dispatchers.IO) {

                val takerSymbol = getCurrencySymbol(bottomCurrency)

                prepareAccountForUpdate()

                val receiptText = "Receipt ${takerSymbol}${String.format(locale = Locale.ENGLISH, "%.2f", bottomValue)} to account ${bottomCurrency}.\n" +
                        "Available balance: ${takerSymbol}${String.format(locale = Locale.ENGLISH, "%.2f",getBalance(bottomCurrency) )} \n\n" +
                        "Available accounts:\n" +
                        "EUR: ${EUR_SYMBOL}${String.format(locale = Locale.ENGLISH, "%.2f",getBalance(EUR))}\n" +
                        "USD: ${USD_SYMBOL}${String.format(locale = Locale.ENGLISH, "%.2f",getBalance(USD))}\n" +
                        "GBP: ${GBP_SYMBOL}${String.format(locale = Locale.ENGLISH, "%.2f",getBalance(GBP))}\n"

                _receiptSender.emit(receiptText)

                exchangerInteractor.updateBalance(account)
                emitNewValues()
            }
        }else{
            showErrorMessage(context.getString(R.string.not_enough_money))
            return
        }
    }

    private fun validateExchange(inputExchange: Double): Boolean {
        val currentBalance = getBalance(topCurrency)
        return currentBalance >= inputExchange
    }

    private fun calculateScroll(isIncomeCards: Boolean){

        val topBasicRate = getBasicRate(topCurrency)
        val botBasicRate = getBasicRate(bottomCurrency)

        topRate = botBasicRate/topBasicRate
        bottomRate = topBasicRate/botBasicRate

        if (isIncomeCards){
            emitNewValues(newExpenseInput = topValue.toString())
        }
        else{
            emitNewValues(newIncomeInput = bottomValue.toString())
        }
    }


    private fun emitNewValues(
        newExpenseInput: String? = null,
        newIncomeInput: String? = null
    ){
        newExpenseInput?.let{
            val tempItem = topList[topActivePosition].copy(
                value = it.toDouble()
            )
            topList.removeAt(topActivePosition)
            topList.add(topActivePosition,tempItem)
        }

        newIncomeInput?.let{
            val tempItem = bottomList[bottomActivePosition].copy(
                value = it.toDouble()
            )
            bottomList.removeAt(bottomActivePosition)
            bottomList.add(bottomActivePosition,tempItem)
        }

        val topSymbol = getCurrencySymbol(topCurrency)
        val bottomSymbol = getCurrencySymbol(bottomCurrency)

        val newTopItem = if (newIncomeInput == null ){
            topList[topActivePosition].copy(
                exchangeRate = "${topSymbol}1 = ${bottomSymbol}${String.format(Locale.ENGLISH,"%.2f",topRate)}",
                balance = getBalance(topCurrency)
            )
        }  else{
                val inputBD = newIncomeInput.toBigDecimal()
                val rateBD = bottomRate.toBigDecimal()
                val newValue = inputBD.multiply(rateBD)
                bottomValue = newIncomeInput.toDouble()
                topValue = newValue.toDouble()

                topList[topActivePosition].copy(
                exchangeRate = "${topSymbol}1 = ${bottomSymbol}${String.format(Locale.ENGLISH,"%.2f",topRate)}",
                value = newValue.toDouble(),
                balance = getBalance(topCurrency)
            )
        }

        val topTempList = mutableListOf<CurrencyItem>()
        topTempList.addAll(topList)
        topTempList.removeAt(topActivePosition)
        topTempList.add(topActivePosition,newTopItem)

        topList = topTempList

        val newBottomItem = if(newExpenseInput == null){
            bottomList[bottomActivePosition].copy(
                exchangeRate = "${bottomSymbol}1 = ${topSymbol}${String.format(Locale.ENGLISH,"%.2f",bottomRate)}",
                balance = getBalance(bottomCurrency)
            )
        }
        else{
            val inputBD = newExpenseInput.toBigDecimal()
            val rateBD = topRate.toBigDecimal()
            val newValue = inputBD.multiply(rateBD)
            topValue = newExpenseInput.toDouble()
            bottomValue = newValue.toDouble()

            bottomList[bottomActivePosition].copy(
                exchangeRate = "${bottomSymbol}1 = ${topSymbol}${String.format(Locale.ENGLISH,"%.2f",bottomRate)}",
                value = newValue.toDouble(),
                balance = getBalance(bottomCurrency)
            )
        }

        val bottomTempList = mutableListOf<CurrencyItem>()
        bottomTempList.addAll(bottomList)
        bottomTempList.removeAt(bottomActivePosition)
        bottomTempList.add(bottomActivePosition,newBottomItem)

        bottomList = bottomTempList

        _exchangerScreenState.postValue(ExchangerScreenState.Content(
            topTempList,
            bottomTempList,
            "${topSymbol}1 = ${bottomSymbol}${String.format(Locale.ENGLISH,"%.2f",topRate)}"
        ))
    }

    fun manageCardsScroll(position: Int, isIncomeCards: Boolean){
        if (isIncomeCards){
            bottomActivePosition = position
            bottomCurrency = bottomList[position].name
            calculateScroll(true)
        }
        else{
            topActivePosition = position
            topCurrency = topList[position].name
            calculateScroll(false)
        }
    }

    fun manageCardTextChange(input: String, isIncomeCard: Boolean){
        if (input.isBlank()) return
        if (isIncomeCard){
            emitNewValues(newIncomeInput = input)
        }
        else{
            emitNewValues(newExpenseInput = input)
        }
    }

    private fun getCurrencySymbol(currencyName: String): String{
        return when(currencyName){
            USD -> USD_SYMBOL
            EUR -> EUR_SYMBOL
            else -> GBP_SYMBOL
        }
    }

    private fun getBasicRate(currencyName: String): Double{
        return when(currencyName){
            USD -> usdRate
            EUR -> eurRate
            else -> gbpRate
        }
    }

    private fun getBalance(currencyName: String): Double{
        return when(currencyName){
            USD -> account.usdBalance
            EUR -> account.eurBalance
            else -> account.gbpBalance
        }
    }

    private fun prepareAccountForUpdate(){
        account = when(topCurrency){
            USD -> {
                val value = getBalance(USD) - topValue
                account.copy(usdBalance = value )
            }
            EUR -> {
                val value = getBalance(EUR) - topValue
                account.copy(eurBalance = value)
            }
            else -> {
                val value = getBalance(GBP) - topValue
                account.copy(gbpBalance = value)
            }
        }

        account = when(bottomCurrency){
            USD -> {
                val value = getBalance(USD) + bottomValue
                account.copy(usdBalance = value )
            }
            EUR -> {
                val value = getBalance(EUR) + bottomValue
                account.copy(eurBalance = value )
            }
            else -> {
                val value = getBalance(GBP) + bottomValue
                account.copy(gbpBalance = value)
            }
        }
    }

    private fun showErrorMessage(message: String){
        viewModelScope.launch {
            _errorMessageSender.emit(message)
        }
    }



    companion object {
        const val EUR = "EUR"
        const val USD = "USD"
        const val GBP = "GBP"
        const val USD_SYMBOL ="$"
        const val EUR_SYMBOL ="€"
        const val GBP_SYMBOL ="£"
        const val UPDATE_DELAY = 30000L
    }
}