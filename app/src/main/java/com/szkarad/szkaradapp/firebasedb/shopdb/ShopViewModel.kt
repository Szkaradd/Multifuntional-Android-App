package com.szkarad.szkaradapp.firebasedb.shopdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.szkarad.szkaradapp.firebasedb.FirebaseDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShopViewModel(app : Application, userPath: String) : AndroidViewModel(app) {

    private val firebaseDB: FirebaseDB
    private val shopDAO: ShopDAO
    private val shopRepository: ShopRepository
    val shops: Flow<List<Shop>>

    init {
        firebaseDB = FirebaseDB.getInstance(userPath)
        shopDAO = firebaseDB.getShopDAO()
        shopRepository = ShopRepository(shopDAO)
        shops = shopRepository.allShops
    }


    fun getShopById(shopId: String, onShopReceived: (Shop?) -> Unit) {
        viewModelScope.launch {
            shopRepository.getShopById(shopId)
                .collect { shop ->
                    onShopReceived(shop)
                }
        }
    }

    fun insertShop(shop: Shop, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val id = shopRepository.insert(shop)
            onResult(id)
        }
    }

    fun updateShop(shop: Shop) {
        viewModelScope.launch {
            shopRepository.update(shop)
        }
    }

    fun deleteShop(shop: Shop) {
        viewModelScope.launch {
            shopRepository.delete(shop)
        }
    }

    fun deleteAllShops() {
        viewModelScope.launch {
            shopRepository.deleteAll()
        }
    }

}