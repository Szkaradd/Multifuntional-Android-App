package com.szkarad.szkaradapp.firebasedb.shopdb

data class Shop (
    var id: String,
    val name: String,
    val description: String,
    val radius: Double,
    val latitude: Double,
    val longitude: Double
) {
    constructor(): this("","","",0.0, 0.0, 0.0)

}
