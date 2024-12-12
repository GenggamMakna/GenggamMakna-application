package com.example.genggammakna.repository

import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val dbFirestore = FirebaseFirestore.getInstance()
    private val userCollection = dbFirestore.collection("users")

    fun registerUser(user: UserModel, callback: (Boolean, String?) -> Unit) {
        userCollection.whereEqualTo("email", user.email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result?.isEmpty == true) {
                        userCollection.add(user)
                            .addOnSuccessListener {
                                callback(true, "Pengguna sukses terdaftar!")
                            }
                            .addOnFailureListener { e ->
                                callback(false, "Gagal mendaftarkan pengguna: ${e.message}")
                            }
                    } else {
                        callback(false, "Alamat Email Sudah Digunakan")
                    }
                } else {
                    callback(false, "Kesalahan saat memeriksa email: ${task.exception?.message}")
                }
            }
    }

    fun loginUser(email: String, password: String, callback: (Boolean, String?, UserModel?) -> Unit){
        userCollection.whereEqualTo("email", email).whereEqualTo("password", password).get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    if (it.result?.isEmpty == false){
                        val user = it.result?.documents?.first()?.toObject(UserModel::class.java)
                        callback(true, "Login Berhasil", user)
                    } else {
                        callback(false, "email atau password salah", null)
                    }
                } else {
                    callback(false, it.exception?.message, null)
                }
            }
    }
}
