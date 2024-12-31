package com.example.healthmate.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmate.R
import com.example.healthmate.viewmodel.AuthState
import com.example.healthmate.viewmodel.AuthViewModel


@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val authState by viewModel.authState.collectAsState()
    var showSignInDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var showOTPDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Success -> onLoginSuccess()
            is AuthState.Error -> TODO()
            AuthState.Initial -> TODO()
            AuthState.Loading -> TODO()
            is AuthState.OtpSent -> showOTPDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = modifier
                .size(120.dp)
                .clip(RectangleShape)
                .background(colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = modifier.fillMaxSize()
            )
        }

        Spacer(modifier = modifier.height(24.dp))

        Text(
            text = "HealthMate",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = modifier.height(8.dp))

        Text(
            text = "Your personal Health Companion\nManage Your Health Journey",
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = modifier.height(48.dp))

        // SignIn Button
        Button(
            onClick = { showSignInDialog = true },
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = modifier.height(16.dp))

        // SignUp Button
        Button(
            onClick = { showSignUpDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, colorScheme.primary)
        ) {
            Text(
                text = "Create Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

}


@Composable
fun SignInDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                
            ) {  }
        }
    }
}