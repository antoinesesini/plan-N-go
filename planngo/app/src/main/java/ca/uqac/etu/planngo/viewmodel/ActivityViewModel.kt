package ca.uqac.etu.planngo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ca.uqac.etu.planngo.data.ActivityRepository
import ca.uqac.etu.planngo.models.Activity

class ActivityViewModel : ViewModel() {
    private val repository = ActivityRepository()
    private var activities: MutableList<Activity> = mutableListOf()

    init {
        // Charger les activités lors de l'initialisation
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            activities = repository.getAllActivities()
        }
    }

    public fun getActivities(): List<Activity> {
        return activities
    }
}
