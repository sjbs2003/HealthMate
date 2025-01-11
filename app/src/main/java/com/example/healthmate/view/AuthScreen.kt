package com.example.healthmate.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmate.R
import com.example.healthmate.viewmodel.AuthState
import com.example.healthmate.viewmodel.AuthViewModel


@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val authState by viewModel.authState.collectAsState()
    var showSignInDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var showOTPDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Success -> {
                isLoading = false
                errorMessage = null
                showOTPDialog = false
                onLoginSuccess()
            }
            is AuthState.OtpSent -> {
                isLoading = false
                errorMessage = null
                showOTPDialog = true
                showSignInDialog = false
            }
            is AuthState.Error -> {
                isLoading = false
                errorMessage = (authState as AuthState.Error).message
            }
            is AuthState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            AuthState.LoggedOut -> {
                isLoading = false
                errorMessage = null
                showSignInDialog = false
                showSignUpDialog = false
                showOTPDialog = false
                viewModel.resetAuthState() // Reset to initial state after handling logout
            }
            AuthState.Initial -> {
                isLoading = false
                errorMessage = null
            }
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
                .size(400.dp),
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

    // SignIn Dialog
    if (showSignInDialog) {
        SignInDialog(
            onDismiss = {
                showSignInDialog = false
                errorMessage = null
                viewModel.resetAuthState()
            },
            onSubmit = { phone ->
                if (validatePhoneNumber(phone)) {
                    viewModel.login(phone)
                } else {
                    errorMessage = "Please Enter A Valid Number"
                }
            },
            error = errorMessage,
            isLoading = isLoading
        )
    }

    // SignUp Dialog
    if (showSignUpDialog) {
        SignUpDialog(
            onDismiss = {
                showSignUpDialog = false
                errorMessage = null
                viewModel.resetAuthState()
            },
            onSubmit = { name, phone, email ->
                when {
                    name.length < 2 -> errorMessage = "Name must be at least 2 characters"
                    !validatePhoneNumber(phone) -> errorMessage = "Please enter a valid phone number"
                    email != null && !validateEmail(email) -> errorMessage = "Please enter a valid email or leave it empty"
                    else -> {
                        viewModel.signUp(name, phone, email)
                    }
                }
            },
            error = errorMessage,
            isLoading = isLoading
        )
    }

    // OTP Dialog
    if (showOTPDialog) {
        OTPDialog(
            onDismiss = {
                showOTPDialog = false
                errorMessage = null
                viewModel.resetAuthState()
            },
            onSubmit = { otp ->
                if (validateOTP(otp)) {
                    viewModel.verifyOTP(otp)
                } else {
                    errorMessage = "Please enter a valid OTP"
                }
            },
            error = errorMessage,
            isLoading = isLoading
        )
    }
}


@Composable
fun SignInDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    error: String? = null,
    isLoading: Boolean = false
) {
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = modifier.height(24.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone number") },
                    modifier = modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Phone"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = error != null
                )
                ErrorText(error = error, modifier = modifier)
                Spacer(modifier = modifier.height(24.dp))
                LoadingButton(
                    onClick = { onSubmit(phone) },
                    enabled = phone.isNotBlank(),
                    isLoading = isLoading,
                    text = "Continue",
                    modifier = modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SignUpDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String?) -> Unit,
    error: String? = null,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name"
                        )
                    },
                    singleLine = true,
                    isError = error != null
                )
                Spacer(modifier = modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Phone"
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = error != null
                )
                Spacer(modifier = modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Id") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "email"
                        )
                    },
                    modifier = modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                ErrorText(error = error, modifier = modifier)
                Spacer(modifier = modifier.height(24.dp))
                LoadingButton(
                    onClick = { onSubmit(name, phone, email.takeIf { it.isNotBlank() }) },
                    enabled = name.isNotBlank() && phone.isNotBlank(),
                    isLoading = isLoading,
                    text = "SignUp",
                    modifier = modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun OTPDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    error: String? = null,
    isLoading: Boolean = false
) {
    var otp by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter OTP",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We've sent an OTP to your phone",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = modifier.height(24.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("OTP") },
                    modifier = modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = error != null,
                    enabled = !isLoading

                )
                ErrorText(error = error, modifier = modifier)
                Spacer(modifier = modifier.height(24.dp))
                LoadingButton(
                    onClick = {
                        if (otp.isBlank()) return@LoadingButton
                        onSubmit(otp)
                    },
                    enabled = otp.isNotBlank(),
                    isLoading = isLoading,
                    text = "Verify",
                    modifier = modifier.fillMaxWidth()
                )
            }
        }
    }
}


private fun validatePhoneNumber(phone: String): Boolean {
    return phone.length >= 10 && phone.all { it.isDigit() }
}

private fun validateEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun validateOTP(otp: String): Boolean {
    return otp.length == 4 && otp.all { it.isDigit() }  // Assuming 4-digit OTP
}

@Composable
private fun LoadingButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text)
        }
    }
}

@Composable
private fun ErrorText(
    error: String?,
    modifier: Modifier = Modifier
) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier.padding(top = 4.dp)
        )
    }
}