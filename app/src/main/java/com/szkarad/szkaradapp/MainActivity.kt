package com.szkarad.szkaradapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szkarad.szkaradapp.common.CommonComposables
import com.szkarad.szkaradapp.shoppinglist.ShoppingList
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SzkaradAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BackgroundImage {
                        MainMenu("User")
                    }
                }
            }
        }
    }
}


@Composable
fun BackgroundImage(content: @Composable () -> Unit) {
    val painter = painterResource(id = R.drawable.background)
    Image(
        painter = painter,
        contentDescription = "Background image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    content()
}

@Composable
fun MainMenu(name: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        CommonComposables.WelcomeText("Hello Dear $name!", Color.Black)
        Spacer(modifier = Modifier.height(50.dp))
        SwitchActivityButton(context, ShoppingList::class.java)
    }
}

@Composable
fun SwitchActivityButton(context: Context, activityClass: Class<*>) {
    val buttonColors = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    )
    Button(
        onClick = { context.startActivity(Intent(context, activityClass)) },
        modifier = Modifier
            .requiredHeight(90.dp)
            .requiredWidth(250.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(60),
                clip = false,
            ),
        colors = buttonColors,
        shape = RoundedCornerShape(60)
    ) {
        Text(
            text = activityClass.simpleName,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(4f, 4f),
                    blurRadius = 4f
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    SzkaradAppTheme {
        MainMenu("User")
    }
}