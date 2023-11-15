package com.szkarad.szkaradapp.shoppinglist.productdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: BigDecimal,
    val count: Int,
    var status: Boolean
)
