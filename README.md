# OpenData Flight Service ✈️

[![Kotlin](https://img.shields.io/badge/kotlin-2.3+-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Java](https://img.shields.io/badge/java-21+-orange.svg?logo=java)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-BSD_3--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

A modern, decoupled Kotlin service module for integrating real-time flight tracking into your JVM applications. 

This repository provides the `FlightService`, which fetches live air traffic data from the OpenSky Network. It is designed to be completely agnostic to your geocoding setup—as long as you can provide a latitude and a longitude, the service will return the flights in that area.

## Features

* **Decoupled Design:** Takes simple `Double` coordinates (latitude/longitude), meaning you can pair it with any location provider, map click event, or GPS sensor you prefer.
* **Real-Time Flight Tracking:** Streams live flight data (altitude, velocity, track, squawk codes, etc.) within a calculated bounding box around your coordinates.
* **Coroutines & Ktor:** Built on a modern, non-blocking asynchronous stack.

## Tech Stack

* **Language:** Kotlin 2.3+
* **Runtime:** Java 21+
* **Build Tool:** Gradle 9.6+
* **Libraries:** [Ktor Client](https://ktor.io/) (CIO, ContentNegotiation), [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization), [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines).

## Getting Started

### Prerequisites
* JDK 21+
* Gradle 9.6+

### 1. Clone the repository
```bash
git clone [https://github.com/mano3567/open-data-flights-kt.git](https://github.com/mano3567/open-data-flights-kt.git)
cd open-data-flights-kt
```

## Programmatic Usage

The `FlightService` is incredibly straightforward to use. You handle the location logic, and the service handles the planes.

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.coroutines.runBlocking
import se.metricspace.opendata.flight.FlightService

fun main() = runBlocking {
    // 1. Setup Ktor HttpClient
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // 2. Initialize the FlightService
    val flightService = FlightService(httpClient, userAgent = "my-awesome-app/1.0")

    // 3. Provide raw coordinates (e.g., from a map UI, GPS, or external API)
    val latitude = 59.3293
    val longitude = 18.0686
    
    // 4. Fetch the flights
    val flights = flightService.getFlights(latitude, longitude)

    println("Found ${flights.size} flights in the area!")
    flights.forEach { flight ->
        println("${flight.callsign} - Altitude: ${flight.altitudeMeters}m")
    }
}
```

## Sample CLI Application

To demonstrate how this service can be used in a real-world scenario, this repository includes an interactive Command-Line Interface (CLI) application. 

The CLI pulls in a separate, external Geolocation service via **JitPack** to handle place name lookups. It then feeds the resulting coordinates directly into the `FlightService`.

To run the sample application:
```bash
./gradlew run
```

### Example Output
```text
--- Välkommen till GeoService CLI ---
Skriv namnet på en plats för att slå upp den, eller 'q' för att avsluta.

1. Välj plats
2. Visa plan i närheten
Q. Avsluta
Välj ett alternativ: 1
Ange plats: Solna

Resultat: Solna, Stockholm County, Sweden
Lat/Lon: 59.3600, 18.0000

Välj ett alternativ: 2
3 flights
 Flight: SAS123 (4a3b21)
   Country: Sweden
   Position: 59.3651, 18.0123
   Altitude: 4500.0 m, GeoAltitude: 4650.0 m
   On Ground: false, Velocity: 150.5 m/s, True Track: 180.0° -> S 
```

## API Credits

* **Flight Data:** Provided by the [OpenSky Network](https://opensky-network.org/).

## License

This project is licensed under the BSD 3-Clause License - see the [LICENSE](LICENSE) file for details.

