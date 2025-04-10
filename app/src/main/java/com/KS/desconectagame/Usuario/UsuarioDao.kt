package com.KS.desconectagame.Usuario

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    // 📝 Insere um novo usuário no banco
    @Insert
    suspend fun inserir(usuario: Usuario)

    // 🔎 Busca todos os usuários cadastrados
    @Query("SELECT * FROM usuarios")
    suspend fun buscarTodos(): List<Usuario>

    // 🔍 Busca um usuário pelo e-mail
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    // 🔑 Verifica se existe um usuário com o e-mail e senha informados (login)
    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun fazerLogin(email: String, senha: String): Usuario?

    // 🗑️ Remove um usuário do banco
    @Delete
    suspend fun deletar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha")
    fun buscarPorEmailSenha(email: String, senha: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun buscarUsuarioPorEmail(email: String): Usuario?

}

