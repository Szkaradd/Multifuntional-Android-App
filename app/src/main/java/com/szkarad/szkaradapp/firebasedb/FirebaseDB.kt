package com.szkarad.szkaradapp.firebasedb

import com.google.firebase.database.FirebaseDatabase
import com.szkarad.szkaradapp.firebasedb.productdb.ProductDAO
import com.szkarad.szkaradapp.firebasedb.shopdb.ShopDAO

class FirebaseDB(private val userPath: String) {
    private val database = FirebaseDatabase.getInstance("https://szkaradapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private val productsDbRef = database.getReference("products/$userPath")
    private val shopsDbRef = database.getReference("shops/$userPath")

    companion object {
        private var instance: FirebaseDB? = null

        @Synchronized
        fun getInstance(userPath: String): FirebaseDB {
            if (instance == null || (instance?.userPath != userPath)) {
                instance = FirebaseDB(userPath)
            }
            return instance!!
        }
    }

    fun getProductDAO(): ProductDAO {
        return ProductDAO(productsDbRef)
    }


    fun getShopDAO(): ShopDAO {
        return ShopDAO(shopsDbRef)
    }
}