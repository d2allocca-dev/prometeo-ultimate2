package it.prometeo.ultimate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import it.prometeo.core.PrometeoApp
import it.prometeo.core.AndroidStoreInit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidStoreInit.context = applicationContext
        setContent { PrometeoApp(platformName = "Android") }
    }
}
