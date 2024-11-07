package ca.uqac.etu.planngo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DB-DEBUG", "Successfully authenticated on Firebase")
                } else {
                    Log.d("DB-DEBUG", "Firebase authentication has failed")
                }
            }
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
