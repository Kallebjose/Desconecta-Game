package com.KS.desconectagame

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.KS.desconectagame.Usuario.DesconectaBD
import com.KS.desconectagame.Usuario.UsuarioDao
import com.KS.desconectagame.cadastro.CadastroActivity
import com.KS.desconectagame.cadastro.UsuariosActivity
import com.KS.desconectagame.util.CryptoUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var emailEdit: EditText
    private lateinit var senhaEdit: EditText
    private lateinit var logarButton: Button
    private lateinit var db: DesconectaBD
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val firestore = FirebaseFirestore.getInstance()

        emailEdit = findViewById(R.id.email)
        senhaEdit = findViewById(R.id.senha)
        logarButton = findViewById(R.id.bt_logar)
        val cadastroText: TextView = findViewById(R.id.txt_cadastro)
        val verUsuarios: TextView = findViewById(R.id.txt_verUsuarios)

        db = DesconectaBD.getDatabase(this)
        usuarioDao = db.usuarioDao()

        // Lógica para mostrar/ocultar senha
        senhaEdit.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEndIndex = 2 // posição do drawableEnd
                val drawable = senhaEdit.compoundDrawables[drawableEndIndex]
                val bounds = drawable?.bounds

                if (bounds != null && event.rawX >= (senhaEdit.right - bounds.width() - senhaEdit.paddingEnd)) {
                    val isVisible = senhaEdit.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

                    if (isVisible) {
                        senhaEdit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        senhaEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)
                    } else {
                        senhaEdit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        senhaEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }

                    // Manter o cursor no fim
                    senhaEdit.setSelection(senhaEdit.text.length)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        logarButton.setOnClickListener {
            val email = emailEdit.text.toString()
            val senha = senhaEdit.text.toString()

            if (email.isBlank() || senha.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val senhaCriptografada = CryptoUtils.gerarHashSHA256(senha)
                val usuario = usuarioDao.fazerLogin(email, senhaCriptografada)

                runOnUiThread {
                    if (usuario != null) {
                        val primeiroNome = usuario.nome.split(" ")[0]
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.putExtra("primeiroNome", primeiroNome)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Credenciais inválidas!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        cadastroText.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        verUsuarios.setOnClickListener {
            val intent = Intent(this, UsuariosActivity::class.java)
            startActivity(intent)
        }
    }
}
