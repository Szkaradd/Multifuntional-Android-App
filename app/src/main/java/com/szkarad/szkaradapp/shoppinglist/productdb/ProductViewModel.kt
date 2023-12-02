package com.szkarad.szkaradapp.shoppinglist.productdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(app: Application) : AndroidViewModel(app) {

    private val productRepository: ProductRepository
    val products: Flow<List<Product>>

    fun getProductById(productId: Long): Product? {
        var product: Product? = null
        viewModelScope.launch {
            product = productRepository.getProductById(productId)
        }
        return product
    }

    init {
        val productDAO = ProductDatabase.getDatabase(app).productDAO()
        productRepository = ProductRepository(productDAO)
        products = productRepository.allProducts
    }

    fun insertProduct(product: Product, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = productRepository.insert(product)
            onResult(id)
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

    fun deleteAllProducts() {
        viewModelScope.launch {
            productRepository.deleteAll()
        }
    }

}