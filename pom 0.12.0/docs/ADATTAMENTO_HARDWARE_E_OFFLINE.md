# PROMETEO — adattamento hardware e funzionamento offline

## Implementato in questa versione
- Rilevamento Android locale di RAM totale/disponibile, numero di core, ABI, API Android, modalità low-RAM e risparmio energetico.
- Piano adattivo prudente: livello `RULES_ONLY`, `TINY`, `COMPACT` o `STANDARD`; budget memoria, numero massimo di thread, contesto indicativo e riduzione del lavoro in background.
- Nessuna telemetria: il profilo è calcolato sul dispositivo e mostrato nella dashboard.
- Test unitari comuni per condizioni hardware differenti e valori non disponibili.

## Limiti da non confondere
- `AdaptiveRuntime` è un selettore di profilo e risorse, non un motore AI generativo.
- Il codice `LocalIntelligence` già presente è un assistente deterministico basato su regole e ricerca nelle note. Non equivale a un LLM e non può ragionare liberamente.
- Non sono inclusi pesi di modelli AI locali, runtime LLM (ad esempio llama.cpp/ONNX Runtime), tokenizer, pipeline di download/verifica dei modelli o benchmark per ogni chipset. Quindi la generazione AI avanzata offline NON è ancora implementata.
- Il rilevamento NNAPI indica soltanto che l'API Android è disponibile per quel livello API; non prova che un modello specifico sia compatibile o accelerato. GPU compute è lasciato disabilitato finché non viene rilevato da un backend reale.
- Il profilo è una stima prudente; i consumi effettivi dipendono dal modello, quantizzazione, runtime, temperatura, memoria libera e produttore.

## Regola di adattamento
PROMETEO non deve tentare di caricare un modello più grande della memoria prudenzialmente disponibile. Se le risorse sono insufficienti, resta sulle funzioni locali leggere. La rete può aggiungere ricerca e aggiornamenti, ma non deve essere necessaria per avviare l'app, leggere/scrivere note o gestire attività.

## Test eseguiti durante la preparazione
- Smoke test Kotlin standalone: 4 casi del selettore adattivo superati.
- Verifica XML del manifest Android.
- Verifica statica della presenza del workflow e del collegamento probe/dashboard.

## Test ancora necessari
- `:shared:allTests` con Gradle.
- `:androidApp:assembleDebug` su runner con Android SDK 35.
- Prove su dispositivi fisici differenti (low-RAM, medio, fascia alta), sotto carico e in risparmio energetico.
- Benchmark e validazione di un runtime LLM locale e dei relativi modelli quantizzati prima di dichiarare attiva l'AI generativa offline.
