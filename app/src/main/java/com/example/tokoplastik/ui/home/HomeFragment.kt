package com.example.tokoplastik.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.tokoplastik.data.network.GetProductApi
import com.example.tokoplastik.data.repository.ProductRepository
import com.example.tokoplastik.data.responses.GetProduct
import com.example.tokoplastik.databinding.FragmentHomeBinding
import com.example.tokoplastik.ui.base.BaseFragment
import com.example.tokoplastik.util.Resource
import com.example.tokoplastik.util.visible
import com.example.tokoplastik.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeFragment : BaseFragment <HomeViewModel, FragmentHomeBinding, ProductRepository> () {

    private fun updateUI(product: List<GetProduct>?) {
        with(binding) {
            if (product != null) {
                idText.text = product.first().id.toString()
                nameText.text = product.first().name
                supplierText.text = product.first().supplier
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeProgressBar.visible(false)

        viewModel.getProduct()
        viewModel.product.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.homeProgressBar.visible(false)
                    updateUI(it.data?.data)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "API Error", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "API Error", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.homeProgressBar.visible(true)
                }
            }
        })
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): ProductRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(GetProductApi::class.java, token)
        return ProductRepository(api)
    }
}