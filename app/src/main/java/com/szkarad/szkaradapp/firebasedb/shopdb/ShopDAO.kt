package com.szkarad.szkaradapp.firebasedb.shopdb

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ShopDAO(private val db: DatabaseReference) {
    fun getShops(): Flow<List<Shop>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(Shop::class.java) }
                trySend(items).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        db.addValueEventListener(listener)
        awaitClose { db.removeEventListener(listener) }
    }

    fun getShopById(shopId: String): Flow<Shop?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Shop::class.java)
                trySend(item).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        db.child(shopId).addValueEventListener(listener)
        awaitClose { db.child(shopId).removeEventListener(listener) }
    }

    suspend fun insertShop(shop: Shop): String {
        val pushRef = db.push()
        pushRef.setValue(shop).await()
        return pushRef.key ?: ""
    }

    suspend fun updateShop(shop: Shop) {
        shop.id.takeIf { it.isNotBlank() }?.let { id ->
            db.child(id).setValue(shop).await()
        }
    }

    suspend fun deleteShop(shop: Shop) {
        shop.id.takeIf { it.isNotBlank() }?.let { id ->
            db.child(id).removeValue().await()
        }
    }

    suspend fun deleteAllShops() {
        db.removeValue().await()
    }
}