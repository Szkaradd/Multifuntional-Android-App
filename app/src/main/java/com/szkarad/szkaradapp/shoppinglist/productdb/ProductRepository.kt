package com.szkarad.szkaradapp.shoppinglist.productdb

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDAO: ProductDAO) {
    val allProducts: Flow<List<Product>> = productDAO.getProducts()

    suspend fun insert(product: Product) = productDAO.insertProduct(product)
    suspend fun update(product: Product) = productDAO.updateProduct(product)
    suspend fun delete(product: Product) = productDAO.deleteProduct(product)
}