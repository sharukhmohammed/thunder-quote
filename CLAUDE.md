# CLAUDE.md — Thunder Quote Android App

## Project Overview

Thunder Quote is a modern Android application that displays inspirational quotes. It features a paginated quote list, favorites management, daily quote notifications, and Material 3 theming with dark mode support.

**Tech stack**: Kotlin, Jetpack Compose, Room, Paging 3, WorkManager, Retrofit, Kotlinx Serialization, Navigation Compose

---

## Repository Structure

```
thunder-quote/
├── app/
│   ├── src/main/java/com/sharukh/thunderquote/
│   │   ├── app/              # Application class & JSON module singleton
│   │   ├── common/           # Shared base composables
│   │   ├── db/               # Room database, DAO
│   │   ├── di/               # Service locator (DI)
│   │   ├── model/            # Data models (entity + JSON)
│   │   ├── navigation/       # Sealed class route definitions
│   │   ├── notification/     # Notification channel setup & posting
│   │   ├── repo/             # Repository layer
│   │   ├── ui/               # Composable screens, ViewModels, state, theme
│   │   └── work/             # WorkManager workers & scheduling
│   ├── src/main/res/
│   │   ├── raw/quotes.json   # Quote data source
│   │   └── assets/thunder-quote.db  # Pre-populated Room database
│   ├── src/test/             # Unit tests
│   └── src/androidTest/      # Instrumented tests
├── gradle/
│   └── libs.versions.toml    # Version catalog (single source of truth for versions)
├── build.gradle.kts          # Root build config
├── app/build.gradle.kts      # App module build config
├── settings.gradle.kts       # Module settings
└── docs/
    └── privacy_policy.txt
```

---

## Architecture

The app follows **MVVM + Repository** pattern with a single Activity:

```
UI (Composables) ──► ViewModel ──► Repository ──► Room DB (pre-populated)
                                                └► SharedPreferences (settings)
```

**Key layers:**

| Layer | Location | Responsibility |
|-------|----------|----------------|
| UI | `ui/` | Jetpack Compose screens, collects state flows |
| ViewModel | `ui/home/HomeViewModel.kt` | Exposes `StateFlow`/`PagingData` to UI |
| Repository | `repo/QuoteRepo.kt` | Data access, favorite toggling, random quote |
| Database | `db/` | Room DAO, entity definition |
| DI | `di/ServiceLocator.kt` | Service locator (not a DI framework) |
| Background | `work/` | WorkManager for daily quote notifications |

---

## Key Conventions

### State Management

- Use `MutableStateFlow` privately, expose as `asStateFlow()`
- UI state classes must be annotated with `@Immutable`
- Collect flows with `collectAsStateWithLifecycle()` (not `collectAsState()`)

```kotlin
// ViewModel
private val _state = MutableStateFlow(QuoteListState())
val state = _state.asStateFlow()

// Composable
val state by viewModel.state.collectAsStateWithLifecycle()
```

### Navigation

Routes are defined as a serializable sealed class in `navigation/NavigationCompose.kt`:

```kotlin
@Serializable sealed class NavRoute {
    @Serializable data object QuoteList : NavRoute()
    @Serializable data class QuoteDetail(val id: Int) : NavRoute()
    // ...
}
```

Always add new routes here. Navigation is handled via `NavController` in `HomeScreen.kt`.

### Dependency Injection

The project uses a **Service Locator** pattern — not Hilt or Koin. New dependencies go in `ServiceLocator.kt`:

```kotlin
object ServiceLocator {
    val database by lazy { AppDatabase.getDatabase(App.context) }
    val prefs by lazy { App.context.getSharedPreferences("thunder-quote", MODE_PRIVATE) }
}
```

Avoid introducing a full DI framework without team agreement.

### Database

- Room database version: **1** — `exportSchema = false`, uses `fallbackToDestructiveMigration()`
- Pre-populated from `assets/thunder-quote.db`
- Only entity: `Quote(id, quote, author, isFavorite)`
- Quote data also lives in `res/raw/quotes.json`

When adding migrations, increment the version in `AppDatabase.kt` and add a `Migration` object. Do **not** rely on destructive migration in production.

### UI / Compose

- Use **Material 3** components only (`androidx.compose.material3.*`)
- Theming is in `ui/theme/` — colors, typography, sizes, and Theme composable
- Always use dimension constants from `Size.kt` rather than hardcoded dp values
- Support both light and dark mode; test with `dynamicColor` on API 31+

### Notifications

- Notification channel/group setup is in `notification/Notification.kt`
- Daily notifications are scheduled via `work/Works.kt` using `WorkManager`
- The worker (`NotificationScheduler`) runs as a `CoroutineWorker`

### Networking

- Retrofit 3 is included but not fully wired (stub present)
- Use `kotlinx.serialization` (not Gson/Moshi) for JSON parsing
- The JSON serializer module singleton is in `app/AppModule.kt`

---

## Build & Development

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+
- Android SDK with API 36

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Lint check
./gradlew lint
```

### Version Catalog

All dependency versions are managed in `gradle/libs.versions.toml`. When adding a new library:
1. Add the version under `[versions]`
2. Add the library under `[libraries]`
3. Reference it in `app/build.gradle.kts` as `libs.<alias>`

### SDK Versions

| Config | Value |
|--------|-------|
| `compileSdk` | 36 |
| `targetSdk` | 36 |
| `minSdk` | 26 (Android 8.0) |
| App version code | 49 |
| App version name | 1.0 |

---

## Testing

Currently only skeleton tests exist. When adding tests:

- **Unit tests**: `app/src/test/` — use JUnit 4, no Android framework dependencies
- **Instrumented tests**: `app/src/androidTest/` — use AndroidJUnit4, Espresso, Compose UI Test
- ViewModel tests should use `kotlinx-coroutines-test` with `TestDispatcher`
- Repository tests should use an in-memory Room database

---

## Package & Application Info

- **Application ID**: `com.sharukh.thunderquote`
- **Main Activity**: `HomeActivity` (single-activity architecture)
- **Required Permissions**: `POST_NOTIFICATIONS`, `INTERNET`
- **Theme**: Material Light NoActionBar with edge-to-edge support

---

## Gotchas & Notes

- `App.context` is a static application context reference — use only for non-UI operations (DB, WorkManager, notifications)
- `SettingsRepo` is a stub — settings persistence is not yet implemented
- `SettingsScreen` is a placeholder composable
- Database schema export is disabled (`exportSchema = false`) — enable this if migrations become important
- No CI/CD pipeline is configured yet
- No localization — only English strings in `res/values/strings.xml`
