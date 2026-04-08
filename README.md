# Musiq

A personal music analytics web app that connects to your Spotify account and gives you a rich, filterable view of your listening history — top tracks, top artists, and recently played — all stored locally in your own database.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Spotify App Setup](#spotify-app-setup)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Environment Variables](#environment-variables)
- [API Reference](#api-reference)
- [Database Schema](#database-schema)
- [How It Works](#how-it-works)

---

## Features

- **Spotify OAuth Login** — Authenticate securely via Spotify's authorization code flow
- **Profile Header** — Displays your Spotify display name, email, and avatar
- **Recently Played** — Horizontal scroll strip of your latest tracks with relative timestamps
- **Top Tracks** — Ranked list of your most-played tracks, filterable by time range
- **Top Artists** — Photo grid of your most-played artists with a 2×2 / 4×4 layout toggle
- **Time Range Selector** — Shared filter across Top Tracks and Top Artists with:
  - Presets: Day, Week, Month, Year, All Time
  - By Month picker (navigate years, select any month)
  - Custom date range (from / to date inputs)
- **Fully Responsive** — Works on desktop, tablet, and mobile; artist grid collapses on small screens
- **JWT Authentication** — Stateless token-based auth; JWT stored in `localStorage`
- **Local Data Storage** — All Spotify data is synced and persisted in a local PostgreSQL database

---

## Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 17 | Language |
| Spring Boot 3.5 | Application framework |
| Spring Security | JWT authentication, CORS |
| Spring Data JPA / Hibernate | ORM and database access |
| Spring WebFlux (`WebClient`) | Reactive HTTP client for Spotify API |
| PostgreSQL | Relational database |
| JJWT 0.12.6 | JWT generation and validation |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

### Frontend
| Technology | Purpose |
|---|---|
| React 18 + TypeScript | UI framework |
| Vite | Build tool and dev server |
| React Router v6 | Client-side routing |
| TanStack Query (React Query) | Server state management and caching |
| Axios | HTTP client |
| CSS Modules | Scoped component styles |
| CSS Custom Properties | Design token system |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                     Browser (React)                      │
│  Landing → Spotify OAuth → Callback → Profile           │
│  TanStack Query caches API responses per time range     │
└──────────────────────┬──────────────────────────────────┘
                       │ JWT in Authorization header
                       │ REST (JSON)
┌──────────────────────▼──────────────────────────────────┐
│               Spring Boot Backend (:8080)                │
│                                                          │
│  SpotifyAuthController  →  AuthService                   │
│       ↓ OAuth callback        ↓ sync on login            │
│  UserController         →  SpotifySyncService            │
│  /api/me/**                   ↓                          │
│                         SpotifyService (WebClient)       │
│                               ↓                          │
│                         Spotify Web API                  │
│                                                          │
│  JwtAuthenticationFilter (all /api/** routes)           │
└──────────────────────┬──────────────────────────────────┘
                       │ JPA / Hibernate
┌──────────────────────▼──────────────────────────────────┐
│              PostgreSQL (musiqdb)                        │
│  users · songs · artists · albums · play_records        │
└─────────────────────────────────────────────────────────┘
```

**Data flow on login:**
1. User clicks "Login with Spotify" → frontend calls `GET /auth/spotify/login`
2. Backend redirects browser to Spotify's authorization page
3. After user approves, Spotify redirects to `GET /auth/spotify/callback?code=...`
4. Backend exchanges code for tokens, fetches user profile, creates/updates user in DB
5. `SpotifySyncService.syncRecentlyPlayed()` is triggered — fetches last 50 plays from Spotify, persists songs, artists, albums, and play records
6. JWT is generated and the browser is redirected to `http://localhost:5173/callback?token=...`
7. Frontend stores the JWT and navigates to `/profile`

---

## Project Structure

```
new-musiq/
├── backend/
│   └── musiq/
│       ├── pom.xml
│       └── src/main/java/com/musiq/
│           ├── MusiqApplication.java
│           ├── auth/
│           │   ├── controller/SpotifyAuthController.java   # OAuth endpoints
│           │   ├── dto/AuthResponseDto.java
│           │   └── service/
│           │       ├── AuthService.java                   # Login flow orchestration
│           │       └── JwtService.java                    # JWT generation/validation
│           ├── config/
│           │   └── WebClientConfig.java                   # WebClient bean
│           ├── listening/
│           │   ├── PlayRecord.java                        # Entity: user × song × timestamp
│           │   └── PlayRecordRepository.java              # JPQL top-N queries
│           ├── security/
│           │   ├── JwtAuthenticationFilter.java           # Extracts JWT from requests
│           │   └── SecurityConfig.java                    # CORS + security rules
│           ├── spotify/
│           │   ├── config/SpotifyProperties.java          # Binds spotify.* properties
│           │   ├── dto/                                   # Spotify API response records
│           │   └── service/SpotifyService.java            # Calls Spotify Web API
│           ├── sync/
│           │   ├── SpotifySyncService.java                # Upserts tracks/artists/albums/records
│           │   └── SpotifySyncScheduler.java              # Scheduled background sync
│           ├── track/
│           │   ├── Album.java / Artist.java / Song.java   # JPA entities
│           │   └── *Repository.java
│           └── user/
│               ├── User.java                              # JPA entity + UserDetails
│               ├── UserRepository.java
│               ├── controller/
│               │   ├── UserController.java                # /api/me/** endpoints
│               │   └── TestController.java                # Dev helper
│               └── dto/                                   # Response DTOs
│
└── frontend/
    └── src/
        ├── api/
        │   ├── musiqClient.ts          # Axios instance with JWT interceptor
        │   └── userApi.ts              # Typed fetch functions + TimeRange type
        ├── components/
        │   ├── Navbar.tsx              # Sticky top bar with login/logout
        │   └── profile/
        │       ├── ProfileHeader.tsx   # Avatar + name + email
        │       ├── RecentlyPlayed.tsx  # Horizontal card strip
        │       ├── TopTrackList.tsx    # Ranked track list with scroll
        │       ├── TopArtistsList.tsx  # 2×2 / 4×4 photo grid
        │       └── TimeRangeSelector.tsx  # Preset + custom date picker
        ├── context/
        │   └── AuthContext.tsx         # isAuthenticated, token, logout
        ├── pages/
        │   ├── LandingPage.tsx         # Marketing page with Spotify CTA
        │   ├── CallbackPage.tsx        # Receives JWT from redirect, stores it
        │   └── ProfilePage.tsx         # Main profile view with shared time range
        ├── routes/
        │   └── AppRoutes.tsx           # React Router route definitions
        └── styles/
            └── shared.css             # Global @keyframes, skeleton, design tokens
```

---

## Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+** and **npm**
- **PostgreSQL 14+** running locally
- A **Spotify Developer** account

---

### Spotify App Setup

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Click **Create App**
3. Set the **Redirect URI** to: `http://127.0.0.1:8080/auth/spotify/callback`
4. Note your **Client ID** and **Client Secret**
5. Under **APIs used**, enable **Web API**

---

### Backend Setup

1. **Create the database:**
   ```sql
   CREATE DATABASE musiqdb;
   ```

2. **Set environment variables** (or create a `.env` file — see [Environment Variables](#environment-variables)):
   ```powershell
   $env:SPOTIFY_CLIENT_ID="your_client_id"
   $env:SPOTIFY_CLIENT_SECRET="your_client_secret"
   ```

3. **Run the backend:**
   ```bash
   cd backend/musiq
   ./mvnw spring-boot:run
   ```
   The server starts on `http://localhost:8080`.  
   Hibernate will auto-create all tables on first run (`ddl-auto=update`).

---

### Frontend Setup

1. **Install dependencies:**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the dev server:**
   ```bash
   npm run dev
   ```
   The app runs at `http://localhost:5173`.

3. Open `http://localhost:5173` in your browser and click **Connect with Spotify**.

---

## Environment Variables

### Backend (`application.properties` / system environment)

| Variable | Description | Default |
|---|---|---|
| `SPOTIFY_CLIENT_ID` | Your Spotify app's Client ID | — |
| `SPOTIFY_CLIENT_SECRET` | Your Spotify app's Client Secret | — |
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/musiqdb` |
| `spring.datasource.username` | DB username | `postgres` |
| `spring.datasource.password` | DB password | `postgres` |
| `jwt.expiration` | JWT validity in milliseconds | `86400000` (24 h) |
| `app.frontend-url` | Frontend origin for redirects | `http://localhost:5173` |

> `SPOTIFY_CLIENT_ID` and `SPOTIFY_CLIENT_SECRET` must be set as real environment variables — they are referenced with `${...}` in `application.properties` and have no defaults.

---

## API Reference

All endpoints under `/api/**` require a valid JWT in the `Authorization: Bearer <token>` header.

### Auth

| Method | Path | Description |
|---|---|---|
| `GET` | `/auth/spotify/login` | Redirects browser to Spotify authorization page |
| `GET` | `/auth/spotify/callback` | Handles OAuth callback; redirects to frontend with `?token=` |

### User Profile

| Method | Path | Params | Description |
|---|---|---|---|
| `GET` | `/api/me` | — | Returns authenticated user's profile |
| `GET` | `/api/me/top-tracks` | `range` or `from`+`to` | Top tracks ranked by play count |
| `GET` | `/api/me/top-artists` | `range` or `from`+`to` | Top artists ranked by play count |
| `GET` | `/api/me/recently-played` | — | Last 50 play records, newest first |

#### Time range parameters

Use **either** `range` (preset) **or** `from` + `to` (custom), not both.

| `range` value | Window |
|---|---|
| `day` | Last 24 hours |
| `week` | Last 7 days |
| `month` | Last 30 days |
| `year` | Last 365 days |
| `all_time` | All records |

For custom ranges, pass ISO-8601 timestamps:
```
GET /api/me/top-tracks?from=2025-01-01T00:00:00Z&to=2025-01-31T23:59:59Z
```

---

## Database Schema

```
users
  id (PK), spotify_id, display_name, email, avatar_url,
  access_token, refresh_token, expires_at,
  created_at, updated_at

albums
  id (PK), spotify_id (UNIQUE), name, image_url, release_date

artists
  id (PK), spotify_id (UNIQUE), name, image_url

songs
  id (PK), spotify_id (UNIQUE), name, duration_ms, preview_url,
  explicit, image_url, album_id (FK → albums)

track_artists              ← join table
  song_id (FK → songs), artist_id (FK → artists)

play_records
  id (PK), user_id (FK → users), song_id (FK → songs), played_at
```

---

## How It Works

### Authentication
Spring Security is configured as **stateless** — no HTTP sessions. Every request to `/api/**` passes through `JwtAuthenticationFilter`, which extracts the Bearer token, validates it with `JwtService`, loads the `User` entity, and places it in the `SecurityContext`. Controllers call `SecurityContextHolder.getContext().getAuthentication().getPrincipal()` to get the current user.

### Data Sync
`SpotifySyncService` uses Spring's `WebClient` to call the Spotify Web API. It upserts (find-or-create-and-update) albums, artists, and songs to avoid duplicates. `syncRecentlyPlayed` additionally creates `PlayRecord` rows only if an identical `(user, song, playedAt)` triple doesn't already exist — preventing duplicates across repeated syncs.

### Top Items Queries
Top tracks and artists are derived from `play_records` using JPQL aggregate queries:
```sql
-- Top tracks
SELECT pr.song FROM PlayRecord pr
WHERE pr.user = :user AND pr.playedAt > :from AND pr.playedAt <= :to
GROUP BY pr.song ORDER BY COUNT(pr) DESC

-- Top artists
SELECT a FROM PlayRecord pr JOIN pr.song.artists a
WHERE pr.user = :user AND pr.playedAt > :from AND pr.playedAt <= :to
GROUP BY a ORDER BY COUNT(pr) DESC
```
The more play records accumulated over time, the more meaningful the "top" rankings become.

### Frontend Caching
TanStack Query caches each `(endpoint, timeRangeKey)` pair. Switching between time ranges re-uses cached results instantly, only re-fetching when the cache is stale. The `timeRangeKey` function in `userApi.ts` generates a stable string key from both preset and custom ranges.
