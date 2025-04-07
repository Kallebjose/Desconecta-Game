package com.KS.desconectagame.Usuario

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nome: String,
    var email: String,
    var idade: Int,
    var senha: String
)
