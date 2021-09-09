package com.hieuwu.groceriesstore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hieuwu.groceriesstore.R
import com.hieuwu.groceriesstore.databinding.FragmentCartBinding
import com.hieuwu.groceriesstore.di.ProductRepo
import com.hieuwu.groceriesstore.domain.entities.OrderWithLineItems
import com.hieuwu.groceriesstore.domain.repository.OrderRepository
import com.hieuwu.groceriesstore.domain.repository.ProductRepository
import com.hieuwu.groceriesstore.presentation.adapters.LineListItemAdapter
import com.hieuwu.groceriesstore.presentation.viewmodels.CartViewModel
import com.hieuwu.groceriesstore.presentation.viewmodels.factory.CartViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCartBinding

    @ProductRepo
    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var orderRepository: OrderRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCartBinding>(
            inflater, R.layout.fragment_cart, container, false
        )

        val viewModelFactory = CartViewModelFactory(productRepository, orderRepository)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = LineListItemAdapter(
            LineListItemAdapter.OnClickListener(
                minusListener = { viewModel.decreaseQty(it) },
                plusListener = { viewModel.increaseQty(it) }
            )
        )

        binding.cartDetailRecyclerview.adapter = adapter

        viewModel.totalPrice.observe(viewLifecycleOwner, {
            binding.total.text = it.toString()
        })

        viewModel.order.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.sumPrice()
            }
        })

        binding.checkoutButton.setOnClickListener {
            val direction =
                ShopFragmentDirections.actionShopFragmentToCheckOutFragment2(
                    viewModel.order.value?.order?.id as String
                )
            this.findNavController().navigate(direction)
            dismiss();
        }
        return binding.root
    }

}