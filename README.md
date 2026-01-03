# Swiss Ski Resorts

A full-stack application for exploring Swiss ski resorts, their lifts, and elevation profiles with **real data from OpenStreetMap**.

## Features

- **Ski Resort Listing**: Browse all Swiss ski resorts with details including:
  - Drive time from Zurich
  - Number of blue, red, and black slopes
  - Elevation range
  - Total slope kilometers

- **Filtering & Search**:
  - Search resorts by name or region
  - Filter by maximum drive time from Zurich
  - Sort by name, drive time, slope count, or elevation

- **Lift Information**: Click on any resort to view detailed lift information:
  - Lift type (Gondola, Chairlift, Cable Car, Drag Lift, Funicular)
  - Start and end elevations
  - Length and capacity
  - GPS coordinates

- **Elevation Profiles**: Interactive SVG-based elevation charts showing:
  - Elevation gain along the lift
  - Distance markers
  - Hover tooltips with exact elevation data

- **Real Data Import**: Fetch real-time data from OpenStreetMap:
  - Ski lifts (aerialways) from OSM Overpass API
  - Ski pistes/slopes with difficulty ratings
  - Drive times calculated via OSRM routing
  - Elevation profiles from Open Elevation API

## Tech Stack

### Backend
- **Java 21** with **Spring Boot 3.2**
- **Spring Data JPA** with H2 in-memory database
- **Spring WebFlux** for reactive API calls
- **Lombok** for boilerplate reduction
- Integration with:
  - **OpenStreetMap Overpass API** for ski lift and piste data
  - **OSRM** (Open Source Routing Machine) for drive time calculations
  - **Open Elevation API** for elevation profiles

### Frontend
- **Angular 19** with standalone components
- **Angular Signals** and **rxResource** for reactive state management
- **SCSS** for styling
- **SVG** for elevation profile visualization

## Project Structure

```
ski-lifts/
├── backend/                    # Spring Boot REST API
│   └── src/main/java/com/skiresorts/
│       ├── controller/         # REST endpoints
│       │   ├── SkiResortController.java
│       │   └── DataImportController.java
│       ├── service/
│       │   ├── SkiResortService.java
│       │   ├── DataImportService.java      # OSM data import
│       │   ├── OverpassApiService.java     # OSM Overpass API
│       │   ├── RoutingService.java         # OSRM routing
│       │   └── OpenElevationService.java   # Elevation API
│       ├── repository/         # Data access
│       ├── model/              # Entities and DTOs
│       └── config/             # Configuration
│
└── frontend/                   # Angular application
    └── src/app/
        ├── components/         # UI components
        │   ├── resort-list/    # Resort listing page
        │   ├── resort-detail/  # Resort detail page
        │   ├── lift-card/      # Lift information card
        │   └── elevation-chart/# Elevation profile visualization
        ├── services/           # API communication
        └── models/             # TypeScript interfaces
```

## API Endpoints

### Resort Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/resorts` | List all ski resorts |
| GET | `/api/resorts/{id}` | Get resort details with lifts |
| GET | `/api/resorts/region/{region}` | Get resorts by region |
| GET | `/api/resorts/drive-time?maxMinutes={n}` | Get resorts by max drive time |
| GET | `/api/resorts/search?q={query}` | Search resorts |
| GET | `/api/resorts/{resortId}/lifts` | Get lifts for a resort |
| GET | `/api/resorts/lifts/{liftId}` | Get lift details |
| POST | `/api/resorts/lifts/{liftId}/elevation-profile` | Fetch elevation profile |

### Data Import Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/import/all` | Import all known Swiss ski resorts from OSM |
| POST | `/api/import/resort?name={name}` | Import a specific resort by name |
| POST | `/api/import/resort/{id}/lifts` | Re-import lifts for an existing resort |
| POST | `/api/import/resort/{id}/elevations` | Fetch elevation profiles for all lifts |
| GET | `/api/import/preview/lifts?lat={lat}&lon={lon}&radius={m}` | Preview lifts from OSM |
| GET | `/api/import/preview/pistes?minLat=...&maxLat=...` | Preview pistes from OSM |
| GET | `/api/import/search?name={name}` | Search ski areas in OSM |

