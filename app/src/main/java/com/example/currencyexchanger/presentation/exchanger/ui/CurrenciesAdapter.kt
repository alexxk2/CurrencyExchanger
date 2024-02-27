package com.example.currencyexchanger.presentation.exchanger.ui

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchanger.R
import com.example.currencyexchanger.databinding.ExchangeCardItemBinding
import com.example.currencyexchanger.domain.models.CurrencyItem
import java.util.*

class CurrenciesAdapter(
    private val onExpenseTextChangeListener: (input: Editable?) -> Unit = {},
    private val onIncomeTextChangeListener: (input: Editable?) -> Unit = {},
    private val isIncomeAdapter: Boolean,
    private val context: Context
) : ListAdapter<CurrencyItem, CurrenciesAdapter.CurrencyViewHolder>(
    DiffCallBack
) {

    inner class CurrencyViewHolder(private val binding: ExchangeCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CurrencyItem,
            onExpenseTextChangeListener: (input: Editable?) -> Unit,
            onIncomeTextChangeListener: (input: Editable?) -> Unit,
            isIncomeAdapter: Boolean
        ) {
            with(binding) {

                val usdSymbol = context.getString(R.string.usd_symbol)
                val eurSymbol = context.getString(R.string.eur_symbol)
                val gbpSymbol = context.getString(R.string.gbp_symbol)


                tvCurrencyName.text = item.name

                when (item.name) {
                    USD -> {
                        tvYouHave.text =
                            context.getString(R.string.you_have, String.format(locale = Locale.ENGLISH,"%.2f", item.balance), usdSymbol)
                    }

                    EUR -> {
                        tvYouHave.text =
                            context.getString(R.string.you_have, String.format(locale = Locale.ENGLISH,"%.2f", item.balance), eurSymbol)
                    }

                    GBP -> {
                        tvYouHave.text =
                            context.getString(R.string.you_have, String.format(locale = Locale.ENGLISH,"%.2f", item.balance), gbpSymbol)
                    }
                }
                tvCurrencyRate.text = item.exchangeRate

                if (isIncomeAdapter) {
                    tiValueExpense.visibility = View.GONE
                    tvExpenseSign.visibility = View.GONE
                    tiValueIncome.visibility = View.VISIBLE

                    etValueIncome.addTextChangedListener {

                        if (it.isNullOrBlank()) {
                            tvIncomeSign.visibility = View.GONE
                        } else {
                            tvIncomeSign.visibility = View.VISIBLE
                        }
                        if (etValueIncome.hasFocus()) {
                            onIncomeTextChangeListener(it)
                        }

                        if ((it?.length ?: 0) > 7) {
                            tvCurrencyName.text = getCurrencySymbol(item.name)
                        } else {
                            tvCurrencyName.text = item.name
                        }
                    }

                    if (item.value != 0.0) {
                        val text = String.format(Locale.ENGLISH, "%.2f", item.value)
                        etValueIncome.setText(text)
                    } else {
                        etValueIncome.text?.clear()
                    }
                } else {
                    tiValueExpense.visibility = View.VISIBLE
                    tvIncomeSign.visibility = View.GONE
                    tiValueIncome.visibility = View.GONE

                    etValueExpense.addTextChangedListener {

                        if (it.isNullOrBlank()) {
                            tvExpenseSign.visibility = View.GONE
                        } else {
                            tvExpenseSign.visibility = View.VISIBLE
                        }

                        if (etValueExpense.hasFocus()) {
                            onExpenseTextChangeListener(it)
                        }

                        if ((it?.length ?: 0) > 7) {
                            tvCurrencyName.text = getCurrencySymbol(item.name)
                        } else {
                            tvCurrencyName.text = item.name
                        }

                    }

                    if (item.value != 0.0) {
                        etValueExpense.setText(String.format(Locale.ENGLISH, "%.2f", item.value))
                    } else {
                        etValueExpense.text?.clear()
                    }
                }






                etValueIncome.placeCursorToEnd()
                etValueExpense.placeCursorToEnd()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExchangeCardItemBinding.inflate(inflater, parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onExpenseTextChangeListener, onIncomeTextChangeListener, isIncomeAdapter)
    }

    private fun getCurrencySymbol(currencyName: String): String{
        return when(currencyName){
            USD -> USD_SYMBOL
            EUR -> EUR_SYMBOL
            else -> GBP_SYMBOL
        }
    }

    companion object {
        val DiffCallBack = object : DiffUtil.ItemCallback<CurrencyItem>() {

            override fun areItemsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
                return oldItem == newItem
            }
        }

        const val EUR = "EUR"
        const val USD = "USD"
        const val GBP = "GBP"

        const val USD_SYMBOL ="$"
        const val EUR_SYMBOL ="€"
        const val GBP_SYMBOL ="£"
    }

    fun EditText.placeCursorToEnd() {
        this.setSelection(this.text.length)
    }
}