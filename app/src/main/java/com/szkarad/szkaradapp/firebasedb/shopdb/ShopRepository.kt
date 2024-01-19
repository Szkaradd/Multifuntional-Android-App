package com.szkarad.szkaradapp.firebasedb.shopdb

import kotlinx.coroutines.flow.Flow

class ShopRepository(private val shopDAO: ShopDAO) {
    val allShops: Flow<List<Shop>> = shopDAO.getShops()

    fun getShopById(shopId: String): Flow<Shop?> {
        return shopDAO.getShopById(shopId)
    }

    suspend fun insert(shop: Shop): String = shopDAO.insertShop(shop)
    suspend fun update(shop: Shop) = shopDAO.updateShop(shop)
    suspend fun delete(shop: Shop) = shopDAO.deleteShop(shop)
    suspend fun deleteAll() = shopDAO.deleteAllShops()
}