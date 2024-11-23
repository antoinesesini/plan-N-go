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
    private val PLANNED_DAYS_KEY = stringPreferencesKey("planned_days")
    private val gson = Gson()

    // Ajouter une journée planifiée
    suspend fun addPlannedDay(context: Context, day: DayPlan) {
        val currentDays = getPlannedDays(context)
        val updatedDays = currentDays.toMutableList().apply { add(day) }
        savePlannedDays(context, updatedDays)
    }

    // Récupérer toutes les journées planifiées
    fun getPlannedDaysFlow(context: Context): Flow<List<DayPlan>> {
        return context.dataStore.data.map { preferences ->
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
        val currentDays = getPlannedDays(context)
        val updatedDays = currentDays.toMutableList().apply { remove(day) }
        savePlannedDays(context, updatedDays)
    }

    // Effacer toutes les journées planifiées
    suspend fun clearPlannedDays(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(PLANNED_DAYS_KEY)
        }
    }

    // Effacer toutes les journées planifiées
    suspend fun clearAllData(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Sauvegarder les journées planifiées
    private suspend fun savePlannedDays(context: Context, days: List<DayPlan>) {
        val json = gson.toJson(days)
        context.dataStore.edit { preferences ->
            preferences[PLANNED_DAYS_KEY] = json
        }
    }

    // Obtenir toutes les journées planifiées en une fois (pas un Flow)
    private suspend fun getPlannedDays(context: Context): List<DayPlan> {
        val preferences = context.dataStore.data.map { it }.firstOrNull()
        val json = preferences?.get(PLANNED_DAYS_KEY)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<DayPlan>>() {}.type
            gson.fromJson(json, type)
        }
    }
}
