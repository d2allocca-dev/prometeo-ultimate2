package it.prometeo.core

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class Page(val label: String) { HOME("Home"), CHAT("Assistente"), NOTES("Note"), TASKS("Attività"), MODULES("Moduli"), SETTINGS("Impostazioni"), PRIVATE_SETUP(" "), PRIVATE_GATE(" "), COSMO(" "), COSMO_CONFIG(" "), EFESTO(" "), ORACLE(" ") }
private data class ChatLine(val fromUser: Boolean, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrometeoApp(platformName: String) {
    var initialized by remember { mutableStateOf(PlatformStore.get("initialized") == "true") }
    var awakened by remember { mutableStateOf(PlatformStore.get("awakened") == "true") }
    var assistantName by remember { mutableStateOf(PlatformStore.get("assistantName") ?: "Prometeo") }
    var userName by remember { mutableStateOf(PlatformStore.get("userName") ?: "") }
    var page by remember { mutableStateOf(Page.HOME) }
    var titleTaps by remember { mutableIntStateOf(0) }
    var privateUnlocked by remember { mutableStateOf(false) }
    var secretInput by remember { mutableStateOf("") }
    var secretConfirm by remember { mutableStateOf("") }
    var secretError by remember { mutableStateOf("") }
    var credentialUser by remember { mutableStateOf("") }
    var credentialPassword by remember { mutableStateOf("") }
    var credentialError by remember { mutableStateOf("") }
    var efestoLanguage by remember { mutableStateOf(PlatformStore.get("efesto.language") ?: "it") }
    var efestoVoice by remember { mutableStateOf(PlatformStore.get("efesto.voice") == "true") }
    var efestoOffline by remember { mutableStateOf(PlatformStore.get("efesto.offline") != "false") }
    var oracleDraft by remember { mutableStateOf("") }
    var oracleQuery by remember { mutableStateOf("") }
    var oracleEntries by remember { mutableStateOf(unpack(PlatformStore.get("oracle.entries"))) }
    var message by remember { mutableStateOf("") }
    var noteDraft by remember { mutableStateOf("") }
    var taskDraft by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(unpack(PlatformStore.get("notes"))) }
    var tasks by remember { mutableStateOf(unpack(PlatformStore.get("tasks")).mapNotNull { row ->
        val split = row.lastIndexOf("|"); if (split < 0) null else row.substring(0, split) to (row.substring(split + 1) == "1")
    }) }
    var chat by remember { mutableStateOf(unpack(PlatformStore.get("chat")).mapNotNull { row ->
        val split = row.indexOf("|"); if (split < 0) null else ChatLine(row.substring(0, split) == "U", row.substring(split + 1))
    }.ifEmpty { listOf(ChatLine(false, "Sono pronto ad assisterti nelle funzioni disponibili sul dispositivo.")) }) }
    var aiEndpoint by remember { mutableStateOf(PlatformStore.get("aiEndpoint") ?: "") }
    var cosmoOwnerEmail by remember { mutableStateOf(PlatformStore.get("cosmo.ownerEmail") ?: "promrtro_ai-cloud@gmail.com") }
    var cosmoCloudEndpoint by remember { mutableStateOf(PlatformStore.get("cosmo.cloudEndpoint") ?: "") }
    var cosmoEnrollmentEndpoint by remember { mutableStateOf(PlatformStore.get("cosmo.enrollmentEndpoint") ?: "") }
    var djiEndpoint by remember { mutableStateOf(PlatformStore.get("integration.dji") ?: "") }
    var dflightEndpoint by remember { mutableStateOf(PlatformStore.get("integration.dflight") ?: "") }
    var smartHomeEndpoint by remember { mutableStateOf(PlatformStore.get("integration.smarthome") ?: "") }
    val aiEndpointValid = isSecureAiEndpoint(aiEndpoint)
    var offlineMode by remember { mutableStateOf(PlatformStore.get("offline") != "false") }

    SideEffect {
        PlatformStore.put("initialized", initialized.toString())
        PlatformStore.put("awakened", awakened.toString())
        PlatformStore.put("assistantName", assistantName)
        PlatformStore.put("userName", userName)
        PlatformStore.put("notes", pack(notes))
        PlatformStore.put("tasks", pack(tasks.map { it.first + "|" + if (it.second) "1" else "0" }))
        PlatformStore.put("offline", offlineMode.toString())
        PlatformStore.put("aiEndpoint", aiEndpoint.trim())
        PlatformStore.put("cosmo.ownerEmail", cosmoOwnerEmail.trim())
        PlatformStore.put("cosmo.cloudEndpoint", cosmoCloudEndpoint.trim())
        PlatformStore.put("cosmo.enrollmentEndpoint", cosmoEnrollmentEndpoint.trim())
        PlatformStore.put("integration.dji", djiEndpoint.trim())
        PlatformStore.put("integration.dflight", dflightEndpoint.trim())
        PlatformStore.put("integration.smarthome", smartHomeEndpoint.trim())
        PlatformStore.put("chat", pack(chat.map { (if (it.fromUser) "U" else "A") + "|" + it.text }))
        PlatformStore.put("efesto.language", efestoLanguage)
        PlatformStore.put("efesto.voice", efestoVoice.toString())
        PlatformStore.put("efesto.offline", efestoOffline.toString())
        PlatformStore.put("oracle.entries", pack(oracleEntries))
    }

    val prometeoColors = darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF29C7FF),
        secondary = androidx.compose.ui.graphics.Color(0xFF70E7FF),
        background = androidx.compose.ui.graphics.Color(0xFF020B18),
        surface = androidx.compose.ui.graphics.Color(0xFF07182B),
        onPrimary = androidx.compose.ui.graphics.Color(0xFF00111D),
        onBackground = androidx.compose.ui.graphics.Color(0xFFE8F5FF),
        onSurface = androidx.compose.ui.graphics.Color(0xFFE8F5FF)
    )
    MaterialTheme(colorScheme = prometeoColors) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (!initialized) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.Start) {
                    Spacer(Modifier.height(32.dp))
                    Text("PROMETEO ULTIMATE", style = MaterialTheme.typography.headlineLarge)
                    Text("Benvenuto. Configura l'ambiente iniziale.", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(userName, { userName = it.take(48) }, label = { Text("Il tuo nome (facoltativo)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(assistantName, { assistantName = it.take(32) }, label = { Text("Nome dell'assistente") }, modifier = Modifier.fillMaxWidth())
                    ElevatedCard {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Modalità iniziale", style = MaterialTheme.typography.titleMedium)
                            Text("L'app parte in modalità limitata. L'assistente e i moduli vengono inizializzati solo quando scegli di attivarli.")
                        }
                    }
                    Button(onClick = { initialized = true }, modifier = Modifier.fillMaxWidth()) { Text("Completa configurazione iniziale") }
                    Text("Esecuzione: $platformName", style = MaterialTheme.typography.labelSmall)
                }
            } else if (!awakened) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.Start) {
                    Spacer(Modifier.height(36.dp))
                    Text("La nascita di $assistantName", style = MaterialTheme.typography.headlineMedium)
                    Text("La configurazione iniziale è pronta. L'assistente non è ancora stato attivato.")
                    ElevatedCard { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Prima di iniziare", style = MaterialTheme.typography.titleMedium)
                        Text("Con l'attivazione abiliti le funzioni locali dell'assistente. Le funzioni AI generative richiedono un servizio AI configurato e non vengono simulate.")
                    } }
                    Button(onClick = { awakened = true }, modifier = Modifier.fillMaxWidth()) { Text("Dai vita a $assistantName") }
                    TextButton(onClick = { initialized = false }) { Text("Torna alla configurazione") }
                }
            } else {
                Scaffold(
                    topBar = { TopAppBar(title = { Column {
                        Text(assistantName.uppercase(), modifier = Modifier.clickable { if (PlatformStore.get("private.hash") == null) { titleTaps++; if (titleTaps >= 7) { titleTaps = 0; page = Page.PRIVATE_SETUP } } }, style = MaterialTheme.typography.titleLarge)
                        Text(if (aiEndpointValid) "Endpoint HTTPS impostato · non verificato" else "Modalità locale", style = MaterialTheme.typography.labelSmall)
                    } }) },
                    bottomBar = { NavigationBar { listOf(Page.HOME, Page.CHAT, Page.NOTES, Page.TASKS, Page.MODULES, Page.SETTINGS).forEach { p -> NavigationBarItem(selected = page == p, onClick = { page = p }, icon = { Text(p.label.take(1)) }, label = { Text(p.label) }) } } }
                ) { padding ->
                    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp)) {
                        when (page) {
                            Page.HOME -> HomePage(assistantName, userName, aiEndpointValid, offlineMode) { page = it }
                            Page.CHAT -> ChatPage(assistantName, chat, message, { message = it }, { text ->
                                if (text.isNotBlank()) {
                                    val storedSalt = PlatformStore.get("private.salt")
                                    val storedHash = PlatformStore.get("private.hash")
                                    val isSecretPhrase = storedSalt != null && storedHash != null && deriveSecretHash(text.trim(), storedSalt) == storedHash
                                    if (isSecretPhrase) {
                                        // La frase non viene salvata nella cronologia della chat.
                                        message = ""
                                        credentialUser = ""
                                        credentialPassword = ""
                                        credentialError = ""
                                        page = Page.PRIVATE_GATE
                                    } else {
                                        val answer = LocalIntelligence.respond(text, assistantName, notes, tasks)
                                        chat = chat + ChatLine(true, text.trim()) + ChatLine(false, answer)
                                        message = ""
                                    }
                                }
                            })
                            Page.NOTES -> NotesPage(notes, noteDraft, { noteDraft = it }, { if (noteDraft.isNotBlank()) { notes = notes + noteDraft.trim(); noteDraft = "" } }, { notes = notes - it })
                            Page.TASKS -> TasksPage(tasks, taskDraft, { taskDraft = it }, { if (taskDraft.isNotBlank()) { tasks = tasks + (taskDraft.trim() to false); taskDraft = "" } }, { index -> tasks = tasks.mapIndexed { i, item -> if (i == index) item.first to !item.second else item } }, { index -> tasks = tasks.filterIndexed { i, _ -> i != index } })
                            Page.MODULES -> ModulesPage()
                            Page.SETTINGS -> SettingsPage(assistantName, { assistantName = it }, aiEndpoint, { aiEndpoint = it }, offlineMode, { offlineMode = it }, { initialized = false; awakened = false })
                            Page.PRIVATE_SETUP -> PrivateSetupPage(
                                secretInput, { secretInput = it }, secretConfirm, { secretConfirm = it },
                                credentialUser, { credentialUser = it }, credentialPassword, { credentialPassword = it }, secretError,
                                onSubmit = {
                                    if (PlatformStore.get("private.hash") != null) secretError = "Configurazione già presente."
                                    else if (secretInput.length < 12 || secretInput != secretConfirm) secretError = "La frase deve avere almeno 12 caratteri e coincidere con la conferma."
                                    else if (credentialUser.isBlank() || credentialPassword.length < 12) secretError = "Inserisci un identificativo e una password di almeno 12 caratteri."
                                    else {
                                        val phraseSalt = createSecretSalt()
                                        val credentialSalt = createSecretSalt()
                                        PlatformStore.put("private.salt", phraseSalt)
                                        PlatformStore.put("private.hash", deriveSecretHash(secretInput, phraseSalt))
                                        PlatformStore.put("private.user", credentialUser.trim())
                                        PlatformStore.put("private.credential.salt", credentialSalt)
                                        PlatformStore.put("private.credential.hash", deriveSecretHash(credentialPassword, credentialSalt))
                                        secretInput = ""; secretConfirm = ""; credentialPassword = ""; credentialUser = ""; secretError = ""
                                        page = Page.HOME
                                    }
                                }, onBack = { page = Page.HOME }
                            )
                            Page.PRIVATE_GATE -> CredentialGatePage(
                                credentialUser, { credentialUser = it }, credentialPassword, { credentialPassword = it }, credentialError,
                                onSubmit = {
                                    val expectedUser = PlatformStore.get("private.user")
                                    val salt = PlatformStore.get("private.credential.salt")
                                    val hash = PlatformStore.get("private.credential.hash")
                                    if (expectedUser != null && salt != null && hash != null && credentialUser.trim() == expectedUser && deriveSecretHash(credentialPassword, salt) == hash) {
                                        privateUnlocked = true
                                        credentialUser = ""; credentialPassword = ""; credentialError = ""
                                        page = Page.COSMO
                                    } else {
                                        credentialError = "Credenziali non valide."
                                        credentialPassword = ""
                                    }
                                }, onBack = { credentialUser = ""; credentialPassword = ""; page = Page.HOME }
                            )
                            Page.COSMO -> if (privateUnlocked) CosmoDashboard(
                                controlEmail = cosmoOwnerEmail, controlEndpointConfigured = isSecureAiEndpoint(cosmoCloudEndpoint),
                                onEfesto = { page = Page.EFESTO },
                                onOracle = { page = Page.ORACLE },
                                onConfigure = { page = Page.COSMO_CONFIG },
                                onLock = { privateUnlocked = false; page = Page.HOME }
                            ) else { page = Page.HOME }
                            Page.COSMO_CONFIG -> if (privateUnlocked) CosmoConfigPage(
                                cosmoOwnerEmail, { cosmoOwnerEmail = it }, cosmoCloudEndpoint, { cosmoCloudEndpoint = it },
                                cosmoEnrollmentEndpoint, { cosmoEnrollmentEndpoint = it }, djiEndpoint, { djiEndpoint = it },
                                dflightEndpoint, { dflightEndpoint = it }, smartHomeEndpoint, { smartHomeEndpoint = it },
                                onBack = { page = Page.COSMO }
                            ) else { page = Page.HOME }
                            Page.EFESTO -> if (privateUnlocked) EfestoPage(assistantName, { assistantName = it }, efestoLanguage, { efestoLanguage = it }, efestoVoice, { efestoVoice = it }, efestoOffline, { efestoOffline = it }, onOracle = { page = Page.ORACLE }, onLock = { privateUnlocked = false; page = Page.HOME }) else { page = Page.HOME }
                            Page.ORACLE -> if (privateUnlocked) OraclePage(oracleEntries, oracleDraft, { oracleDraft = it }, { if (oracleDraft.isNotBlank()) { oracleEntries = oracleEntries + oracleDraft.trim(); oracleDraft = "" } }, { oracleEntries = oracleEntries - it }, oracleQuery, { oracleQuery = it }, onBack = { page = Page.COSMO }) else { page = Page.HOME }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun HomePage(name: String, user: String, configured: Boolean, offline: Boolean, navigate: (Page) -> Unit) {
    val accent = androidx.compose.ui.graphics.Color(0xFF29C7FF)
    val muted = androidx.compose.ui.graphics.Color(0xFF9BCDEB)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        // Home redesign inspired by the supplied PROMETEO/COSMO visual reference.
        ElevatedCard(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = androidx.compose.ui.graphics.Color(0xFF061A31))
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text("✧  PROMETEO  ✧", style = MaterialTheme.typography.headlineLarge, color = accent)
                Text("ULTIMATE AI ASSISTANT", style = MaterialTheme.typography.labelLarge, color = muted)
                Spacer(Modifier.height(4.dp))
                Text(if (user.isBlank()) "Ciao" else "Ciao, $user", style = MaterialTheme.typography.headlineMedium)
                Text("Come posso aiutarti oggi?", style = MaterialTheme.typography.titleMedium, color = muted)
                Text("〰  🎙  〰", style = MaterialTheme.typography.displaySmall, color = accent)
                Button(onClick = { navigate(Page.CHAT) }, modifier = Modifier.fillMaxWidth()) {
                    Text("🎙  Apri assistente")
                }
                Text(
                    when {
                        configured -> "Endpoint HTTPS inserito · collegamento non verificato"
                        offline -> "$name attivo · modalità locale/offline selezionata"
                        else -> "$name attivo · modalità locale"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = muted
                )
            }
        }
        val tiles = listOf(
            Triple("💬", "Chat AI", Page.CHAT),
            Triple("▣", "Assistente personale", Page.TASKS),
            Triple("⌂", "Domotica", Page.MODULES),
            Triple("✈", "Droni & Volo", Page.MODULES),
            Triple("☼", "Meteo & Mappe", Page.MODULES),
            Triple("⚒", "Strumenti", Page.SETTINGS)
        )
        tiles.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                pair.forEach { (symbol, label, target) ->
                    ElevatedCard(
                        Modifier.weight(1f).height(112.dp).clickable { navigate(target) },
                        colors = CardDefaults.elevatedCardColors(containerColor = androidx.compose.ui.graphics.Color(0xFF071B32))
                    ) {
                        Column(
                            Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(symbol, style = MaterialTheme.typography.headlineMedium, color = accent)
                            Spacer(Modifier.height(5.dp))
                            Text(label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Il tuo spazio personale", style = MaterialTheme.typography.titleMedium, color = accent)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { navigate(Page.NOTES) }, modifier = Modifier.weight(1f)) { Text("Note") }
                    OutlinedButton(onClick = { navigate(Page.TASKS) }, modifier = Modifier.weight(1f)) { Text("Attività") }
                    OutlinedButton(onClick = { navigate(Page.SETTINGS) }, modifier = Modifier.weight(1f)) { Text("Profilo") }
                }
            }
        }
    }
}

@Composable private fun CosmoDashboard(controlEmail: String, controlEndpointConfigured: Boolean, onEfesto: () -> Unit, onOracle: () -> Unit, onConfigure: () -> Unit, onLock: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("COSMO", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        ElevatedCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✓  Accesso riservato autorizzato", color = androidx.compose.ui.graphics.Color(0xFF75E69A), style = MaterialTheme.typography.titleMedium)
        } }
        ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("CENTRO DI COMANDO GLOBALE", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text("Casella amministrativa: $controlEmail")
            Text(if (controlEndpointConfigured) "Endpoint HTTPS inserito · connessione da verificare" else "Backend globale non configurato · comandi remoti disattivati", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            Text("L'indirizzo identifica la casella amministrativa; non invia comandi da solo.", style = MaterialTheme.typography.bodySmall)
        } }
        ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ω", style = MaterialTheme.typography.displayLarge, color = androidx.compose.ui.graphics.Color(0xFFFFD36A))
            Text("CENTRO OPERATIVO SICUREZZA E MONITORAGGIO", style = MaterialTheme.typography.labelMedium)
        } }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ElevatedCard(Modifier.weight(1f).clickable { onEfesto() }) { Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("⚙", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary); Text("EFESTO"); Text("Pannello operativo", style = MaterialTheme.typography.bodySmall) } }
            ElevatedCard(Modifier.weight(1f).clickable { onOracle() }) { Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("◉", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary); Text("ORACLE"); Text("Memorie locali", style = MaterialTheme.typography.bodySmall) } }
        }
        listOf("Gestione sicurezza", "Controllo dispositivi", "Analisi e strategie").forEach { label ->
            ElevatedCard(Modifier.fillMaxWidth()) { Text(label + " · predisposto per il backend", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.secondary) }
        }
        Button(onClick = onConfigure, modifier = Modifier.fillMaxWidth()) { Text("Configurazione cloud e unità") }
        Button(onClick = onLock, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF8E2525))) { Text("🔒  Blocca area riservata") }
    }
}

