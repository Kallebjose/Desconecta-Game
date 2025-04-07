package com.KS.desconectagame

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val primeiroNome = intent.getStringExtra("primeiroNome") ?: "usuário"
        val boasVindasText = findViewById<TextView>(R.id.tv_boas_vindas)
        boasVindasText.text = "Seja bem-vindo $primeiroNome!"
    }
}
