# Pokedex151

A modern, native Android Pokédex app built with Kotlin, Views, Ktor and Koin. Browse, search, and explore the original 151 Pokémon with beautiful UI and smooth performance.

## Screenshot

<img width="1256" alt="Screenshot 2025-07-10 at 3 18 11 PM" src="https://github.com/user-attachments/assets/1989f2a2-507a-44fe-a8f3-dc241ba41ea4" />

## Features

- Browse and search the original 151 Pokémon
- View detailed stats, types, and abilities (coming soon)
- Filter Pokémon by type or caught/missing status
- Lottie animations for loading and transitions
- High-performance image loading with Glide
- Dependency injection with Koin
- **GAME MODE**: Ability to catch pokemon by throwing pokeball (Still in development)

## Optimizations & Architecture

- **Dependency Injection:** All screens use Koin for dependency injection, ensuring ViewModels are managed by the DI container for maintainability and testability.
- **Architecture:** The app follows a **MVVM** architecture, with a clean separation of concerns, making it easy to understand and maintain.
- **Testing:** The app features simple unit tests for ViewModels using MockK, ensuring a high level of quality and reliability.

## Getting Started

### Prerequisites

- JDK 17+
- Android Studio
- Gradle (wrapper included)

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Gabrieal4Real/Pokedex151.git
   cd Pokedex151
   ```
2. **Install dependencies:**
   ```bash
   ./gradlew build
   ```
3. **Run the app:**
   - Use Android Studio’s device manager to build and run the app on an emulator or device.

## Tech Stack

- [Kotlin](https://kotlinlang.org/)  
- [Lottie (animations)](https://airbnb.io/lottie/#/)
- [Glide (image loading)](https://github.com/bumptech/glide)
- [Koin (dependency injection)](https://insert-koin.io/)  
- [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines)  
- [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)  
- [Ktor (networking)](https://ktor.io/)  
- [MocKK (testing)](https://mockk.io/)

## Platform Support

- **Android**: Fully supported

## Project Structure

```
app/
  ├── src/
  │   ├── main/
  │   │   ├── java/org/gabrieal/pokedex/
  │   │   │   ├── data/
  │   │   │   ├── feature/
  │   │   │   ├── helpers/
  │   │   │   └── ...
  ├── build.gradle.kts
gradle/
  └── libs.versions.toml
```

---