@Composable private fun CosmoConfigPage(
    ownerEmail: String, setOwnerEmail: (String) -> Unit,
    cloud: String, setCloud: (String) -> Unit,
    enrollment: String, setEnrollment: (String) -> Unit,
    dji: String, setDji: (String) -> Unit,
    dflight: String, setDflight: (String) -> Unit,
    smartHome: String, setSmartHome: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("COSMO · Configurazione ecosistema", style = MaterialTheme.typography.headlineSmall)
        Text("Questi campi preparano la registrazione delle unità e dei servizi. Inserire un indirizzo non attiva da solo un collegamento.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(ownerEmail, setOwnerEmail, modifier = Modifier.fillMaxWidth(), label = { Text("Email amministrativa globale") })
        OutlinedTextField(cloud, setCloud, modifier = Modifier.fillMaxWidth(), label = { Text("Endpoint HTTPS backend COSMO") })
        OutlinedTextField(enrollment, setEnrollment, modifier = Modifier.fillMaxWidth(), label = { Text("Endpoint registrazione unità HTTPS") })
        Divider()
        Text("Integrazioni esterne", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(dji, setDji, modifier = Modifier.fillMaxWidth(), label = { Text("Endpoint servizio DJI / flotta") })
        OutlinedTextField(dflight, setDflight, modifier = Modifier.fillMaxWidth(), label = { Text("Endpoint D-Flight / pianificazione") })
        OutlinedTextField(smartHome, setSmartHome, modifier = Modifier.fillMaxWidth(), label = { Text("Endpoint domotica (eWeLink / Home / Alexa)") })
        ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Credenziali e sicurezza", style = MaterialTheme.typography.titleMedium)
            Text("Non inserire qui la password Gmail né password di account esterni. L'email $ownerEmail è un recapito di riferimento, non un canale di comando.")
            Text("Per il controllo globale, il backend dovrà usare OAuth 2.0 o credenziali di servizio conservate in un secret manager, autenticare ogni unità, firmare i comandi, applicare ruoli ZEUS/ADE/Poseidone e registrare gli audit. Le credenziali non devono essere incluse nell'APK.", style = MaterialTheme.typography.bodySmall)
        } }
        Text("Stato: configurazione locale salvata sul dispositivo; servizi remoti non verificati.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Salva e torna a COSMO") }
    }
}

