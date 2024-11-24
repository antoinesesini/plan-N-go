package ca.uqac.etu.planngo.data

import android.util.Log
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.util.GeoPoint

class ActivityRepository {

    // Initialisation de la base de données Firestore
    private val db = FirebaseFirestore.getInstance()

    // Fonction pour récupérer toutes les activités depuis Firestore
    fun getAllActivities(): MutableList<Activity> {
        val resultList: MutableList<Activity> = mutableListOf()
        val activities = db.collection("activities")

        // Récupération des documents depuis Firestore
        activities.get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    // Extraction des champs depuis le document Firestore
                    val name = document.getString("name") ?: ""
                    val typeString = document.getString("type") ?: ""

                    // Conversion du type en enum `ActivityType`
                    val type = when (typeString) {
                        "marche" -> ActivityType.MARCHE
                        "ski" -> ActivityType.SKI
                        "fitness" -> ActivityType.FITNESS
                        "randonnee" -> ActivityType.RANDONNEE
                        "kayak" -> ActivityType.KAYAK
                        "football_americain" -> ActivityType.FOOTBALL_AMERICAIN
                        "soccer" -> ActivityType.SOCCER
                        "yoga" -> ActivityType.YOGA
                        "running" -> ActivityType.RUNNING
                        "velo" -> ActivityType.VELO
                        "escalade" -> ActivityType.ESCALADE
                        "natation" -> ActivityType.NATATION
                        "tennis" -> ActivityType.TENNIS
                        "combat" -> ActivityType.COMBAT
                        "volleyball" -> ActivityType.VOLLEYBALL
                        "handball" -> ActivityType.HANDBALL
                        "hockey" -> ActivityType.HOCKEY
                        "raquettes" -> ActivityType.RAQUETTES
                        "motoneige" -> ActivityType.MOTONEIGE
                        "chien_de_traineau" -> ActivityType.CHIEN_DE_TRAINEAU
                        "peche" -> ActivityType.PECHE
                        else -> ActivityType.MARCHE
                    }

                    // Extraction des autres informations et conversion en objet `Activity`
                    val description = document.getString("description") ?: ""
                    val locationGeopoint = document.getGeoPoint("location")
                    val latitude = locationGeopoint?.latitude ?: 0.0
                    val longitude = locationGeopoint?.longitude ?: 0.0
                    val location = GeoPoint(latitude, longitude)
                    val hours = document.get("hours") as? Map<String, String> ?: emptyMap()
                    val duration = document.getLong("duration")?.toInt() ?: 0
                    val difficulty = document.getLong("difficulty")?.toInt() ?: 0
                    val required = document.get("required") as? List<String> ?: emptyList()
                    val pictures = document.get("pictures") as? List<String> ?: emptyList()

                    val activity = Activity(
                        name = name,
                        type = type,
                        description = description,
                        location = location,
                        hours = hours,
                        duration = duration,
                        difficulty = difficulty,
                        required = required,
                        pictures = pictures
                    )
                    resultList.add(activity) // Ajout de l'activité à la liste des résultats
                }
            }
            .addOnFailureListener {
                // Gestion de l'échec de la récupération
                Log.d("DB-DATA", "No activity found")
            }
        return resultList // Retourne la liste des activités
    }

    // Fonction pour ajouter une nouvelle activité à Firestore
    fun addActivity(activity: Activity) {
        val activityData = mapOf(
            "name" to activity.name,
            "type" to activity.type.name.lowercase(),
            "description" to activity.description,
            "location" to com.google.firebase.firestore.GeoPoint(activity.location.latitude, activity.location.longitude),
            "hours" to activity.hours,
            "duration" to activity.duration,
            "difficulty" to activity.difficulty,
            "required" to activity.required,
            "pictures" to activity.pictures
        )

        // Ajout de l'activité dans la collection Firestore
        db.collection("activities")
            .add(activityData)
            .addOnSuccessListener {
                // Succès de l'ajout
                Log.d("Firestore", "Activity added successfully!")
            }
            .addOnFailureListener { exception ->
                // Gestion des erreurs lors de l'ajout
                Log.w("Firestore", "Error adding activity", exception)
            }
    }

    // Fonction pour mettre à jour une activité existante dans Firestore
    fun updateActivity(activityId: String, updatedActivity: Activity) {
        val updatedData = mapOf(
            "name" to updatedActivity.name,
            "type" to updatedActivity.type.name.lowercase(),
            "description" to updatedActivity.description,
            "location" to com.google.firebase.firestore.GeoPoint(updatedActivity.location.latitude, updatedActivity.location.longitude),
            "hours" to updatedActivity.hours,
            "duration" to updatedActivity.duration,
            "difficulty" to updatedActivity.difficulty,
            "required" to updatedActivity.required,
            "pictures" to updatedActivity.pictures
        )

        // Mise à jour du document correspondant dans Firestore
        db.collection("activities").document(activityId)
            .set(updatedData)
            .addOnSuccessListener {
                // Succès de la mise à jour
                Log.d("Firestore", "Activity updated successfully!")
            }
            .addOnFailureListener { exception ->
                // Gestion des erreurs lors de la mise à jour
                Log.w("Firestore", "Error updating activity", exception)
            }
    }

    // Fonction pour supprimer une activité dans Firestore
    fun deleteActivity(activityId: String) {
        // Suppression du document correspondant dans Firestore
        db.collection("activities").document(activityId)
            .delete()
            .addOnSuccessListener {
                // Succès de la suppression
                Log.d("Firestore", "Activity deleted successfully!")
            }
            .addOnFailureListener { exception ->
                // Gestion des erreurs lors de la suppression
                Log.w("Firestore", "Error deleting activity", exception)
            }
    }
}

