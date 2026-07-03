# OpenData Flight Tracker CLI ✈️

[![Kotlin](https://img.shields.io/badge/kotlin-2.3+-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Java](https://img.shields.io/badge/java-21+-orange.svg?logo=java)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-BSD_3--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

A modern Kotlin command-line application that allows you to look up geographical locations and discover real-time air traffic in that specific area.

This project consumes open data APIs to fetch geolocation coordinates and streams live flight data (altitude, velocity, track, etc.) from the OpenSky Network.

## Features

* **Interactive CLI:** A simple, prompt-driven interface to manage your queries.
* **Geocoding:** Look up places (e.g., "Sergels torg i Stockholm") and retrieve their exact latitude, longitude, and bounding box.
* **Real-Time Flight Tracking:** Discover planes currently flying within a defined radius of your chosen location.
* **Rich Flight Data:** Calculates and formats true track degrees into readable compass directions (e.g., N, NE, S, SW) and displays altitude, velocity, and squawk codes.

## Tech Stack

This project is built with modern JVM technologies:

* **Language:** Kotlin 2.3+
* **Runtime:** Java 21+
* **Build Tool:** Gradle 9.6+
* **Libraries:**
    * [Ktor Client](https://ktor.io/) (CIO Engine, ContentNegotiation) for asynchronous HTTP requests.
    * [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for parsing JSON responses.
    * [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) for non-blocking operations.

## Prerequisites

To build and run this project, ensure you have the following installed on your system:

* [JDK 21](https://adoptium.net/) or higher.
* [Gradle 9.6](https://gradle.org/install/) or higher (using the Gradle Wrapper is recommended).

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/mano3567/open-data-flights-kt.git](https://github.com/mano3567/open-data-flights-kt.git)
   cd open-data-flights-kt