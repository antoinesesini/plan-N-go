package ca.uqac.etu.planngo.models

import org.osmdroid.util.GeoPoint


enum class ActivityType { MARCHE, SKI, FITNESS, RANDONNEE, KAYAK, FOOTBALL_AMERICAIN, SOCCER, YOGA, RUNNING, VELO, ESCALADE, NATATION, TENNIS, COMBAT, VOLLEYBALL, HANDBALL, HOCKEY, RAQUETTES, MOTONEIGE, CHIEN_DE_TRAINEAU, PECHE}

data class Activity(
    val name: String,
    val type: ActivityType,
    val description: String,
    val location: GeoPoint,
    val hours: Map<String, String>,
    val duration: Int,
    val difficulty: Int,
    val required: List<String>,
    val pictures: List<String>,
)