package ca.uqac.etu.planngo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ca.uqac.etu.planngo.data.ActivityRepository
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType

class ActivityViewModel : ViewModel() {
    // Initialisation du repository et de la liste des activités
    private val repository = ActivityRepository()
    private var activities: MutableList<Activity> = mutableListOf()

    init {
        // Charger les activités lors de l'initialisation
        loadActivities()
    }

    // Fonction pour charger les activités depuis le repository
    private fun loadActivities() {
        viewModelScope.launch {
            activities = repository.getAllActivities()
        }
    }

    // Fonction pour obtenir la liste des activités
    fun getActivities(): List<Activity> {
        return activities
    }

    // Fonction pour ajouter une activité et recharger la liste
    fun addActivity(activity: Activity) {
        viewModelScope.launch {
            repository.addActivity(activity)
            loadActivities()
        }
    }

    // Fonction pour mettre à jour une activité et recharger la liste
    fun updateActivity(activityId: String, updatedActivity: Activity) {
        viewModelScope.launch {
            repository.updateActivity(activityId, updatedActivity)
            loadActivities()
        }
    }

    // Fonction pour supprimer une activité et recharger la liste
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            repository.deleteActivity(activityId)
            loadActivities()
        }
    }

    // Fonction pour obtenir la liste des types d'activités distincts
    fun getActivityTypes(): List<ActivityType> {
        return activities.map { it.type }.distinct()
    }

    private val _filteredActivities = MutableLiveData<List<Activity>>()
    val filteredActivities: LiveData<List<Activity>> = _filteredActivities


    // Méthode pour rechercher des activités
    fun searchActivities(query: String) {
        if (query.isEmpty()) {
            // Si la requête est vide, afficher toutes les activités
            _filteredActivities.postValue(activities)
        } else {
            // Sinon, filtrer les activités par nom ou autre critère
            val filtered = activities.filter { it.name.contains(query, ignoreCase = true) }
            _filteredActivities.postValue(filtered)
        }
    }

}
