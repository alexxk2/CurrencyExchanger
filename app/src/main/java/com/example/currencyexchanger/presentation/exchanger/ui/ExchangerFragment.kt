package com.example.currencyexchanger.presentation.exchanger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.currencyexchanger.R
import com.example.currencyexchanger.databinding.FragmentExchangerBinding
import com.example.currencyexchanger.presentation.exchanger.models.ExchangerScreenState
import com.example.currencyexchanger.presentation.exchanger.view_model.ExchangerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExchangerFragment : Fragment() {
    private var _binding: FragmentExchangerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExchangerViewModel by viewModels()

    private lateinit var expenseAdapter: CurrenciesAdapter
    private lateinit var incomeAdapter: CurrenciesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //requireContext().getSharedPreferences("shared_prefs",0).edit().clear().apply()

        setViewPagers()
        viewModel.getCurrencies()
        viewModel.getAccountData()
        showRatesUpdatedAnimation()

        binding.tvExchange.setOnClickListener {
            viewModel.updateAccountData(requireContext())
        }

        subscribeToViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startRatesUpdate()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRatesUpdate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToViewModel(){
        viewModel.exchangerScreenState.observe(viewLifecycleOwner) { state ->
            manageScreenContent(state)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessageSender.collect { message ->
                    showToast(message)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.receiptSender.collect { message ->
                    showReceiptDialog(message)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateSender.collect {
                    showRatesUpdatedAnimation()
                }
            }
        }
    }

    private fun setViewPagers() {

        expenseAdapter = CurrenciesAdapter(
            onExpenseTextChangeListener = {input->
                if (input.isNullOrBlank()){
                    viewModel.manageCardTextChange("0",false)
                }else{
                    viewModel.manageCardTextChange(input.toString(),false)
                }

            },
            isIncomeAdapter = false,
            context = requireContext()

        )

        incomeAdapter = CurrenciesAdapter(
            onIncomeTextChangeListener = {input->
                if (input.isNullOrBlank()){
                    viewModel.manageCardTextChange("0",true)
                }else{
                    viewModel.manageCardTextChange(input.toString(),true)
                }
            },
            isIncomeAdapter = true,
            context = requireContext()
        )
        binding.vpTopList.adapter = expenseAdapter
        binding.vpBottomList.adapter = incomeAdapter


        binding.vpTopList.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.manageCardsScroll(position,false)
            }
        })

        binding.vpBottomList.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.manageCardsScroll(position,true)
            }
        })

        binding.vpBottomList.setPageTransformer { _, _ -> }
        binding.vpTopList.setPageTransformer { _, _ -> }

    }


    private fun manageScreenContent(state: ExchangerScreenState) {
        with(binding) {
            when (state) {
                is ExchangerScreenState.Content -> {
                    tvCurrencyRate.visibility = View.VISIBLE
                    tvExchange.visibility = View.VISIBLE
                    vpTopList.visibility = View.VISIBLE
                    vpBottomList.visibility = View.VISIBLE
                    ivArrowDivider.visibility = View.VISIBLE
                    tvError.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    expenseAdapter.submitList(state.topList)
                    incomeAdapter.submitList(state.bottomList)
                    tvCurrencyRate.text = state.topRate
                }

                ExchangerScreenState.Error -> {
                    tvCurrencyRate.visibility = View.GONE
                    tvExchange.visibility = View.GONE
                    vpTopList.visibility = View.GONE
                    vpBottomList.visibility = View.GONE
                    ivArrowDivider.visibility = View.GONE
                    tvError.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }

                ExchangerScreenState.Loading -> {
                    tvCurrencyRate.visibility = View.GONE
                    tvExchange.visibility = View.GONE
                    vpTopList.visibility = View.GONE
                    vpBottomList.visibility = View.GONE
                    ivArrowDivider.visibility = View.GONE
                    tvError.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun showToast(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }

    private fun showReceiptDialog(message: String){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.receipt))
            .setMessage(message)
            .setBackground(ResourcesCompat.getDrawable(resources,R.drawable.dialog_background,null))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
    }

    private fun showRatesUpdatedAnimation() {
        lifecycleScope.launch {
            binding.updateProgressBar.visibility = View.VISIBLE
            delay(UPDATE_DURATION)
            binding.updateProgressBar.visibility = View.GONE
        }
    }

    companion object{
        const val UPDATE_DURATION = 4000L
    }
}