package com.szkarad.szkaradapp.shoppinglist.productdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(app: Application) : AndroidViewModel(app) {

    private val productRepository: ProductRepository
    val products: Flow<List<Product>>

    init {
        val productDAO = ProductDatabase.getDatabase(app).productDAO()
        productRepository = ProductRepository(productDAO)
        products = productRepository.allProducts
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            productRepository.insert(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.update(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.delete(product)
        }
    }

}