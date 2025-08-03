# WatchIt - A Smart Movie Discovery App 🎬

WatchIt is a native Android application built with Kotlin and Jetpack Compose that demonstrates a modern, personalized content discovery experience. Moving beyond a simple static list, this app features a smart engine that learns from user preferences to deliver a curated home feed, similar to today's leading streaming platforms.

<p align="center">
<img src="https://github.com/HarshithPancheru/WatchIt/blob/main/Screenshots/Screenshot.jpg" width="260">
<img src="https://github.com/HarshithPancheru/WatchIt/blob/main/Screenshots/Screenshot1.jpg" width="260">
<img src="https://github.com/HarshithPancheru/WatchIt/blob/main/Screenshots/Screenshot2.jpg" width="260">
</p>
<p align="center">
<img src="https://github.com/HarshithPancheru/WatchIt/blob/main/Screenshots/Screenshot3.jpg" width="260">
<img src="https://github.com/HarshithPancheru/WatchIt/blob/main/Screenshots/Screenshot4.jpg" width="260">
</p>

---

## ✨ Features

* **Personalized Home Feed:** The app's home screen is dynamically curated based on user interactions. It prioritizes genres the user has liked and excludes genres they've disliked.
* **Preference Engine:** A scoring system (`like: +5`, `dislike: -5`) tracks user taste for different genres, saving the data locally.
* **Infinite Scroll:** The "explore" section of the feed seamlessly loads new movies as the user scrolls, ensuring an endless discovery experience.
* **State-Driven UI:** The app displays loading, success, and error states, providing clear feedback to the user.
* **Modern UX:** Features a pull-to-refresh gesture, a custom splash screen, and a clean, intuitive interface.
* **Preference Transparency:** A user profile dialog displays the saved genre scores, giving users insight into their own calculated tastes.

---

## 🛠️ Tech Stack & Architecture

This project was built using modern Android development practices and libraries.

* **Language:** **Kotlin**
* **UI:** **Jetpack Compose** for a fully declarative user interface.
* **Architecture:** **MVVM (Model-View-ViewModel)** to separate UI from business logic.
* **Asynchronous Programming:** **Kotlin Coroutines & Flow** for managing background tasks and data streams.
* **Networking:** **Retrofit** for type-safe REST API calls to The Movie Database (TMDB).
* **Data Persistence:** **Jetpack DataStore** for storing user preferences asynchronously.
* **Dependency:** A manual ViewModel Factory was used to provide dependencies to ViewModels.

---

## 🚀 Setup and Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/HarshithPancheru/WatchIt.git
    ```
2.  Open the project in Android Studio.
3.  Get an API key from [The Movie Database (TMDB)](https://www.themoviedb.org/settings/api).
4.  Open the ViewModel files (`HomeViewModel.kt`, `DetailViewModel.kt`) and replace the `"YOUR_API_KEY"` placeholder with your actual TMDB v3 API Key.
5.  Build and run the app.
