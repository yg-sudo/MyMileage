/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.yg.mileage.auth

// Added imports for Microsoft OAuth sign-in
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yg.mileage.R
import com.yg.mileage.ui.theme.MyMileage
import com.yg.mileage.ui.theme.robotoFlexTopAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInScreen(
    onEmailSignInClick: (String, String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onMicrosoftSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneLoginMessage by remember { mutableStateOf<String?>(null) }

    LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MyMileage) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentDescription = "MyMileage Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Log in", fontFamily = robotoFlexTopAppBar, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Text("Sign in to continue.", fontFamily = robotoFlexTopAppBar, fontSize = 16.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onEmailSignInClick(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MyMileage)
                    ) {
                        Text("Log in")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("OR", fontFamily = robotoFlexTopAppBar, fontWeight = FontWeight.Bold, color = MyMileage)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        OutlinedIconButton(
                            onClick = onGoogleSignInClick,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MyMileage),
                            shape = MaterialShapes.Square.toShape()
                        ) {
                            Image(
                                painterResource(id = R.drawable.google_2025_g_logo),
                                contentDescription = "Google"
                            )
                        }
                        OutlinedIconButton(
                            onClick = { phoneLoginMessage = "Phone Login Under Development" },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White),
                            shape = MaterialShapes.Square.toShape(),
                        ) {
                            Image(
                                imageVector = Icons.Rounded.Call,
                                contentDescription = "Phone",
                            )
                        }
                        // Microsoft sign-in button in SignInScreen.kt
                        OutlinedIconButton(
                            onClick = onMicrosoftSignInClick,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White),
                            shape = MaterialShapes.Square.toShape(),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.microsoft_logo),
                                contentDescription = "Microsoft",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(2.dp)
                            )
                        }
                    }
                    phoneLoginMessage?.let {
                        Text(
                            text = it,
                            color = MyMileage,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { onForgotPasswordClick(email) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(contentColor = MyMileage)
                    ) {
                        Text(
                            "Forgot Password?",
                        )
                    }
                    TextButton(
                        onClick = { onSignUpClick() },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(contentColor = MyMileage)
                    ) {
                        Text("Don't have an account? Sign up")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        onEmailSignInClick = { _, _ -> },
        onGoogleSignInClick = {},
        onMicrosoftSignInClick = {},
        onSignUpClick = {},
        onForgotPasswordClick = {}
    )
}