## Getting Started

### Prerequisites
- Java 21+
- Node.js 18+
- npm or yarn

### Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Running the Frontend

```bash
cd frontend
npm install
npm start
```

The app will be available at `http://localhost:4200`

## Importing Real Data from OpenStreetMap

The application starts with sample data. To import **real data** from OpenStreetMap:

### Option 1: Import All Resorts (Recommended)

```bash
curl -X POST http://localhost:8080/api/import/all
```

This will:
1. Fetch ski lifts for 20 major Swiss ski resorts from OpenStreetMap
2. Count ski pistes by difficulty (blue, red, black)
3. Calculate drive times from Zurich using OSRM
4. Store everything in the database

**Note**: This takes several minutes due to API rate limiting.

### Option 2: Import Individual Resorts

```bash
# Import a specific resort
curl -X POST "http://localhost:8080/api/import/resort?name=Zermatt"

# Re-import lifts for an existing resort
curl -X POST http://localhost:8080/api/import/resort/1/lifts

# Fetch elevation profiles for all lifts
curl -X POST http://localhost:8080/api/import/resort/1/elevations
```

### Option 3: Preview OSM Data

```bash
# Preview lifts near a location (Zermatt)
curl "http://localhost:8080/api/import/preview/lifts?lat=46.0207&lon=7.7491&radius=5000"

# Search for ski areas by name
curl "http://localhost:8080/api/import/search?name=Verbier"
```

## Data Sources

### OpenStreetMap (Overpass API)
The primary data source for ski lifts and pistes. The application queries:
- `aerialway=*` for ski lifts (gondola, chair_lift, cable_car, drag_lift, etc.)
- `piste:type=downhill` with `piste:difficulty` for slopes

**Overpass API**: https://overpass-api.de/

### OSRM (Open Source Routing Machine)
Used to calculate driving times from Zurich to each ski resort.

**Public Demo Server**: https://router.project-osrm.org/

### Open Elevation API
Provides elevation data for lift profiles based on GPS coordinates.

**API**: https://api.open-elevation.com/

### Supported Swiss Ski Resorts

The import service knows about these major Swiss ski resorts:
- Zermatt
- Arosa Lenzerheide
- St. Moritz
- Verbier
- Davos Klosters
- Laax / Flims
- Engelberg-Titlis
- Grindelwald / Wengen
- Saas-Fee
- Crans-Montana
- Adelboden / Lenk
- Gstaad
- Andermatt
- Villars
- Champéry
- Nendaz
- Leukerbad

## Angular Signals & Resources

This application demonstrates modern Angular patterns:

### Signals
```typescript
// Reactive state with signals
searchQuery = signal('');
maxDriveTime = signal<number | null>(null);

// Computed values derived from signals
filteredResorts = computed(() => {
  const resorts = this.resortsResource.value() ?? [];
  const query = this.searchQuery().toLowerCase();
  return resorts.filter(r => r.name.toLowerCase().includes(query));
});
```

### rxResource
```typescript
// Declarative data fetching with rxResource
resortsResource = rxResource({
  loader: () => this.skiResortService.getAllResorts()
});

// With reactive parameters
resortResource = rxResource({
  request: () => this.resortId,
  loader: ({ request: id }) => this.skiResortService.getResortById(id)
});
```

## Screenshots

The application displays:
1. A grid of ski resort cards with key statistics
2. Detailed resort view with slope distribution visualization
3. Lift cards with elevation data and expandable details
4. Interactive SVG elevation profile charts

## API Rate Limiting

The application respects rate limits of public APIs:
- **Overpass API**: 2 second delay between requests
- **Open Elevation API**: 0.5 second delay between requests
- **OSRM**: No explicit rate limiting, but used sparingly

For production use, consider hosting your own instances of these services.

## License

MIT
