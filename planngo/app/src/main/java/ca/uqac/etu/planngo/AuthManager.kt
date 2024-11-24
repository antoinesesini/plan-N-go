package ca.uqac.etu.planngo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthManager {

    // Initialisation de l'instance FirebaseAuth
    private val auth = FirebaseAuth.getInstance()

    // Méthode pour se connecter anonymement
    fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                // Si la connexion réussit, on log le succès
                if (task.isSuccessful) {
                    Log.d("DB-DEBUG", "Successfully authenticated on Firebase")
                } else {
                    // Sinon, on log l'échec
                    Log.d("DB-DEBUG", "Firebase authentication has failed")
                }
            }
    }

    // Vérifie si l'utilisateur est authentifié
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    // Déconnexion de l'utilisateur
    fun signOut() {
        auth.signOut()
    }

    // Récupère l'utilisateur actuellement connecté
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
