FamConnect is a family organizer app for Android, built from the ground up using 100% Kotlin and modern Android development tools. This repository serves as a practical example of a full-stack mobile application using a MVVM architecture.

### Tech Stack & Architecture

This project demonstrates proficiency in a variety of modern technologies:
UI: Built entirely with Jetpack Compose, Android's modern declarative UI toolkit.
Architecture: Clean MVVM (Model-View-ViewModel)  architecture to separate UI from business logic.
Asynchronicity: Kotlin Coroutines and Flow are used for managing background threads and handling asynchronous data streams.
Backend: Leverages the Firebase Platform for:
Authentication: Email/Password sign-in.
Database: Cloud Firestore for real-time, persistent data storage.
Security: Complex server-side security rules (firestore.rules) to protect user data.
Dependency Injection: Using Hilt/Dagger or manual injection via ViewModels. (mention which you used)
Navigation: NavController for navigating between different Composables.
    
This project was a deep dive into solving real-world challenges like handling race conditions between authentication and database reads, designing secure and efficient Firestore rules, and structuring a scalable, maintainable codebase.
