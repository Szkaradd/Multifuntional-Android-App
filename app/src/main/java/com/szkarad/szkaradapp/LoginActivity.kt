package com.szkarad.szkaradapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            SzkaradAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting3(auth)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting3(auth: FirebaseAuth, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var displayTextPass by remember { mutableStateOf("password") }
    var displayTextLogin by remember { mutableStateOf("email") }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = displayTextLogin,
            onValueChange = {
                displayTextLogin = it
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.requiredHeight(10.dp))
        TextField(
            value = displayTextPass,
            onValueChange = {
                displayTextPass = it
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.requiredHeight(20.dp))
        Button( // TODO
            onClick = {
                auth.createUserWithEmailAndPassword(
                    displayTextLogin,
                    displayTextPass
                ).addOnCompleteListener {
                    if (it.isSuccessful) Toast.makeText(context, "Registered!", Toast.LENGTH_LONG).show()
                    else Toast.makeText(context, "Nah", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .requiredHeight(50.dp)
                .requiredWidth(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.requiredHeight(10.dp))
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(displayTextLogin, displayTextPass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) context.startActivity(Intent(context, MainActivity::class.java))
                    }
            },
            modifier = Modifier
                .requiredHeight(50.dp)
                .requiredWidth(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Login")
        }
    }
}
