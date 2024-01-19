package com.szkarad.szkaradapp.firebasedb.productdb

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductDAO(private val db: DatabaseReference) {
    fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                trySend(items).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        db.addValueEventListener(listener)
        awaitClose { db.removeEventListener(listener) }
    }

    fun getProductById(productId: String): Flow<Product?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Product::class.java)
                trySend(item).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        db.child(productId).addValueEventListener(listener)
        awaitClose { db.child(productId).removeEventListener(listener) }
    }

    suspend fun insertProduct(product: Product): String {
        val pushRef = db.push()
        pushRef.setValue(product).await()
        return pushRef.key ?: ""
    }

    suspend fun updateProduct(product: Product) {
        product.id.takeIf { it.isNotBlank() }?.let { id ->
            db.child(id).setValue(product).await()
        }
    }

    suspend fun deleteProduct(product: Product) {
        product.id.takeIf { it.isNotBlank() }?.let { id ->
            db.child(id).removeValue().await()
        }
    }

    suspend fun deleteAllProducts() {
        db.removeValue().await()
    }

}