@Composable private fun ChatPage(name: String, chat: List<ChatLine>, message: String, onMessage: (String) -> Unit, send: (String) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Conversazione con $name", style = MaterialTheme.typography.titleLarge)
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chat) { line -> ElevatedCard(Modifier.fillMaxWidth()) { Text((if (line.fromUser) "Tu: " else "$name: ") + line.text, Modifier.padding(12.dp)) } }
        }
        OutlinedTextField(message, onMessage, modifier = Modifier.fillMaxWidth(), label = { Text("Scrivi un messaggio") })
        Button(onClick = { send(message) }, modifier = Modifier.fillMaxWidth(), enabled = message.isNotBlank()) { Text("Invia") }
    }
}

@Composable private fun NotesPage(notes: List<String>, draft: String, onDraft: (String) -> Unit, add: () -> Unit, remove: (String) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Note personali", style = MaterialTheme.typography.titleLarge)
        Text("Appunti salvati localmente sul dispositivo. Non inserire segreti o credenziali.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(draft, onDraft, modifier = Modifier.fillMaxWidth(), label = { Text("Nuova nota") })
        Button(onClick = add, enabled = draft.isNotBlank()) { Text("Aggiungi nota") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) { items(notes) { note -> ElevatedCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) { Text(note, Modifier.weight(1f)); TextButton(onClick = { remove(note) }) { Text("Elimina") } } } } }
    }
}

