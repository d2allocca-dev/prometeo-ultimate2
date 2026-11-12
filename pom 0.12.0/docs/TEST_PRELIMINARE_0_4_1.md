# Test preliminare – PROMETEO ULTIMATE 0.4.1-alpha
Data: 8 novembre 2026

## Controlli eseguiti
- Integrità dell'archivio sorgente 0.4: OK (nessun errore ZIP).
- Presenza dei moduli Android, shared e desktop: OK.
- Presenza di MainActivity e inizializzazione del contesto Android prima della UI: OK da ispezione statica.
- Presenza delle schermate di configurazione iniziale, attivazione, home, chat, note, attività, moduli e impostazioni: OK da ispezione statica.
- Correzione del codec di serializzazione: implementata e verificata con casi limite equivalenti (backslash, separatore, stringa vuota, testo normale).
- Persistenza dell'endpoint HTTPS nelle preferenze: implementata; il formato non equivale a una connessione verificata.
- Ripristino iniziale: corretto per riportare a false sia initialized sia awakened.
- Manifest: launcher activity esportata; backup Android disabilitato.

## Anomalie corrette
1. Il codec precedente poteva alterare testo contenente la sequenza letterale `\\e`; sostituito con scansione carattere per carattere.
2. L'endpoint AI non veniva mantenuto dopo il riavvio; ora viene salvato localmente.
3. Un interruttore manuale consentiva di mostrare “Assistente configurato” senza alcun collegamento reale; rimosso. L'interfaccia ora dichiara esplicitamente che l'endpoint HTTPS non è verificato.
4. Il comando “Ripeti configurazione iniziale” non azzerava lo stato awakened; corretto.

## Limiti del test
Non è stato possibile eseguire compilazione Kotlin/Gradle, test strumentali o avvio su emulatore/dispositivo: nell'ambiente non sono presenti Gradle, Android SDK e wrapper Gradle. Quindi il report è un controllo statico preliminare, non una certificazione di compilazione o funzionamento runtime.
