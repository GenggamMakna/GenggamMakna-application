package com.example.genggammakna.preferences

import android.content.Context
import com.example.genggammakna.repository.UserModel

class UserPreferences(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun getUser(): UserModel? {
        val firstname = sharedPreferences.getString("firstname", null)
        val lastname = sharedPreferences.getString("lastname", null)
        val email = sharedPreferences.getString("email", null)

        return if (firstname != null && lastname != null && email != null) {
            UserModel(firstname, lastname, email) // Membuat objek UserModel dengan data lengkap
        } else {
            null
        }
    }
    fun saveUser(user: UserModel) {
        with(sharedPreferences.edit()) {
            putString("firstname", user.firstname)
            putString("lastname", user.lastname)
            putString("email", user.email)
            apply()
        }
    }


    fun clearUser() {
        with(sharedPreferences.edit()) {
            remove("firstname")
            remove("email")
            apply()
        }
    }
}
