package ca.uqac.etu.planngo.models

data class DayPlan(
    val date: String,
    val startTime: String,
    val activities: List<String>
)
