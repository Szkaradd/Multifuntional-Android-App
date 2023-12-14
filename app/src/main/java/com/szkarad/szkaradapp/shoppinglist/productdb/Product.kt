package com.szkarad.szkaradapp.shoppinglist.productdb

data class Product(
    var id: String,
    val name: String,
    val price: String,
    val count: Int,
    val status: Boolean
) {
    constructor(): this("","","0",0,false)
}