@Composable private fun TasksPage(tasks: List<Pair<String, Boolean>>, draft: String, onDraft: (String) -> Unit, add: () -> Unit, toggle: (Int) -> Unit, remove: (Int) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Attività", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(draft, onDraft, modifier = Modifier.fillMaxWidth(), label = { Text("Nuova attività") })
        Button(onClick = add, enabled = draft.isNotBlank()) { Text("Aggiungi") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) { items(tasks.size) { i -> Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(tasks[i].second, { toggle(i) }); Text(tasks[i].first, Modifier.weight(1f)); TextButton(onClick = { remove(i) }) { Text("Rimuovi") } } } }
    }
}

@Composable private fun ModulesPage() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Moduli", style = MaterialTheme.typography.titleLarge)
        listOf("Domotica" to "Le integrazioni con servizi e dispositivi richiedono account e autorizzazioni dei rispettivi fornitori.", "Droni" to "La pianificazione richiede dati aeronautici ufficiali e verifiche normative aggiornate.", "Dispositivi" to "Le autorizzazioni Android saranno richieste solo quando una funzione ne avrà bisogno.", "Modalità offline" to "Le funzioni locali restano disponibili senza rete; l'AI generativa remota richiede connessione.").forEach { (title, desc) -> ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(desc) } } }
    }
}

