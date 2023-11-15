package com.szkarad.szkaradapp.shoppinglist.productdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.szkarad.szkaradapp.shoppinglist.utils.BigDecimalConverter

@Database(entities = [Product::class], version = 1, exportSchema = false)
@TypeConverters(BigDecimalConverter::class)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDAO(): ProductDAO

    companion object {
        private var instance: ProductDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): ProductDatabase {
            if (instance != null)
                return instance as ProductDatabase
            instance = Room.databaseBuilder(
                context,
                ProductDatabase::class.java,
                "Products database"
            ).build()
            return instance as ProductDatabase
        }
    }
}