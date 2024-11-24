package ca.uqac.etu.planngo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ca.uqac.etu.planngo.models.DayPlan
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Extension pour accéder au DataStore
val Context.dataStore by preferencesDataStore("planngo_preferences")

object LocalStorage {
    // Clé pour stocker les journées planifiées dans DataStore
    private val PLANNED_DAYS_KEY = stringPreferencesKey("planned_days")
    // Instance Gson pour la sérialisation/désérialisation JSON
    private val gson = Gson()

    // Ajouter une journée planifiée
    suspend fun addPlannedDay(context: Context, day: DayPlan) {
        // Récupérer les journées existantes et y ajouter la nouvelle
        val currentDays = getPlannedDays(context)
        val updatedDays = currentDays.toMutableList().apply { add(day) }
        // Sauvegarder la liste mise à jour
        savePlannedDays(context, updatedDays)
    }

    // Récupérer un Flow de toutes les journées planifiées
    fun getPlannedDaysFlow(context: Context): Flow<List<DayPlan>> {
        return context.dataStore.data.map { preferences ->
            // Lire les données JSON stockées ou retourner une liste vide
            val json = preferences[PLANNED_DAYS_KEY]
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<DayPlan>>() {}.type
                gson.fromJson(json, type)
            }
        }
    }

    // Supprimer une journée planifiée
    suspend fun removePlannedDay(context: Context, day: DayPlan) {
        // Récupérer les journées existantes et en retirer la journée spécifiée
        val currentDays = getPlannedDays(context)
        val updatedDays = currentDays.toMutableList().apply { remove(day) }
        // Sauvegarder la liste mise à jour
        savePlannedDays(context, updatedDays)
    }

    // Effacer toutes les journées planifiées
    suspend fun clearPlannedDays(context: Context) {
        // Supprimer uniquement la clé des journées planifiées
        context.dataStore.edit { preferences ->
            preferences.remove(PLANNED_DAYS_KEY)
        }
    }

    // Effacer toutes les données stockées
    suspend fun clearAllData(context: Context) {
        // Supprimer toutes les préférences dans DataStore
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Sauvegarder les journées planifiées en JSON
    private suspend fun savePlannedDays(context: Context, days: List<DayPlan>) {
        val json = gson.toJson(days)
        // Stocker les données sérialisées sous la clé PLANNED_DAYS_KEY
        context.dataStore.edit { preferences ->
            preferences[PLANNED_DAYS_KEY] = json
        }
    }

    // Obtenir toutes les journées planifiées (en une fois, pas sous forme de Flow)
    private suspend fun getPlannedDays(context: Context): List<DayPlan> {
        // Récupérer les préférences stockées
        val preferences = context.dataStore.data.map { it }.firstOrNull()
        val json = preferences?.get(PLANNED_DAYS_KEY)
        return if (json.isNullOrEmpty()) {
            // Retourner une liste vide si aucune donnée n'est trouvée
            emptyList()
        } else {
            // Désérialiser les données JSON en une liste de DayPlan
            val type = object : TypeToken<List<DayPlan>>() {}.type
            gson.fromJson(json, type)
        }
    }
}
