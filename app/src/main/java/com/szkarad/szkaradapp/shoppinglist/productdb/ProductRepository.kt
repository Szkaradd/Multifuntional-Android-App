package com.szkarad.szkaradapp.shoppinglist.productdb

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDAO: ProductDAO) {
    val allProducts: Flow<List<Product>> = productDAO.getProducts()

    suspend fun getProductById(productId: Long): Product {
        return productDAO.getProductById(productId)
    }

    suspend fun insert(product: Product): Long = productDAO.insertProduct(product)
    suspend fun update(product: Product) = productDAO.updateProduct(product)
    suspend fun delete(product: Product) = productDAO.deleteProduct(product)
    suspend fun deleteAll() = productDAO.deleteAllProducts()
}