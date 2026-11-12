package it.prometeo.core

/** Deterministic on-device helper: useful offline, but not a sentient or generative model. */
object LocalIntelligence {
    fun respond(input: String, assistantName: String, memories: List<String>, tasks: List<Pair<String, Boolean>>): String {
        val q = normalize(input)
        if (q.isBlank()) return "Scrivi una richiesta e ti aiuterò con le funzioni disponibili sul dispositivo."
        val recalled = memories.map { it to score(q, normalize(it)) }
            .filter { it.second > 0 }.sortedByDescending { it.second }.take(3).map { it.first }
        return when {
            containsAny(q, "ciao", "buongiorno", "buonasera", "salve") -> "Ciao, sono $assistantName. Posso organizzare attività, conservare appunti e ritrovare informazioni salvate."
            containsAny(q, "cosa ricordi", "cosa sai", "ricordi", "cerca nei ricordi", "cerca nelle note") ->
                if (recalled.isEmpty()) "Non ho trovato appunti pertinenti. Puoi aggiungere informazioni nella sezione Note." else "Ho trovato queste informazioni salvate:\n" + recalled.joinToString("\n• ", prefix = "• ")
            containsAny(q, "attività", "compiti", "cose da fare", "todo") -> {
                val open = tasks.filterNot { it.second }
                if (open.isEmpty()) "Non risultano attività aperte." else "Attività ancora aperte:\n" + open.take(8).joinToString("\n• ", prefix = "• ") { it.first }
            }
            containsAny(q, "offline", "senza internet", "senza rete") -> "Le funzioni locali (note, attività e ricerca negli appunti) funzionano senza rete. Le risposte generative avanzate richiedono un modello AI installato o un servizio remoto configurato."
            containsAny(q, "ai", "intelligenza", "come funzioni") -> "Sono $assistantName. In questa modalità uso funzioni locali deterministiche e i dati che hai salvato. Non dichiaro di essere senziente; un modello generativo richiede un motore AI dedicato."
            recalled.isNotEmpty() -> "Questi appunti potrebbero essere pertinenti:\n" + recalled.joinToString("\n• ", prefix = "• ") + "\n\nSe vuoi, posso aiutarti a organizzarli nelle Note."
            else -> "Ho compreso la richiesta, ma in modalità locale non dispongo ancora di un modello generativo per ragionare liberamente. Posso cercare nelle Note, riepilogare le attività e aiutarti a organizzare le informazioni."
        }
    }

    private fun normalize(value: String): String = value.lowercase()
        .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
        .split(Regex("\\s+"))
        .filter { it.length > 2 }
        .distinct()
        .joinToString(" ")

    private fun score(query: String, memory: String): Int {
        val terms = query.split(' ').filter { it.length > 2 }
        if (terms.isEmpty()) return 0
        val memoryTerms = memory.split(' ').toSet()
        return terms.count { it in memoryTerms }
    }

    private fun containsAny(value: String, vararg terms: String): Boolean = terms.any { value.contains(it) }
}
