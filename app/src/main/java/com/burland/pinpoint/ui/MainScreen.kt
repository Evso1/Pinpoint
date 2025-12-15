package com.burland.pinpoint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.burland.pinpoint.MainViewModel
import com.burland.pinpoint.ui.theme.PrivacyOrange
import com.burland.pinpoint.ui.theme.PrivacyOrangeDim

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // --- Header ---
        Text(
            text = "PINPOINT", // Minimal branding
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- Input ---
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Enter Address") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = PrivacyOrange,
                cursorColor = PrivacyOrange,
                focusedLabelColor = PrivacyOrange
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // INCOGNITO MODE: Disable autocorrect to prevent keyboard from learning addresses
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search, 
                autoCorrect = false 
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                viewModel.searchLocation()
            })
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Action ---
        if (uiState.isLoading) {
            CircularProgressIndicator(color = PrivacyOrange)
        } else {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    viewModel.searchLocation()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrivacyOrange)
            ) {
                Text(
                    text = "ACQUIRE COORDINATES",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- Result Display ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Allow list to take up space
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else if (uiState.selectedResult != null) {
                // Show Single Selected Result (Big Coords)
                val result = uiState.selectedResult!!
                val coordsText = "${result.latitude}, ${result.longitude}"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        text = coordsText,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.clickable {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            clipboardManager.setText(AnnotatedString(coordsText))
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = result.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[ TAP COORDS TO COPY ]",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrivacyOrange
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Allow going back to list if multiple results exist
                    if (uiState.results.size > 1) {
                         Text(
                            text = "< BACK TO RESULTS",
                            color = Color.LightGray,
                            modifier = Modifier.clickable { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove) // Subtle tick
                                viewModel.clearSelection()
                             }
                        )
                    }
                }
            } else if (uiState.results.isNotEmpty()) {
                // Show List of Candidates (Scrollable)
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "MULTIPLE MATCHES FOUND", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = PrivacyOrange,
                            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    items(uiState.results.size) { index ->
                        val location = uiState.results[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                    viewModel.selectLocation(location) 
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = location.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${location.category} / ${location.type}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrivacyOrangeDim
                                )
                            }
                        }
                        // Divider
                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.DarkGray))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Kill Switch ---
        Text(
            text = "CLEAR TRACE",
            color = Color.DarkGray,
            modifier = Modifier
                .clickable { 
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    viewModel.clearAll() 
                }
                .padding(16.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
