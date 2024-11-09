package ca.uqac.etu.planngo.models

import org.osmdroid.util.GeoPoint


enum class ActivityType { MARCHE, SKI, FITNESS, RANDONNEE, KAYAK, FOOTBALL_AMERICAIN, SOCCER}


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