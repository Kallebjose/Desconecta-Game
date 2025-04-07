package com.KS.desconectagame.cadastro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.KS.desconectagame.R
import com.KS.desconectagame.Usuario.DesconectaBD
import com.KS.desconectagame.Usuario.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var nome: EditText
    private lateinit var idade: EditText
    private lateinit var email: EditText
    private lateinit var emailC: EditText
    private lateinit var senha: EditText
    private lateinit var senhaC: EditText
    private lateinit var botaoCadastro: Button

    private lateinit var db: DesconectaBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // Inicializar os campos
        nome = findViewById(R.id.cad_nome)
        idade = findViewById(R.id.cad_idade)
        email = findViewById(R.id.cad_email)
        emailC = findViewById(R.id.cad_emailC)
        senha = findViewById(R.id.cad_senha)
        senhaC = findViewById(R.id.cad_senhaC)
        botaoCadastro = findViewById(R.id.bt_cadastro)

        // Inicializar o banco de dados
        db = DesconectaBD.getDatabase(this)

        botaoCadastro.setOnClickListener {
            val nomeText = nome.text.toString()
            val idadeText = idade.text.toString()
            val emailText = email.text.toString()
            val emailConfirm = emailC.text.toString()
            val senhaText = senha.text.toString()
            val senhaConfirm = senhaC.text.toString()

            // Verificação básica
            if (nomeText.isBlank() || idadeText.isBlank() || emailText.isBlank() ||
                emailConfirm.isBlank() || senhaText.isBlank() || senhaConfirm.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (emailText != emailConfirm) {
                Toast.makeText(this, "Os e-mails não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senhaText != senhaConfirm) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idadeInt = idadeText.toIntOrNull()
            if (idadeInt == null || idadeInt <= 0) {
                Toast.makeText(this, "Idade inválida!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inserir no banco usando coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val novoUsuario = Usuario(
                    nome = nomeText,
                    idade = idadeInt,
                    email = emailText,
                    senha = senhaText
                )

                db.usuarioDao().inserir(novoUsuario)

                runOnUiThread {
                    Toast.makeText(this@CadastroActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    finish() // Fecha a activity e volta pra tela anterior
                }
            }
        }
    }
}
