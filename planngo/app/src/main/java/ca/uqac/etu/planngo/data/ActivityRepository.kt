package ca.uqac.etu.planngo.data

import android.util.Log
import ca.uqac.etu.planngo.models.Activity
import ca.uqac.etu.planngo.models.ActivityType
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.util.GeoPoint

class ActivityRepository {

    private val db = FirebaseFirestore.getInstance()

    // Récupérer toutes les activités (Read)
    fun getAllActivities(): MutableList<Activity> {
        val resultList: MutableList<Activity> = mutableListOf()
        val activities = db.collection("activities")

        activities.get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    val name = document.getString("name") ?: ""
                    val typeString = document.getString("type") ?: ""
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
                    resultList.add(activity)
                }
            }
            .addOnFailureListener {
                Log.d("DB-DATA", "No activity found")
            }
        return resultList
    }

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

        db.collection("activities")
            .add(activityData)
            .addOnSuccessListener {
                Log.d("Firestore", "Activity added successfully!")
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error adding activity", exception)
            }
    }

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

        db.collection("activities").document(activityId)
            .set(updatedData)
            .addOnSuccessListener {
                Log.d("Firestore", "Activity updated successfully!")
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error updating activity", exception)
            }
    }

    fun deleteActivity(activityId: String) {
        db.collection("activities").document(activityId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Activity deleted successfully!")
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error deleting activity", exception)
            }
    }

}

















