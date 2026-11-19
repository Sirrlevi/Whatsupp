package com.sirrlevi.whatsupp.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shows the update state and lets the user save a token, check, download and install. */
@Composable
fun UpdateDialog(controller: UpdateController, onDismiss: () -> Unit) {
    val state by controller.state.collectAsState()
    var token by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { controller.check() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App updates") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Installed: ${controller.installedVersionName} (build ${controller.installedVersionCode})",
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(10.dp))
                when (val s = state) {
                    is UpdateUiState.NotConfigured -> {
                        Text(
                            "This app's updates live in a private GitHub repository. Paste a fine-grained GitHub token " +
                                "(Resource owner: ${UpdateRules.OWNER}, only the ${UpdateRules.REPOSITORY} repository, " +
                                "Contents: Read-only). It is stored encrypted on this phone and only sent to GitHub.",
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = token,
                            onValueChange = { token = it },
                            label = { Text("GitHub token") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is UpdateUiState.Idle -> Text("Tap Check now to look for a new version.", fontSize = 13.sp)
                    is UpdateUiState.Checking -> {
                        Text("Checking for updates…", fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    is UpdateUiState.UpToDate -> Text("You already have the latest version.", fontSize = 13.sp)
                    is UpdateUiState.CheckFailed -> Text(s.message, fontSize = 13.sp)
                    is UpdateUiState.Available -> {
                        Text("New version ${s.info.versionName} is available.", fontSize = 14.sp)
                        val size = UpdateRules.formatSize(s.info.sizeBytes)
                        if (size.isNotEmpty()) Text("Download size: $size", fontSize = 12.sp)
                        if (s.info.notes.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text("What's new", fontSize = 12.sp)
                            Text(s.info.notes, fontSize = 13.sp)
                        }
                        if (s.error != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(s.error, fontSize = 13.sp)
                        }
                    }
                    is UpdateUiState.Downloading -> {
                        Text("Downloading ${s.info.versionName}…", fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        if (s.total > 0L) {
                            val fraction = (s.downloaded.toFloat() / s.total.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth())
                        } else {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    is UpdateUiState.ReadyToInstall -> {
                        Text("Version ${s.info.versionName} is downloaded.", fontSize = 14.sp)
                        if (!s.message.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(s.message, fontSize = 13.sp)
                        }
                    }
                }
                if (state !is UpdateUiState.NotConfigured) {
                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = { controller.clearToken() }) { Text("Remove saved token") }
                }
            }
        },
        confirmButton = {
            when (state) {
                is UpdateUiState.NotConfigured -> TextButton(
                    onClick = {
                        if (token.isNotBlank()) {
                            controller.saveToken(token)
                            token = ""
                        }
                    },
                    enabled = token.isNotBlank()
                ) { Text("Save token") }
                is UpdateUiState.Available -> TextButton(onClick = { controller.downloadAndInstall() }) { Text("Download & install") }
                is UpdateUiState.ReadyToInstall -> TextButton(onClick = { controller.downloadAndInstall() }) { Text("Install") }
                is UpdateUiState.Downloading, is UpdateUiState.Checking -> TextButton(onClick = {}, enabled = false) { Text("Please wait") }
                else -> TextButton(onClick = { controller.check() }) { Text("Check now") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
