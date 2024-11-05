package ca.uqac.etu.planngo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.etu.planngo.data.Activity
import kotlinx.coroutines.launch
import ca.uqac.etu.planngo.data.ActivityRepository

class ActivityViewModel : ViewModel() {
    private val repository = ActivityRepository()
    private var activities: List<Activity> = listOf()

    init {
        // Charger les activités lors de l'initialisation
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            activities = repository.getActivities()
        }
    }

    public fun getActivities(): List<Activity> {
        return activities
    }
}
