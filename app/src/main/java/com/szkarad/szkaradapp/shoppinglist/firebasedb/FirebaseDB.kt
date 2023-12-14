package com.szkarad.szkaradapp.shoppinglist.firebasedb

import com.google.firebase.database.FirebaseDatabase
import com.szkarad.szkaradapp.shoppinglist.productdb.ProductDAO

class FirebaseDB(private val userPath: String) {
    private val database = FirebaseDatabase.getInstance("https://szkaradapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private val dbRef = database.getReference("products/$userPath")

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
        return ProductDAO(dbRef)
    }
}