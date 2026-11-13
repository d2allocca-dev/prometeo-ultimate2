# Prometeo Evolution (Android)

Standalone Android companion app (applicationId: `it.prometeo.evolution`).

Current working features:
- Editable assistant display name.
- Local persistence of endpoint and offline/sync preferences.
- Export of a versioned JSON configuration through Android Sharesheet.
- No embedded API keys or credentials.

Not implemented in this build: authenticated COSMO connection, automatic two-way sync/import, remote command execution, server-side ZEUS/ADE/Poseidone authorization. The sync toggle is a saved preference only and does not claim a live connection.

Build from the project root:
`gradle :evolutionApp:assembleDebug`
