package se.metricspace.opendata.flight

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.*
import java.util.Locale

data class Flight(
    val altitudeMeters: Double,
    val callsign: String,
    val category: Int,
    val country: String,
    val geoAltitude: Double?,
    val icao24: String,
    val lastContact: Int?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val onGround: Boolean,
    val positionSource: Int?,
    val spi: Boolean,
    val squawk: String?,
    val timePosition: Int?,
    val trueTrackDegrees: Double,
    val velocityMs: Double,
    val verticalRate: Double?
)

class FlightService(private val httpClient: HttpClient, private val userAgent: String) {
    suspend fun getFlights(latitude: Double, longitude: Double): List<Flight> {
        return try {
            val lamin = "%.4f".format(Locale.US, latitude - 0.5)
            val lamax = "%.4f".format(Locale.US, latitude + 0.5)
            val lomin = "%.4f".format(Locale.US, longitude - 1.0)
            val lomax = "%.4f".format(Locale.US, longitude + 1.0)

            val url = "https://opensky-network.org/api/states/all?lamin=$lamin&lomin=$lomin&lamax=$lamax&lomax=$lomax"

            val flightJson = httpClient.get(url) {
                header(HttpHeaders.UserAgent, userAgent)
            }.bodyAsText()

            val root = Json.parseToJsonElement(flightJson).jsonObject
            val statesArray = root["states"]?.jsonArray ?: return emptyList()

            // mapNotNull filtrerar automatiskt bort null-värden om vi returnerar det i blocket
            statesArray.mapNotNull { state ->
                val data = state.jsonArray

                // Säkerhetskontroll: Returnera null för detta flygplan om datan är ofullständig
                if (data.size < 17) return@mapNotNull null

                Flight(
                    icao24 = data[0].jsonPrimitive.contentOrNull?.trim() ?: "Okänd",
                    callsign = data[1].jsonPrimitive.contentOrNull?.trim()?.takeIf { it.isNotEmpty() } ?: "Okänd",
                    country = data[2].jsonPrimitive.contentOrNull?.trim()?.takeIf { it.isNotEmpty() } ?: "Okänt",
                    timePosition = data[3].jsonPrimitive.intOrNull,
                    lastContact = data[4].jsonPrimitive.intOrNull,
                    longitude = data[5].jsonPrimitive.doubleOrNull,
                    latitude = data[6].jsonPrimitive.doubleOrNull,
                    altitudeMeters = data[7].jsonPrimitive.doubleOrNull ?: 0.0,
                    onGround = data[8].jsonPrimitive.booleanOrNull ?: false,
                    velocityMs = data[9].jsonPrimitive.doubleOrNull ?: 0.0,
                    trueTrackDegrees = data[10].jsonPrimitive.doubleOrNull ?: 0.0,
                    verticalRate = data[11].jsonPrimitive.doubleOrNull,
                    // index 12 är 'sensors' och utelämnas oftast
                    geoAltitude = data[13].jsonPrimitive.doubleOrNull,
                    squawk = data[14].jsonPrimitive.contentOrNull?.trim()?.takeIf { it.isNotEmpty() },
                    spi = data[15].jsonPrimitive.booleanOrNull ?: false,
                    positionSource = data[16].jsonPrimitive.intOrNull,
                    // Säkert sätt att plocka ut category om det finns (element 18 / index 17)
                    category = data.getOrNull(17)?.jsonPrimitive?.intOrNull ?: 0
                )
            }
        } catch (e: Exception) {
            println("❌ Error fetching flights: ${e.message}")
            emptyList()
        }
    }
}