@Composable private fun SettingsPage(name: String, setName: (String) -> Unit, endpoint: String, setEndpoint: (String) -> Unit, offline: Boolean, setOffline: (Boolean) -> Unit, reset: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Impostazioni", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(name, setName, modifier = Modifier.fillMaxWidth(), label = { Text("Nome assistente") })
        Text("Collegamento AI", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(endpoint, setEndpoint, modifier = Modifier.fillMaxWidth(), label = { Text("Endpoint del servizio AI (HTTPS)") }, supportingText = { Text("Non inserire chiavi API qui. Le credenziali devono essere gestite da un backend sicuro.") })
        Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(offline, setOffline); Text("Preferisci modalità offline") }
        Text(if (isSecureAiEndpoint(endpoint)) "Formato HTTPS presente; collegamento e autenticazione non verificati." else "Nessun endpoint HTTPS valido impostato.", style = MaterialTheme.typography.bodySmall)
        Text("La selezione non crea da sola un collegamento AI. Per l'uso reale serve un backend autenticato, TLS, gestione segreti lato server e autorizzazioni.", style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = reset) { Text("Ripeti configurazione iniziale") }
    }
}



@Composable private fun PrivateSetupPage(secret: String, setSecret: (String) -> Unit, confirm: String, setConfirm: (String) -> Unit, username: String, setUsername: (String) -> Unit, password: String, setPassword: (String) -> Unit, error: String, onSubmit: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Configurazione accesso", style = MaterialTheme.typography.headlineSmall)
        Text("Imposta la frase da inviare nella chat e le credenziali da confermare nel passaggio successivo.")
        OutlinedTextField(secret, setSecret, modifier = Modifier.fillMaxWidth(), label = { Text("Frase segreta (minimo 12 caratteri)") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        OutlinedTextField(confirm, setConfirm, modifier = Modifier.fillMaxWidth(), label = { Text("Conferma frase segreta") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        OutlinedTextField(username, setUsername, modifier = Modifier.fillMaxWidth(), label = { Text("Identificativo") })
        OutlinedTextField(password, setPassword, modifier = Modifier.fillMaxWidth(), label = { Text("Password (minimo 12 caratteri)") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) { Text("Salva configurazione") }
        TextButton(onClick = onBack) { Text("Annulla") }
        Text("Configurazione locale di sviluppo: non equivale ad autenticazione server e non protegge da un dispositivo compromesso.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable private fun CredentialGatePage(username: String, setUsername: (String) -> Unit, password: String, setPassword: (String) -> Unit, error: String, onSubmit: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Verifica credenziali", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(username, setUsername, modifier = Modifier.fillMaxWidth(), label = { Text("Identificativo") })
        OutlinedTextField(password, setPassword, modifier = Modifier.fillMaxWidth(), label = { Text("Password") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), enabled = username.isNotBlank() && password.isNotBlank()) { Text("Conferma accesso") }
        TextButton(onClick = onBack) { Text("Annulla") }
        Text("Verifica locale di sviluppo. Per un'autenticazione reale servono verifica lato server, MFA, limitazione tentativi e revoca sessioni.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable private fun EfestoPage(name: String, setName: (String) -> Unit, language: String, setLanguage: (String) -> Unit, voice: Boolean, setVoice: (Boolean) -> Unit, offline: Boolean, setOffline: (Boolean) -> Unit, onOracle: () -> Unit, onLock: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("EFESTO", style = MaterialTheme.typography.headlineMedium)
        Text("Configurazione dell'assistente e delle preferenze locali.")
        OutlinedTextField(name, setName, modifier = Modifier.fillMaxWidth(), label = { Text("Nome visualizzato") })
        OutlinedTextField(language, setLanguage, modifier = Modifier.fillMaxWidth(), label = { Text("Lingua (es. it)") })
        Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(voice, setVoice); Text("Abilita preferenza vocale") }
        Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(offline, setOffline); Text("Preferisci funzioni offline") }
        Text("Le preferenze vengono salvate sul dispositivo. Le integrazioni esterne richiedono account e autorizzazioni dei rispettivi fornitori.", style = MaterialTheme.typography.bodySmall)
        Button(onClick = onOracle, modifier = Modifier.fillMaxWidth()) { Text("Apri ORACLE") }
        OutlinedButton(onClick = onLock, modifier = Modifier.fillMaxWidth()) { Text("Blocca area riservata") }
    }
}

@Composable private fun OraclePage(entries: List<String>, draft: String, setDraft: (String) -> Unit, add: () -> Unit, remove: (String) -> Unit, query: String, setQuery: (String) -> Unit, onBack: () -> Unit) {
    val results = remember(entries, query) { if (query.isBlank()) entries else entries.filter { it.contains(query, ignoreCase = true) } }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("ORACLE", style = MaterialTheme.typography.headlineMedium)
        Text("Memoria locale ricercabile. I dati restano su questo dispositivo.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(draft, setDraft, modifier = Modifier.fillMaxWidth(), label = { Text("Nuova memoria") })
        Button(onClick = add, enabled = draft.isNotBlank()) { Text("Salva memoria") }
        OutlinedTextField(query, setQuery, modifier = Modifier.fillMaxWidth(), label = { Text("Cerca nella memoria") })
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { items(results) { entry -> ElevatedCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) { Text(entry, Modifier.weight(1f)); TextButton(onClick = { remove(entry) }) { Text("Elimina") } } } } }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Torna a EFESTO") }
    }
}
