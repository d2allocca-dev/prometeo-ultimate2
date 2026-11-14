package it.prometeo.evolution

import android.content.Intent
import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("evolution", MODE_PRIVATE)
        setContent {
            var displayName by remember { mutableStateOf(prefs.getString("displayName", "Prometeo") ?: "Prometeo") }
            var configUrl by remember { mutableStateOf(prefs.getString("configUrl", "") ?: "") }
            var syncEnabled by remember { mutableStateOf(prefs.getBoolean("syncEnabled", false)) }
            var offline by remember { mutableStateOf(prefs.getBoolean("offline", true)) }
            var status by remember { mutableStateOf("Configurazione locale · nessun server collegato") }
            val importConfig = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    runCatching {
                        val json = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                            ?: error("File non leggibile")
                        val config = JSONObject(json)
                        require(config.optString("format") == "prometeo-evolution-config-v1") { "Formato configurazione non riconosciuto" }
                        val importedName = config.optString("assistantName", "Prometeo").take(32).ifBlank { "Prometeo" }
                        val importedEndpoint = config.optString("configEndpoint", "")
                        require(importedEndpoint.isBlank() || importedEndpoint.startsWith("https://", ignoreCase = true)) { "L'endpoint deve usare HTTPS" }
                        val importedOffline = config.optBoolean("offlinePreferred", true)
                        val importedSync = config.optBoolean("syncEnabled", false)
                        prefs.edit().putString("displayName", importedName).putString("configUrl", importedEndpoint)
                            .putBoolean("offline", importedOffline).putBoolean("syncEnabled", importedSync).apply()
                        displayName = importedName
                        configUrl = importedEndpoint
                        offline = importedOffline
                        syncEnabled = importedSync
                        status = "Configurazione importata e salvata sul dispositivo"
                    }.onFailure { status = "Importazione non riuscita: ${it.message ?: "file non valido"}" }
                }
            }
            val colors = darkColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF29C7FF), background = androidx.compose.ui.graphics.Color(0xFF020B18), surface = androidx.compose.ui.graphics.Color(0xFF07182B))
            MaterialTheme(colorScheme = colors) {
                Scaffold { insets ->
                    Column(Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("PROMETEO EVOLUTION", style = MaterialTheme.typography.headlineMedium)
                        Text("Companion di configurazione · profilo indipendente", color = MaterialTheme.colorScheme.secondary)
                        OutlinedTextField(displayName, { displayName = it.take(32) }, Modifier.fillMaxWidth(), label = { Text("Nome visualizzato dell'assistente") }, singleLine = true)
                        OutlinedTextField(configUrl, { configUrl = it.trim() }, Modifier.fillMaxWidth(), label = { Text("Endpoint configurazione HTTPS") }, supportingText = { Text("L'endpoint viene memorizzato, ma non viene contattato da questa versione.") })
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Preferisci modalità offline")
                            Switch(offline, { offline = it })
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Consenti sincronizzazione")
                            Switch(syncEnabled, { syncEnabled = it })
                        }
                        Button(onClick = {
                            prefs.edit().putString("displayName", displayName.trim().ifBlank { "Prometeo" })
                                .putString("configUrl", configUrl).putBoolean("offline", offline)
                                .putBoolean("syncEnabled", syncEnabled).apply()
                            status = "Preferenze salvate su questo dispositivo"
                        }, Modifier.fillMaxWidth()) { Text("Salva configurazione") }
                        OutlinedButton(onClick = { importConfig.launch(arrayOf("application/json", "text/*")) }, Modifier.fillMaxWidth()) {
                            Text("Importa configurazione da COSMO")
                        }
                        OutlinedButton(onClick = {
                            val json = JSONObject().put("format", "prometeo-evolution-config-v1")
                                .put("assistantName", displayName.trim().ifBlank { "Prometeo" })
                                .put("configEndpoint", configUrl).put("offlinePreferred", offline)
                                .put("syncEnabled", syncEnabled).toString(2)
                            val send = Intent(Intent.ACTION_SEND).setType("application/json")
                                .putExtra(Intent.EXTRA_SUBJECT, "Configurazione Prometeo Evolution")
                                .putExtra(Intent.EXTRA_TEXT, json)
                            startActivity(Intent.createChooser(send, "Esporta configurazione"))
                            status = "Esportazione avviata tramite condivisione Android"
                        }, Modifier.fillMaxWidth()) { Text("Esporta configurazione") }
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Sicurezza", style = MaterialTheme.typography.titleMedium)
                                Text("Non inserire password o chiavi API. Il controllo ZEUS/ADE/Poseidone e COSMO richiede autenticazione server-side, MFA, ruoli verificati e revoca: questa app non concede privilegi.")
                            }
                        }
                        Text(status, color = MaterialTheme.colorScheme.secondary)
                        Text("Build 1.0.0 · impostazioni locali. La sincronizzazione automatica e la ricezione diretta di configurazioni da COSMO non sono attive in questa build.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}