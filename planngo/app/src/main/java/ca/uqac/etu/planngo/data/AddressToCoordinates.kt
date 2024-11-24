package ca.uqac.etu.planngo.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class AddressToCoordinates {

    // Initialisation du client HTTP OkHttp
    private val client = OkHttpClient()

    // Fonction pour convertir une adresse en coordonnées (latitude, longitude) via une API OpenStreetMap
    suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            // Préparation de l'adresse pour l'intégrer à l'URL (remplacement des espaces par "+")
            val urlAddress = address.replace(" ", "+")
            val url = "https://nominatim.openstreetmap.org/search?q=$urlAddress&format=json&addressdetails=1&limit=1"
            Log.d("ADDRESS (Url API):", url)

            // Construction de la requête HTTP
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                // Envoi de la requête et réception de la réponse
                val response = client.newCall(request).execute()
                Log.d("ADDRESS (Response):", response.toString())

                // Vérification de la validité de la réponse
                if (response.isSuccessful) {
                    response.body?.string()?.let { json ->
                        // Conversion de la réponse JSON en tableau
                        val jsonArray = JSONArray(json)
                        if (jsonArray.length() > 0) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            // Extraction de la latitude et de la longitude
                            val lat = jsonObject.getDouble("lat")
                            val lon = jsonObject.getDouble("lon")
                            return@withContext Pair(lat, lon) // Retourne les coordonnées sous forme de paire
                        }
                    }
                }
                null // Retourne null si la réponse n'est pas valide ou si aucun résultat
            } catch (e: Exception) {
                // Gestion des exceptions éventuelles
                e.printStackTrace()
                null
            }
        }
    }
}

