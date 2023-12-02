package com.szkarad.szkaradapp.shoppinglist.productdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO {

    @Query("SELECT * FROM product")
    fun getProducts(): Flow<List<Product>>

    @Query("SELECT * FROM Product WHERE id = :productId")
    suspend fun getProductById(productId: Long): Product

    @Insert
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM product")
    suspend fun deleteAllProducts()
}