//private fun addActivities() {
//
//    val activities = listOf(
//        mapOf(
//            "name" to "Kayak sur la rivière Saguenay",
//            "type" to "kayak",
//            "description" to "Venez explorer la magnifique rivière Saguenay en kayak, entouré par les paysages exceptionnels de la région.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4213, -71.0537),
//            "hours" to mapOf("start" to "09:00", "end" to "14:00"),
//            "duration" to 3,
//            "difficulty" to 2,
//            "required" to listOf("serviette", "vêtements de rechange"),
//            "pictures" to listOf("url_de_l_image_kayak_1.jpg", "url_de_l_image_kayak_2.jpg")
//        ),
//        mapOf(
//            "name" to "Randonnée au Parc national des Monts-Valin",
//            "type" to "randonnee",
//            "description" to "Explorez les sentiers magnifiques du Parc national des Monts-Valin et admirez les panoramas de la région.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.5135, -70.8861),
//            "hours" to mapOf("start" to "08:00", "end" to "16:00"),
//            "duration" to 8,
//            "difficulty" to 4,
//            "required" to listOf("chaussures de randonnée", "sac à dos", "eau", "vêtements appropriés"),
//            "pictures" to listOf("url_de_l_image_randonnee_1.jpg", "url_de_l_image_randonnee_2.jpg")
//        ),
//        mapOf(
//            "name" to "Ski au Mont Valin",
//            "type" to "ski",
//            "description" to "Ski sur les pistes du Mont Valin, une destination prisée pour les amateurs de glisse.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.5067, -70.8449),
//            "hours" to mapOf("start" to "09:00", "end" to "16:00"),
//            "duration" to 7,
//            "difficulty" to 3,
//            "required" to listOf("skis", "bâtons", "vêtements d'hiver", "casque"),
//            "pictures" to listOf("url_de_l_image_ski_1.jpg", "url_de_l_image_ski_2.jpg")
//        ),
//        mapOf(
//            "name" to "Vélo de montagne à la Vallée du Bras-du-Nord",
//            "type" to "fitness",
//            "description" to "Parcourez les sentiers de vélo de montagne de la Vallée du Bras-du-Nord, dans un cadre naturel incroyable.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4603, -71.0654),
//            "hours" to mapOf("start" to "10:00", "end" to "13:00"),
//            "duration" to 3,
//            "difficulty" to 3,
//            "required" to listOf("vélo de montagne", "casque", "gants"),
//            "pictures" to listOf("url_de_l_image_velo_1.jpg", "url_de_l_image_velo_2.jpg")
//        ),
//        mapOf(
//            "name" to "Randonnée en raquettes au Parc de la Rivière-du-Moulin",
//            "type" to "randonnee",
//            "description" to "Profitez des sentiers de raquettes au Parc de la Rivière-du-Moulin, un endroit idéal pour l'hiver.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4400, -71.0783),
//            "hours" to mapOf("start" to "10:00", "end" to "14:00"),
//            "duration" to 4,
//            "difficulty" to 2,
//            "required" to listOf("raquettes", "vêtements d'hiver", "gants", "bonnet"),
//            "pictures" to listOf("url_de_l_image_raquette_1.jpg", "url_de_l_image_raquette_2.jpg")
//        ),
//        mapOf(
//            "name" to "Football américain à la Plaine des Sports",
//            "type" to "football_americain",
//            "description" to "Joignez-vous à une partie de football américain sur les terrains de la Plaine des Sports de Chicoutimi.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4172, -71.0623),
//            "hours" to mapOf("start" to "14:00", "end" to "16:00"),
//            "duration" to 2,
//            "difficulty" to 2,
//            "required" to listOf("ballon de football", "chaussures de sport", "équipement de protection"),
//            "pictures" to listOf("url_de_l_image_football_1.jpg", "url_de_l_image_football_2.jpg")
//        ),
//        mapOf(
//            "name" to "Soccer au Parc Richelieu",
//            "type" to "soccer",
//            "description" to "Participez à un match de soccer amical au Parc Richelieu, un lieu populaire à Chicoutimi.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4216, -71.0685),
//            "hours" to mapOf("start" to "17:00", "end" to "19:00"),
//            "duration" to 2,
//            "difficulty" to 2,
//            "required" to listOf("ballon de soccer", "chaussures de sport", "maillot"),
//            "pictures" to listOf("url_de_l_image_soccer_1.jpg", "url_de_l_image_soccer_2.jpg")
//        ),
//        mapOf(
//            "name" to "Escalade au Mont Chicoutimi",
//            "type" to "fitness",
//            "description" to "Venez tester vos capacités en escalade au Mont Chicoutimi, un endroit idéal pour les amateurs de sensations fortes.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.3987, -71.0394),
//            "hours" to mapOf("start" to "08:00", "end" to "12:00"),
//            "duration" to 4,
//            "difficulty" to 4,
//            "required" to listOf("chaussures d'escalade", "harnais", "casque"),
//            "pictures" to listOf("url_de_l_image_escalade_1.jpg", "url_de_l_image_escalade_2.jpg")
//        ),
//        mapOf(
//            "name" to "Running au Parc du Saguenay",
//            "type" to "marche",
//            "description" to "Profitez des sentiers de course du Parc du Saguenay, un excellent choix pour les coureurs débutants et expérimentés.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4362, -71.0555),
//            "hours" to mapOf("start" to "06:30", "end" to "08:00"),
//            "duration" to 1,
//            "difficulty" to 1,
//            "required" to listOf("chaussures de course", "vêtements légers"),
//            "pictures" to listOf("url_de_l_image_running_1.jpg", "url_de_l_image_running_2.jpg")
//        ),
//        mapOf(
//            "name" to "Yoga au bord de l'eau",
//            "type" to "fitness",
//            "description" to "Participez à une session de yoga en plein air au bord de la rivière Saguenay.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4235, -71.0518),
//            "hours" to mapOf("start" to "07:30", "end" to "08:30"),
//            "duration" to 1,
//            "difficulty" to 1,
//            "required" to listOf("tapis de yoga", "vêtements confortables"),
//            "pictures" to listOf("url_de_l_image_yoga_1.jpg", "url_de_l_image_yoga_2.jpg")
//        ),
//        mapOf(
//            "name" to "Pêche au lac Saint-Jean",
//            "type" to "marche",
//            "description" to "Venez passer une journée calme à pêcher au lac Saint-Jean.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.7000, -71.3000),
//            "hours" to mapOf("start" to "06:00", "end" to "18:00"),
//            "duration" to 12,
//            "difficulty" to 1,
//            "required" to listOf("canne à pêche", "appâts", "chapeau"),
//            "pictures" to listOf("url_de_l_image_peche_1.jpg", "url_de_l_image_peche_2.jpg")
//        ),
//        mapOf(
//            "name" to "Canoë-kayak au lac Kénogami",
//            "type" to "kayak",
//            "description" to "Découvrez le lac Kénogami en canoë ou kayak, un lieu paisible et idéal pour les sorties en famille.",
//            "location" to com.google.firebase.firestore.GeoPoint(48.4667, -71.1700),
//            "hours" to mapOf("start" to "10:00", "end" to "14:00"),
//            "duration" to 4,
//            "difficulty" to 2,
//            "required" to listOf("pagaie", "gilet de sauvetage"),
//            "pictures" to listOf("url_de_l_image_canoe_1.jpg", "url_de_l_image_canoe_2.jpg")
//        )
//    )
//
//    // Pousser les activités dans Firestore
//    activities.forEach { activity ->
//        db.collection("activities")
//            .add(activity)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Activity added successfully!")
//            }
//            .addOnFailureListener { exception ->
//                Log.w("Firestore", "Error adding activity", exception)
//            }
//    }
//}
