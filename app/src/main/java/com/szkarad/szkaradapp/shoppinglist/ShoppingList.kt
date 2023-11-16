package com.szkarad.szkaradapp.shoppinglist

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szkarad.szkaradapp.MainActivity
import com.szkarad.szkaradapp.common.CommonComposables
import com.szkarad.szkaradapp.shoppinglist.productdb.Product
import com.szkarad.szkaradapp.shoppinglist.productdb.ProductViewModel
import com.szkarad.szkaradapp.shoppinglist.ui.theme.Purple40
import com.szkarad.szkaradapp.shoppinglist.ui.theme.SzkaradAppTheme
import com.szkarad.szkaradapp.ui.theme.LightKolorek
import java.math.BigDecimal


class ShoppingList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SzkaradAppTheme {
                val pvm = ProductViewModel(application)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    ShoppingListScreen(pvm)
                }
            }
        }
    }
}

@Composable
fun ShoppingListScreen(pvm: ProductViewModel) {
    Column {
        ShoppingListTopBar(onSettingsClicked = {})
        ProductsListColumn(pvm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListTopBar(onSettingsClicked: () -> Unit) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text("Shopping List") },
        navigationIcon = {
            IconButton(onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            }) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
        },
        actions = {
            IconButton(onClick = onSettingsClicked) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun ProductsListColumn(pvm: ProductViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        CommonComposables.WelcomeText(text = "Here is your shopping list!", MaterialTheme.colorScheme.onSecondary)
        Spacer(modifier = Modifier.height(20.dp))
        ProductsList(pvm)
        Spacer(modifier = Modifier.weight(1f))
        ListManagementRow(pvm)
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        modifier = Modifier
            .padding(8.dp)
            .requiredWidth(160.dp)
            .requiredHeight(50.dp)
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListManagementRow(pvm: ProductViewModel) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Confirm Clear") },
            text = { Text("Are you sure that you want to clear the list?") },
            confirmButton = {
                Button(onClick = {
                    pvm.deleteAllProducts()
                    showClearConfirmDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showClearConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    var name by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Add Item") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    isError = name.isBlank() || name.length > 50,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = count,
                    onValueChange = { count = it },
                    label = { Text("Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = count.toIntOrNull() == null || count.toInt() <= 0,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Unit Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = price.toBigDecimalOrNull() == null || price.toBigDecimal() < BigDecimal.ZERO,
                    modifier = Modifier.fillMaxWidth()
                )
                errorMessage?.let { Text(it, color = Color.Red) }
            } },
            confirmButton = {
                Button(onClick = {
                    if (name.isBlank() || name.length > 50) {
                        errorMessage = "Invalid name"
                    } else if (count.toIntOrNull() == null || count.toInt() <= 0) {
                        errorMessage = "Invalid count"
                    } else if (price.toBigDecimalOrNull() == null || price.toBigDecimal() < BigDecimal.ZERO) {
                        errorMessage = "Invalid price"
                    } else {
                        pvm.insertProduct(Product(name = name, price = BigDecimal(price), count = count.toInt(), status = false))
                        showAddItemDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddItemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionButton(text = "Clear List", onClick = { showClearConfirmDialog = true })
        ActionButton(text = "Add Item", onClick = { showAddItemDialog = true })
    }
}


@Composable
fun ProductsList(pvm: ProductViewModel) {
    val products by pvm.products.collectAsState(emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    if (selectedProduct != null) {
        EditProductDialog(product = selectedProduct!!, onDismiss = { selectedProduct = null }, pvm = pvm)
    }

    LazyColumn(
        modifier = Modifier
            .requiredHeight(400.dp)
            .fillMaxWidth()
    ) {
        items(products) { product ->
            ProductRow(product, pvm, onEditClick = { selectedProduct = product })
        }
    }
}

@Composable
fun ProductRow(product: Product, pvm: ProductViewModel, onEditClick: () -> Unit) {
    // var count by remember { mutableStateOf(product.count.toString()) }

    Surface(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductDescription(product, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            // QuantityControl(product, count, pvm, onCountChange = { count = it })
            ProductSettings(product, pvm, onEditClick)
        }
    }
}

@Composable
fun ProductSettings(product: Product, pvm: ProductViewModel, onEditClick: () -> Unit) {
    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colorScheme.onPrimary)
    }
    IconButton(onClick = { pvm.deleteProduct(product) }, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun ProductDescription(product: Product, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "${product.name} x (${product.count})",
            style = TextStyle(
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "total price: ${product.price * BigDecimal(product.count)}",
            style = TextStyle(
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(product: Product, onDismiss: () -> Unit, pvm: ProductViewModel) {
    var newName by remember { mutableStateOf(product.name) }
    var newCount by remember { mutableStateOf(product.count.toString()) }
    var newPrice by remember { mutableStateOf(product.price.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Product") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    isError = newName.isBlank() || newName.length > 50,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = newCount,
                    onValueChange = { newCount = it },
                    label = { Text("Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = newCount.toIntOrNull() == null || newCount.toInt() <= 0,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = newPrice,
                    onValueChange = { newPrice = it },
                    label = { Text("Unit Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = newPrice.toBigDecimalOrNull() == null || newPrice.toBigDecimal() < BigDecimal.ZERO,
                    modifier = Modifier.fillMaxWidth()
                )
                errorMessage?.let { Text(it, color = Color.Red) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isBlank() || newName.length > 50) {
                        errorMessage = "Invalid name"
                    } else if (newCount.toIntOrNull() == null || newCount.toInt() <= 0) {
                        errorMessage = "Invalid count"
                    } else if (newPrice.toBigDecimalOrNull() == null || newPrice.toBigDecimal() < BigDecimal.ZERO) {
                        errorMessage = "Invalid price"
                    } else {
                        pvm.updateProduct(product.copy(name = newName, count = newCount.toInt(), price = BigDecimal(newPrice)))
                        onDismiss()
                    }
                }
            ) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}


/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantityControl(product: Product, count: String, pvm: ProductViewModel, onCountChange: (String) -> Unit) {
    var localCount by remember { mutableStateOf(count) }

    IconButton(onClick = {
        val newCount = (localCount.toIntOrNull() ?: 1) - 1
        if (newCount > 0) {
            localCount = newCount.toString()
            onCountChange(localCount)
            pvm.updateProduct(product.copy(count = newCount))
        }
    }) {
        Icon(Icons.Outlined.Close, contentDescription = "Decrease")
    }
    
    Text(text = localCount)

    IconButton(onClick = {
        val newCount = (localCount.toIntOrNull() ?: 1) + 1
        localCount = newCount.toString()
        onCountChange(localCount)
        pvm.updateProduct(product.copy(count = newCount))
    }) {
        Icon(Icons.Outlined.AddCircle, contentDescription = "Increase")
    }
}*/

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