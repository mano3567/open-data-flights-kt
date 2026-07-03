package sample.flight.cli

import se.metricspace.opendata.geolocation.GeoLocationService
import se.metricspace.opendata.flight.FlightService
import kotlinx.coroutines.runBlocking
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import se.metricspace.opendata.flight.Flight
import se.metricspace.opendata.geolocation.Location
import kotlin.math.roundToInt

fun Double.toCompassDirection(): String {
    val directions = arrayOf("N", "NO", "O", "SO", "S", "SV", "V", "NV")
    val index = ((this % 360) / 45.0).roundToInt()
    return directions[index % 8]
}

fun main() {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val geoLocationService = GeoLocationService(httpClient, userAgent = "metricspace.flights/0.1.0", listOf("se", "no", "dk", "fi"))
    val flightService = FlightService(httpClient, userAgent = "metricspace.flights/0.1.0")

    println("--- Välkommen till GeoService CLI ---")
    println("Skriv namnet på en plats för att slå upp den, eller 'q' för att avsluta.")

    var location: Location? = null

    runBlocking {
        while (true) {
            if(null == location) {
                println("Ingen plats vald ...")
            } else {
                println("Vald plats: ${location.displayName}")
                println("${location.latitude}, ${location.longitude}")
            }
            println("1. Välj plats")
            if(null != location) {
                println("2. Visa plan i närheten")
            }
            println("Q. Avsluta")
            print("Välj ett alternativ: ")

            when (readlnOrNull()?.trim()) {
                "1" -> {
                    print("Ange plats (till exempel Sergels torg i stockholm) ")
                    val somePlace = readlnOrNull()?.trim() ?: ""
                    val newLocation = geoLocationService.findLocation(somePlace)
                    if(newLocation != null) {
                        println("Resultat för '${somePlace}'")
                        location = newLocation
                        showLocation(newLocation)
                    } else {
                        println("Kunde inte hitta plats för '${somePlace}'")
                    }
                }
                "2"-> {
                    if (location == null) {
                        println("❌ Ingen plats vald. Välj en plats först.")
                    } else {
                        val flights = flightService.getFlights(location.latitude, location.longitude)
                        println("${flights.size} flights")
                        flights.forEach { flight  ->
                            showFlight(flight)
                        }
                    }
                }
                "Q", "q", "A", "a" -> {
                    println("Avslutar... Hejdå! 👋")
                    break
                }
                else -> {
                    println("❌ Förstod inte det där. Försök igen ...")
                }
            }

        }
    }
}

fun showFlight(flight: Flight) {
    println(" Flight: ${flight.callsign} (${flight.icao24})")
    println("  Country: ${flight.country}")
    println("  Position: ${flight.latitude}, ${flight.longitude}")
    println("  Altitude: ${flight.altitudeMeters} m, GeoAltitude: ${flight.geoAltitude} m")
    println("  On Ground: ${flight.onGround}, Velocity: ${flight.velocityMs} m/s, True Track: ${flight.trueTrackDegrees}° -> ${flight.trueTrackDegrees.toCompassDirection()} ")
    println("  Vertical Rate: ${flight.verticalRate} m/s, Squawk: ${flight.squawk}, SPI: ${flight.spi}, Position Source: ${flight.positionSource}")
}

fun showLocation(location: se.metricspace.opendata.geolocation.Location) {
    println("Resultat: ${location.displayName}")
    println("Name: ${location.name}")
    println("Lat/Lon: ${location.latitude}, ${location.longitude}")
    println("BoundingBox: ${location.boundingBox}")
    println("Licence: ${location.licence}")
    println("Importance: ${location.importance}")
    println("PlaceRank: ${location.placeRank}")
}