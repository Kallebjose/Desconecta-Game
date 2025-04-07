package com.KS.desconectagame.cadastro

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.KS.desconectagame.R
import com.KS.desconectagame.Usuario.DesconectaBD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsuariosActivity : AppCompatActivity() {

    private lateinit var listaUsuariosTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        listaUsuariosTextView = findViewById(R.id.lista_usuarios)

        val db = DesconectaBD.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val usuarios = db.usuarioDao().buscarTodos()
            val texto = usuarios.joinToString("\n\n") {
                "ID: ${it.id}\nNome: ${it.nome}\nEmail: ${it.email}\nIdade: ${it.idade}\nSenha: ${it.senha}"
            }

            runOnUiThread {
                listaUsuariosTextView.text = texto.ifEmpty { "Nenhum usuário cadastrado." }
            }
        }
    }
}
