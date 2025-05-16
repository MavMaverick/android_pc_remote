package com.example.phone2pc  // ← adjust to match your package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import android.util.Log

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager




class MainActivity : ComponentActivity() {
    private var webSocketClient: WebSocketClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var message by remember { mutableStateOf("") }
            var status by remember { mutableStateOf("Not connected") }
            val focusManager = LocalFocusManager.current

            fun sendMessage() {
                val uri = URI("ws://192.168.40.155:8765") // Use your PC IP here
                if (webSocketClient == null || !webSocketClient!!.isOpen) {
                    webSocketClient = object : WebSocketClient(uri) {
                        override fun onOpen(handshakedata: ServerHandshake?) {
                            Log.d("WebSocket", "Connected")
                            status = "Connected"
                            webSocketClient?.send(message)
                        }

                        override fun onMessage(msg: String?) {
                            Log.d("WebSocket", "Received from server: $msg")
                        }

                        override fun onClose(code: Int, reason: String?, remote: Boolean) {
                            Log.w("WebSocket", "Closed: code=$code reason=$reason remote=$remote")
                            status = "Closed: $reason"
                        }

                        override fun onError(ex: Exception?) {
                            Log.e("WebSocket", "Error", ex)
                            status = "Error: ${ex?.message}"
                        }
                    }

                    try {
                        webSocketClient?.connect()
                    } catch (e: Exception) {
                        Log.e("WebSocket", "Exception during connect", e)
                        status = "Connect Exception: ${e.message}"
                    }
                } else {
                    webSocketClient?.send(message)
                    Log.d("WebSocket", "Sent message: $message")
                }
            }


            MaterialTheme {
                Column(Modifier.padding(16.dp)) {
                    Text("Send message to PC via WebSocket")
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                sendMessage()
                                message = ""
//                                focusManager.clearFocus() // Hides keyboard
                            }
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                sendMessage()
                                message = "" // Clear after sending
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Send")
                        }

                        Button(
                            onClick = {
                                message = "" // Clear without sending
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear")
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Status: $status")
                }
            }
        }
    }
}
