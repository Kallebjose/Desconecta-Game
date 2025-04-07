package com.KS.desconectagame.Usuario

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Usuario::class], version = 1)
abstract class DesconectaBD : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    companion object {
        @Volatile
        private var INSTANCE: DesconectaBD? = null
        fun getDatabase(context: Context): DesconectaBD {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DesconectaBD::class.java,
                    "desconecta_bd"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

