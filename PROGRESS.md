# FileCast — Project Progress

## Project Info
- **App name:** FileCast (File Type Converter)
- **Package:** `com.example.project`
- **Repo:** https://github.com/Dilfa33/FiletypeConverter-MobileApp.git
- **Branch:** `ass1`
- **Android Studio project path:** `C:\Users\Dilfa\AndroidStudioProjects\project`

---

## Tech Stack
- Kotlin `2.1.20`
- Jetpack Compose + Material3
- Navigation Compose `2.8.9`
- ViewModel + StateFlow
- Room `2.6.1`
- Hilt `2.51.1`
- KSP `2.1.20-1.0.32`
- AGP `9.1.0`

---

## Style
- Dark theme (`#0D0D0D` background, `#1A1A1A` surface)
- Light blue accents (`#4FC3F7`)
- Bottom navigation bar (History | Upload | Profile)

---

## Folder Structure
```
com.example.project/
├── data/
│   └── local/
│       ├── dao/          ← FileDao, UserDao, ConversionJobDao, FormatDao, FileTagDao
│       ├── db/           ← AppDatabase.kt
│       ├── entity/       ← 5 entities + UserWithFiles + FileWithTags
│       └── util/         ← Converters.kt
├── di/                   ← DatabaseModule, RepositoryModule
├── model/                ← FileItem, User, HardcodedData, ConversionStatus
├── navigation/           ← NavGraph, BottomNavItem, AppRoutes
├── repository/
│   ├── mappers/          ← FileMapper, UserMapper
│   └── *Repository + *RepositoryImpl (File, User, ConversionJob, Format)
├── ui/
│   └── screens/
│       ├── login/        ← LoginScreen + components/
│       ├── register/     ← RegisterScreen + components/
│       ├── upload/       ← UploadScreen + components/
│       ├── history/      ← HistoryScreen + components/
│       ├── profile/      ← ProfileScreen + components/
│       ├── result/       ← ConversionResultScreen + components/
│       └── details/      ← FileDetailsScreen + components/
├── viewmodel/            ← AuthViewModel, HistoryViewModel, UploadViewModel, ProfileViewModel
├── App.kt                ← @HiltAndroidApp
└── MainActivity.kt       ← @AndroidEntryPoint
```

---

## Assignments

### ✅ Assignment 1 — UI Skeleton (Done)
- 5 screens: Upload, History, Profile, ConversionResult, FileDetails
- + Login and Register screens
- Dark theme + light blue accents
- Bottom app bar with Scaffold
- MVVM structure
- Reusable components in `components/` folders
- Basic validation (disabled buttons, error messages)

### ✅ Assignment 2 — State, Lists & Navigation (Done)
- `HardcodedData.kt` — centralized hardcoded file list (10 items)
- `HistoryViewModel` — search, filter, 5 derived states (filteredFiles, isEmpty, successCount, failedCount, totalSizeMb)
- `ProfileViewModel` — 4 derived states (totalConversions, successRate, storageUsedMb, recentFiles)
- `AuthViewModel` — `isLoginEnabled` / `isRegisterEnabled` as derived states inside UiState
- 2x LazyColumn (History, Profile)
- 2x LazyRow (FormatSelector, StatusFilterRow) + 3rd in Profile stats
- Search bar in History with live filtering
- Status filter chips (All / Success / Failed / Processing)
- Scroll-to-top FAB in History
- Navigation passes 2 args (fileId + fileName)
- FileDetailsScreen shows real data from HardcodedData
- Login/Register navigate via `LaunchedEffect` reacting to `navigateToHome` state (no logic in UI)

### 🔄 Assignment 3 — Local Data Layer + Coroutines (In Progress)
**Deadline: May 17, 2026 (Sunday 23:00)**

#### ✅ Done
- 5 Room entities: `UserEntity`, `FileEntity`, `ConversionJobEntity`, `FormatEntity`, `FileTagEntity`
- Junction table: `FileTagCrossRef` (Many-to-Many: File ↔ Tag)
- Relationship classes: `UserWithFiles` (1-to-Many), `FileWithTags` (Many-to-Many)
- 5 DAOs with full CRUD + Flow queries
- `AppDatabase.kt`
- `Converters.kt`
- 4 Repository interfaces + implementations
- `FileMapper`, `UserMapper`
- `DatabaseModule.kt` (Hilt)
- `RepositoryModule.kt` (Hilt)
- `App.kt` with `@HiltAndroidApp`
- `MainActivity.kt` with `@AndroidEntryPoint`

#### ❌ Still To Do
- **Fix build errors** — Hilt plugin conflict (`Android BaseExtension not found`). Need to fix `app/build.gradle.kts` plugin ordering/config for Hilt + KSP to work together
- **5 ViewModels with sealed UiState** — Each screen needs `sealed class UiState { Init, Loading, Success, Error }` — currently ViewModels use plain data classes
- **1 ViewModel per screen** — Currently some screens share ViewModels; need dedicated VMs for: Login, Register, Upload, History, Profile, Details, Result (7 total, min 5 required)
- **Stateful + Stateless screen pattern** — Each screen needs a stateless `@Composable` (takes plain params) + a stateful wrapper (holds ViewModel, passes state down)
- **Connect ViewModels to repositories** — Replace `HardcodedData` usage in ViewModels with actual Room/repository calls using coroutines (`viewModelScope.launch`)
- **Seed initial data** — On first app launch, populate Room DB with formats and a default user
- **CRUD flow in UI** — At least create + delete wired through the UI (e.g. adding a file to history, deleting a file)

---

## Known Issues / Notes
- Kotlin downgraded from `2.2.10` → `2.1.20` to match KSP `2.1.20-1.0.32`
- Hilt plugin conflict still unresolved — `alias(libs.plugins.hilt)` in `app/build.gradle.kts` throws `Android BaseExtension not found`. Likely needs `kotlin-android` plugin but that conflicts with `kotlin-compose`. Research needed.
- `HardcodedData.kt` still used in ViewModels — needs replacing with Room calls for Assignment 3
- `.claude/` folder exists in repo root (Claude config) — can be gitignored if needed
