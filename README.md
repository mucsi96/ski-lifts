# Swiss Ski Resorts

A full-stack application for exploring Swiss ski resorts, their lifts, and elevation profiles.

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

## Tech Stack

### Backend
- **Java 21** with **Spring Boot 3.2**
- **Spring Data JPA** with H2 in-memory database
- **Spring WebFlux** for reactive API calls
- **Lombok** for boilerplate reduction
- Integration with **Open Elevation API** for elevation data

### Frontend
- **Angular 18** with standalone components
- **Angular Signals** and **rxResource** for reactive state management
- **SCSS** for styling
- **SVG** for elevation profile visualization

## Project Structure

```
ski-lifts/
├── backend/                    # Spring Boot REST API
│   └── src/main/java/com/skiresorts/
│       ├── controller/         # REST endpoints
│       ├── service/            # Business logic
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

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/resorts` | List all ski resorts |
| GET | `/api/resorts/{id}` | Get resort details with lifts |
| GET | `/api/resorts/region/{region}` | Get resorts by region |
| GET | `/api/resorts/drive-time?maxMinutes={n}` | Get resorts by max drive time |
| GET | `/api/resorts/search?q={query}` | Search resorts |
| GET | `/api/resorts/{resortId}/lifts` | Get lifts for a resort |
| GET | `/api/resorts/lifts/{liftId}` | Get lift details |
| POST | `/api/resorts/lifts/{liftId}/elevation-profile` | Fetch elevation profile from Open Elevation API |

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

## Data Sources

### Ski Resort Data
The application includes sample data for major Swiss ski resorts:
- Arosa Lenzerheide
- Zermatt
- St. Moritz
- Verbier
- Davos Klosters
- Laax
- Engelberg-Titlis
- Jungfrau Region
- Saas-Fee
- Adelboden-Lenk
- Crans-Montana
- Flims Laax Falera

### Elevation Data
Elevation profiles are fetched from the [Open Elevation API](https://open-elevation.com/), which provides elevation data based on GPS coordinates. The system interpolates points along each lift's path and fetches elevation data for each point.

### OpenStreetMap Integration
Lift coordinates and metadata can be sourced from OpenStreetMap. The `osmWayId` and `osmRelationId` fields in the data model support linking to OSM data.

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

## License

MIT
