package ca.uqac.etu.planngo.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class AddressToCoordinates {

    private val client = OkHttpClient()

    suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            // Remplacer les espaces par des "+" pour l'URL
            val urlAddress = address.replace(" ", "+")
            val url = "https://nominatim.openstreetmap.org/search?q=$urlAddress&format=json&addressdetails=1&limit=1"
            Log.d("ADDRESS (Url API):", url)

            // Construire la requête HTTP
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                // Exécuter la requête sur un thread d'arrière-plan
                val response = client.newCall(request).execute()
                Log.d("ADDRESS (Response):", response.toString())

                // Vérifier si la réponse est valide
                if (response.isSuccessful) {
                    response.body?.string()?.let { json ->
                        val jsonArray = JSONArray(json)
                        if (jsonArray.length() > 0) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            val lat = jsonObject.getDouble("lat")
                            val lon = jsonObject.getDouble("lon")
                            return@withContext Pair(lat, lon)
                        }
                    }
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
