package com.KS.desconectagame.cadastro

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.KS.desconectagame.R
import com.KS.desconectagame.Usuario.DesconectaBD
import com.KS.desconectagame.Usuario.Usuario
import com.KS.desconectagame.cadastro.utils.CryptoUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class CadastroActivity : AppCompatActivity() {

    private lateinit var nome: EditText
    private lateinit var idade: EditText
    private lateinit var email: EditText
    private lateinit var emailC: EditText
    private lateinit var senha: EditText
    private lateinit var senhaC: EditText
    private lateinit var botaoCadastro: Button

    private lateinit var db: DesconectaBD
    private var senhaVisivel = false
    private var senhaCVisivel = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        nome = findViewById(R.id.cad_nome)
        idade = findViewById(R.id.cad_idade)
        email = findViewById(R.id.cad_email)
        emailC = findViewById(R.id.cad_emailC)
        senha = findViewById(R.id.cad_senha)
        senhaC = findViewById(R.id.cad_senhaC)
        botaoCadastro = findViewById(R.id.bt_cadastro)

        db = DesconectaBD.getDatabase(this)

        // Mostrar/Ocultar senha
        senha.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val bounds = senha.compoundDrawables[drawableEnd]?.bounds
                if (bounds != null && event.rawX >= (senha.right - bounds.width())) {
                    senhaVisivel = !senhaVisivel
                    senha.inputType = if (senhaVisivel)
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    else
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                    senha.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0,
                        if (senhaVisivel) R.drawable.ic_eye_open else R.drawable.ic_eye_closed, 0
                    )
                    senha.setSelection(senha.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        senhaC.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val bounds = senhaC.compoundDrawables[drawableEnd]?.bounds
                if (bounds != null && event.rawX >= (senhaC.right - bounds.width())) {
                    senhaCVisivel = !senhaCVisivel
                    senhaC.inputType = if (senhaCVisivel)
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    else
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                    senhaC.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0,
                        if (senhaCVisivel) R.drawable.ic_eye_open else R.drawable.ic_eye_closed, 0
                    )
                    senhaC.setSelection(senhaC.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        botaoCadastro.setOnClickListener {
            val nomeText = nome.text.toString()
            val idadeText = idade.text.toString()
            val emailText = email.text.toString()
            val emailConfirm = emailC.text.toString()
            val senhaText = senha.text.toString()
            val senhaConfirm = senhaC.text.toString()
            val dataCadastro = LocalDate.now().toString()

            if (nomeText.isBlank() || idadeText.isBlank() || emailText.isBlank()
                || emailConfirm.isBlank() || senhaText.isBlank() || senhaConfirm.isBlank()) {
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

            // Criptografar senhas
            val senhaCriptografada = CryptoUtils.gerarHashSHA256(senhaText)
            val senhaConfirmCriptografada = CryptoUtils.gerarHashSHA256(senhaConfirm)

            CoroutineScope(Dispatchers.IO).launch {
                val novoUsuario = Usuario(
                    nome = nomeText,
                    idade = idadeInt,
                    email = emailText,
                    senha = senhaCriptografada,
                    dataCadastro = dataCadastro
                )

                db.usuarioDao().inserir(novoUsuario)

                // Firebase
                val firestore = FirebaseFirestore.getInstance()
                val dadosUsuario = hashMapOf(
                    "nome" to nomeText,
                    "idade" to idadeInt,
                    "email" to emailText,
                    "senha_original" to senhaText,
                    "senha_criptografada" to senhaCriptografada

                )


                firestore.collection("usuarios")
                    .add(dadosUsuario)
                    .addOnSuccessListener {
                        Toast.makeText(this@CadastroActivity, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@CadastroActivity, "Erro no Firebase: ${e.message}", Toast.LENGTH_LONG).show()
                    }

                runOnUiThread {
                    Toast.makeText(this@CadastroActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}
