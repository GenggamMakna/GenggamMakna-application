package com.example.genggammakna.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.genggammakna.repository.UserModel

class UserPreferences(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: UserModel) {
        val editor = sharedPreferences.edit()
        editor.putString("user_name", user.firstname)
        editor.putString("user_email", user.email)
        editor.apply()
    }

    fun getUser(): UserModel? {
        val name = sharedPreferences.getString("user_name", null)
        val email = sharedPreferences.getString("user_email", null)

        return if (name != null && email != null) {
            UserModel(name, email)
        } else {
            null
        }
    }
    fun clearUser() {
        with(sharedPreferences.edit()) {
            remove("firstname")
            remove("lastname")
            remove("email")
            apply()
        }
    }
}
