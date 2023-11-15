package com.szkarad.szkaradapp.shoppinglist

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.szkarad.szkaradapp.shoppinglist.productdb.Product
import com.szkarad.szkaradapp.shoppinglist.productdb.ProductViewModel
import com.szkarad.szkaradapp.shoppinglist.ui.theme.SzkaradAppTheme
import java.math.BigDecimal


class ShoppingList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SzkaradAppTheme {
                val pvm = ProductViewModel(application)
                pvm.insertProduct(Product(1, "Mleko", BigDecimal("2.5"), 1, false))
                pvm.insertProduct(Product(2, "Chleb", BigDecimal("1.5"), 2, true))
                pvm.insertProduct(Product(3, "Jajka", BigDecimal("3.0"), 12, true))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProductsList(pvm)
                }
            }
        }
    }
}

@Composable
fun ProductsList(pvm: ProductViewModel, modifier: Modifier = Modifier) {
    val products by pvm.products.collectAsState(emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .requiredHeight(400.dp)
                .fillMaxWidth()
        ) {
            items(products) { product ->
                ProductRow(product,
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun ProductRow(product: Product, modifier: Modifier) {
    Row(modifier = modifier) {
        println(modifier)
        Text(text = product.name)
    }
}

@Preview(showBackground = true)
@Composable
fun ProductsListPreview() {
    val fakeProductViewModel = ProductViewModel(Application())
    fakeProductViewModel.insertProduct(Product(1, "Mleko", BigDecimal("2.5"), 1, false))
    fakeProductViewModel.insertProduct(Product(2, "Chleb", BigDecimal("1.5"), 2, true))
    fakeProductViewModel.insertProduct(Product(3, "Jajka", BigDecimal("3.0"), 12, true))

    SzkaradAppTheme {
        ProductsList(fakeProductViewModel)
    }
}