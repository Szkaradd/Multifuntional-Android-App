package com.szkarad.szkaradapp.firebasedb.productdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.szkarad.szkaradapp.firebasedb.FirebaseDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(app : Application, userPath: String) : AndroidViewModel(app) {

    private val firebaseDB: FirebaseDB
    private val productDAO: ProductDAO
    private val productRepository: ProductRepository
    val products: Flow<List<Product>>

    init {
        firebaseDB = FirebaseDB.getInstance(userPath)
        productDAO = firebaseDB.getProductDAO()
        productRepository = ProductRepository(productDAO)
        products = productRepository.allProducts
    }


    fun getProductById(productId: String, onProductReceived: (Product?) -> Unit) {
        viewModelScope.launch {
            productRepository.getProductById(productId)
                .collect { product ->
                    onProductReceived(product)
                }
        }
    }

    fun insertProduct(product: Product, onResult: (String) -> Unit) {
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