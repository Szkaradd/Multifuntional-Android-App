package com.szkarad.szkaradapp.shoppinglist

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.szkarad.szkaradapp.common.CommonComposables
import com.szkarad.szkaradapp.shoppinglist.productdb.Product
import com.szkarad.szkaradapp.shoppinglist.productdb.ProductViewModel
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme
import java.math.BigDecimal


class ShoppingList : ComponentActivity() {
    var selectedProductId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        val testString = intent.getStringExtra("TEST_STRING")
        println("RECEIVED STRING TEST: $testString")

        setContent {
            SzkaradAppTheme {
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user!!.uid
                val pvm = ProductViewModel(application, uid)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    ShoppingListScreen(pvm, selectedProductId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        selectedProductId = intent.getStringExtra("com.szkarad.receiverapp.productId")
        println("RECEIVED SOME COOOL PRODUCT ID: $selectedProductId")
    }
}


@Composable
fun ShoppingListScreen(pvm: ProductViewModel, selectedProductId: String?) {
    Column {
        CommonComposables.CommonTopBar()
        ProductsListColumn(pvm, selectedProductId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListColumn(pvm: ProductViewModel, selectedProductId: String?) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        bottomBar = { ListManagementRow(pvm) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            CommonComposables.WelcomeText(
                text = "Here is your shopping list!",
                MaterialTheme.colorScheme.onSecondary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProductsList(pvm, selectedProductId)
        }
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
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ListManagementRow(pvm: ProductViewModel) {
    val context = LocalContext.current
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
            },
        )
    }

    if (showAddItemDialog) {
        ProductDialog(
            title = "Add Item",
            initialName = "",
            initialCount = "",
            initialPrice = "",
            onConfirm = { name, count, price ->
                var product = Product(id = "", name = name, price = price, count = count.toInt(), status = false)
                pvm.insertProduct(product) { id ->
                    product.id = id
                    pvm.updateProduct(product)
                    val intent = Intent("com.szkarad.szkaradapp.ACTION_SEND")
                    intent.putExtra("com.szkarad.szkaradapp.productId", id)
                    intent.putExtra("com.szkarad.szkaradapp.productName", name)
                    intent.component = ComponentName("com.szkarad.receiverapp", "com.szkarad.receiverapp.ProductReceiver")
                    context.sendBroadcast(intent)
                }
                showAddItemDialog = false
            },
            onDismiss = { showAddItemDialog = false }
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
fun ProductsList(pvm: ProductViewModel, selectedProductId: String?) {
    val products by pvm.products.collectAsState(emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(selectedProductId) {
        if (selectedProductId != null && selectedProductId != "") {
            pvm.getProductById(selectedProductId) { product ->
                selectedProduct = product
            }
        }
    }

    if (selectedProduct != null) {
        EditProductDialog(product = selectedProduct!!, onDismiss = { selectedProduct = null }, pvm = pvm)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        items(products) { product ->
            ProductRow(product, pvm, onEditClick = { selectedProduct = product })
        }
    }
}

@Composable
fun ProductRow(product: Product, pvm: ProductViewModel, onEditClick: () -> Unit) {
    var status by remember { mutableStateOf(product.status) }

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
            Checkbox(
                checked = status,
                onCheckedChange = {
                    status = it
                    pvm.updateProduct(product.copy(status = status))
                }
            )
            ProductDescription(product, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
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
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "total price: ${BigDecimal(product.price) * BigDecimal(product.count)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    title: String,
    initialName: String,
    initialCount: String,
    initialPrice: String,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var count by remember { mutableStateOf(initialCount) }
    var price by remember { mutableStateOf(initialPrice) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text =  {
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || name.length > 50) {
                        errorMessage = "Invalid name"
                    } else if (count.toIntOrNull() == null || count.toInt() <= 0) {
                        errorMessage = "Invalid count"
                    } else if (price.toBigDecimalOrNull() == null || price.toBigDecimal() < BigDecimal.ZERO) {
                        errorMessage = "Invalid price"
                    } else {
                        onConfirm(name, count, price)
                    }
                }
            ) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun EditProductDialog(product: Product, onDismiss: () -> Unit, pvm: ProductViewModel) {
    ProductDialog(
        title = "Edit Product",
        initialName = product.name,
        initialCount = product.count.toString(),
        initialPrice = product.price,
        onConfirm = { newName, newCount, newPrice ->
            pvm.updateProduct(product.copy(name = newName, count = newCount.toInt(), price = newPrice))
            onDismiss()
        },
        onDismiss = onDismiss
    )
}