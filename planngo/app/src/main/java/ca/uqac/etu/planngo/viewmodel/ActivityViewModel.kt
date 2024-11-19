package ca.uqac.etu.planngo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ca.uqac.etu.planngo.data.ActivityRepository
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType

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

    fun getActivities(): List<Activity> {
        return activities
    }

    fun addActivity(activity: Activity) {
        viewModelScope.launch {
            repository.addActivity(activity)
            loadActivities()
        }
    }

    fun updateActivity(activityId: String, updatedActivity: Activity) {
        viewModelScope.launch {
            repository.updateActivity(activityId, updatedActivity)
            loadActivities()
        }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            repository.deleteActivity(activityId)
            loadActivities()
        }
    }

    fun getActivityTypes(): List<ActivityType> {
        return activities.map { it.type }.distinct()
    }

}
