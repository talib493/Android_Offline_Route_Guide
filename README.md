# Offline Route Guide

Offline Route Guide is an Android navigation project I'm building to explore a problem that ordinary navigation apps don't always handle well: **what happens when you lose cellular connectivity while navigating in a remote area?**

The idea is to let users navigate even when their internet connection becomes unreliable or disappears completely. Rather than depending entirely on downloading map data when it is needed, the app aims to prepare the necessary map and route data in advance and keep it available locally.

The project is still a work in progress. Online map rendering and routing are implemented, while the offline map caching and navigation system are under development.

## The Goal

The goal is to build a navigation app that can prepare for connectivity loss before it happens.

My current approach is to use the user's location to determine a geographic bounding box of approximately 20 km around them, then cache the relevant map data. As the user moves, the app will compare the previously cached area with the new area and update the cache instead of downloading everything again.

For route navigation, the aim is to preserve the necessary route data and support turn-by-turn guidance even when the user can no longer reach the routing service.

## Current Features

- **Map rendering:** Uses MapLibre as the map rendering engine.
- **OpenStreetMap-based map data:** Uses Protomaps as part of the map-data setup.
- **Online route calculation:** Uses Stadia Maps for route generation.
- **Turn-by-turn navigation:** Processes route instructions returned by the routing service.
- **Geocoding and reverse geocoding:** Uses Photon to search for places and convert between geographic coordinates and place information.
- **Location tracking:** Tracks the user's current geographic position.
- **Route polyline processing:** Works with decoded route geometry to determine the user's progress along a route.

## Tech Stack

- **Kotlin** — Main programming language.
- **Jetpack Compose** — Android UI development.
- **MapLibre** — Map rendering.
- **OpenStreetMap data** — Underlying map data.
- **Protomaps** — Map-data technology being explored for the offline implementation.
- **Stadia Maps** — Online routing and turn-by-turn route information.
- **Photon** — Geocoding and reverse geocoding.
- **WorkManager** — Planned periodic cache maintenance and location-area checks.

## The Offline Approach

Offline support is the main part of this project that I'm currently working on.

The intended system has three main parts.

### 1. Location-Based Map Cache

The app will use the user's current location to determine the geographic area that should be available offline.

The initial plan is to cache map data for a bounding box covering roughly 20 km around the user's location. WorkManager is intended to periodically check the user's location and compare the new area with the previously cached bounding box.

Instead of replacing all cached data every time, the cache should identify which map tiles are new or no longer needed and update only the necessary data.

The objective is to reduce unnecessary downloads, storage usage, and repeated processing.

### 2. Route Data and Offline Navigation

Caching the map alone is not enough. A navigation app also needs route geometry, navigation instructions, and a reliable way to determine where the user is along the route.

The plan is to retain the necessary route data locally so that navigation can continue when connectivity is lost. The exact caching and tile-provider implementation is still being worked on.

### 3. Finding a Suitable Offline Tile Provider

One of the biggest challenges has been finding a map and route data solution that works with MapLibre while also allowing the required data to be downloaded and stored in advance.

Not every map-data provider permits unrestricted pre-downloading or offline storage. Provider policies and technical compatibility therefore both matter.

I'm exploring a workable approach that respects the provider's terms while making local map data available for offline use.

## Working on Route Progress and Bearing

Another major part of the project is implementing my own algorithm for determining the user's position relative to the route polyline.

A navigation system needs more than the user's GPS coordinates. It needs to determine which part of the route the user is currently following, estimate progress along that route, and identify when the next maneuver is approaching.

I'm working on an algorithm that will:

- Find the closest relevant segment of the route polyline to the user's current location.
- Determine progress along the route rather than relying only on the closest individual coordinate.
- Identify when the user has reached the next navigation step.
- Calculate the direction of travel along the route using its geometry.
- Determine the bearing from the current route step toward the next step for navigation and camera orientation.

This work is particularly important because the route response does not always provide the bearing information I need. Calculating it from the route geometry gives me greater control over how the navigation camera and next-turn guidance behave.

The goal is to make navigation follow the route's direction rather than letting noisy GPS movement alone dictate the camera bearing.

## Current Development Status

**Online navigation is implemented. Offline functionality is still under development.**

| Component | Status |
|---|---|
| MapLibre map rendering | Implemented |
| Online routing with Stadia Maps | Implemented |
| Photon geocoding and reverse geocoding | Implemented |
| Location tracking | Implemented |
| Route geometry processing | In progress |
| Custom route progress and bearing algorithm | In progress |
| Offline-compatible map and route data provider | Under investigation |
| Location-based smart tile caching | Planned / in development |
| Periodic cache updates with WorkManager | Planned / in development |
| Reliable turn-by-turn navigation without connectivity | Goal; not yet complete |

## Why This Project Takes Time

The difficult part isn't displaying a map or requesting a route. Those pieces already work online.

The challenge is making the different components work together when the network is unavailable: obtaining suitable offline map data, managing the cache efficiently, preserving route information, and calculating reliable navigation instructions from GPS updates and route geometry.

I'm developing this as a learning and portfolio project, so I'm working through these problems directly instead of treating offline navigation as a simple map-download feature.

## What's Next

My current priorities are:

1. Finalize a MapLibre-compatible offline map-data solution that permits the required pre-downloading.
2. Design the location-based tile cache and update logic.
3. Implement and test route progress detection using route segments.
4. Improve turn detection and bearing calculation from route geometry.
5. Connect offline map availability and cached route data into a more complete offline navigation workflow.
6. Test how the app behaves when internet connectivity disappears during a trip.

## Project Status

This project is under active development. The online map and routing workflow is functional, but complete offline navigation is not yet available.

The long-term objective is to make navigation more resilient in areas where cellular connectivity is weak or unavailable, by preparing map and route data ahead of time and handling navigation locally wherever possible.

