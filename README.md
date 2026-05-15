# Grama Kalyana Sports

## Project Overview
Grama Kalyana Sports is an Android-based sports tournament management application designed to simplify the organization and management of village-level sports events and competitions. The application provides an efficient digital platform for managing tournaments, player information, live scores, match schedules, scoreboards, and event coordination. The project focuses on improving accessibility, usability, and overall sports event management for local communities using modern Android development technologies and AI-assisted functionalities. Currently it is implemented for most played games in the vilage, which includes Cricket, kabaddi and Volleyball.

The application was developed with the goal of creating a responsive, user-friendly, and feature-rich solution that supports smooth navigation, real-time updates, and enhanced user interaction across different devices and environments.

----------------------------------------------------------------------------------------------------------------------------------------------------------------
# Problem Statement
Traditional management of village sports tournaments is often handled manually using paper-based systems, spreadsheets, or disconnected communication methods. This leads to several challenges such as:
- Difficulty in managing tournament schedules
- Manual score tracking errors
- Lack of centralized player and match information
- Poor accessibility and usability in outdoor environments
- Delayed updates and inefficient coordination

Grama Kalyana Sports aims to solve these problems by providing a centralized digital platform for tournament management, live score updates, player tracking, and efficient event coordination.

------------------------------------------------------------------------------------------------------------------------------------------------------------------

# Features

## Role-based Access 
- Admin login - Create and Manage tournaments
- Scorer Login - Update live match scores
- Fan View - View Live Matches, scorers and players details (stats)

## Tournament & Team Management
- Create and manage sports tournaments
- Add and manage teams
- Add and manage players
- Organize match schedules and fixtures
- Track tournament progress and match results

## Live Scoreboard
- Real-time score updates
- Dynamic score display
- Match result management

## Player & Team Management
- Player profile creation
- Team registration and management
- Player statistics tracking

## Responsive User Interface
- Modern and user-friendly UI
- Responsive layouts for multiple screen sizes
- Smooth navigation and interaction handling

## Accessibility Improvements
- Enhanced text readability
- Better visibility under sunlight conditions
- Improved contrast and usability for outdoor sports environments

## Smart Features
- AI-assisted functionalities
- Intelligent user interaction improvements
- Optimized app performance

## Scorecard Export
- Generate and export match scorecards
- Share tournament results efficiently

## Regional Language Support
- Supports multiple regional languages for improved accessibility and inclusiveness
- Available languages: English, Kannada, Hindi, Tulu, Tamil, Telugu, Malayalam, and Marathi
- Designed to provide a user-friendly experience for rural and village-level communities

----------------------------------------------------------------------------------------------------------------------------------------------------------------

# Tech Stack

## Frontend / Mobile Development
- Kotlin
- Jetpack Compose
- Android SDK

## Architecture & Libraries
- MVVM Architecture
- Navigation Components
- ViewModel
- State Management

## Database & Storage (Backend)
- Room Database (SQLite)
- Firebase Realtime Database

## APIs & Integrations
- Gemini API

## Authentication
- Firebase Authentication

## Notifications/Alerts
- Firebase Cloud Messaging (FCM)

## Sharing/Export 
- Android Canvas API
- Bitmap API
- Whatsapp Intent Sharing

## Additional Tools
- Gradle
- Git & GitHub
- Android Studio

----------------------------------------------------------------------------------------------------------------------------------------------------------------

# Installation Steps

## Prerequisites
Before running the project, ensure you have:
- Android Studio installed
- Android SDK configured
- Git installed
- Internet connection for dependency downloads

----------------------------------------------------------------------------------------------------------------------------------------------------------------

## Clone the Repository

```bash
git clone https://github.com/Raksha451/Grama_Kalyana_Sports.git
```

---------------------------------------------------------------------------------------------------------------------------------------------------------------

## Open the Project

1. Open Android Studio
2. Click on **Open**
3. Select the cloned project folder
4. Wait for Gradle Sync to complete

----------------------------------------------------------------------------------------------------------------------------------------------------------------

## Build the Project

```bash
./gradlew build
```

---------------------------------------------------------------------------------------------------------------------------------------------------------------

# Run Instructions

## Run on Emulator
1. Start Android Emulator
2. Click the **Run** button in Android Studio

## Run on Physical Device
1. Enable USB Debugging
2. Connect your Android device
3. Click **Run App**

----------------------------------------------------------------------------------------------------------------------------------------------------------------

# Screenshots
## 1. Splash Screen
<img width="224" height="440" alt="image" src="https://github.com/user-attachments/assets/157d575a-1a2f-4259-bbee-4000c92439a1" />

## 2. Role-based access Screen 
<img width="250" height="460" alt="image" src="https://github.com/user-attachments/assets/b6df075d-e197-4a58-9fd7-b817f83d23f0" />

## 3. Language Selection screen 
<img width="228" height="444" alt="image" src="https://github.com/user-attachments/assets/a322f441-4a8f-47bf-921a-7cb8f65bbbad" />

## 4. Admin Login Screen
<img width="262" height="510" alt="image" src="https://github.com/user-attachments/assets/c7871cbb-dbc0-4c43-bc4e-307db10ba1f7" />

## 5. Tournament Setup screen
<img width="252" height="513" alt="image" src="https://github.com/user-attachments/assets/35a50124-add2-42eb-9374-d71352be35bd" />

## 6. Create Tournament Screen
<img width="253" height="516" alt="image" src="https://github.com/user-attachments/assets/3e3d599e-70f0-4cb9-9c71-96bb4e05a5bb" />

## 7. Add teams Screen
<img width="252" height="517" alt="image" src="https://github.com/user-attachments/assets/b9847219-2ff7-41ee-9401-4b9ddbef8e68" />

## 8. Add New Player Screen
<img width="254" height="510" alt="image" src="https://github.com/user-attachments/assets/a8105e30-b82c-4ba7-87b9-ef058a610d3e" />

## 9. Scorer Login Screen
<img width="262" height="524" alt="image" src="https://github.com/user-attachments/assets/4f52d269-90ff-4abc-905a-16b88e4f03c2" />

## 10. Scoring Screen
<img width="257" height="521" alt="image" src="https://github.com/user-attachments/assets/bcf14f6a-5b5b-4ec3-918c-7216b75d5ea0" />

## 11. Fan View-Live Scoreboard Screen
<img width="250" height="498" alt="image" src="https://github.com/user-attachments/assets/ccb44694-7742-4279-a95c-170378fd3b19" />

## 12. AI Chatbot Screen
<img width="249" height="498" alt="image" src="https://github.com/user-attachments/assets/666e4c27-55d9-490a-be65-6478c10e3e75" />

## 13. Player Profile Screen
<img width="264" height="534" alt="image" src="https://github.com/user-attachments/assets/ba3053d1-183a-4260-8d61-4270822a1cd3" />

## 14. Scorecard Share Screen 
<img width="269" height="536" alt="image" src="https://github.com/user-attachments/assets/d1022d52-0dbe-4100-b326-f4829300f18d" />

--------------------------------------------------------------------------------------------------------------------------------------------------------------

# Folder Structure

```plaintext
Grama_Kalyana_Sports/
│
├── .idea/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/gramakalyana/sports/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   └── model/
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── navigation/
│   │   │   │   ├── screens/
│   │   │   │   ├── theme/
│   │   │   │   ├── utils/
│   │   │   │   └── viewmodel/
│   │   │   │
│   │   │   ├── GramaKalyanaApplication.kt
│   │   │   └── MainActivity.kt
│   │   │
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   ├── mipmap-anydpi-v26/
│   │   │   ├── values/
│   │   │   └── xml/
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts
│   └── google-services.json
│
├── gradle/
├── .gitignore
├── README.md
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle.kts
```
--------------------------------------------------------------------------------------------------------------------------------------------------------------

# Project Highlights
- Modern Android application using Kotlin and Jetpack Compose
- Focused on real-world sports tournament management
- Responsive and accessible user interface
- Organized project architecture
- Real-time scoreboard and player management system
- AI-assisted smart functionality integration
---------------------------------------------------------------------------------------------------------------------------------------------------------------

# Future Enhancements
- Cloud database integration
- Real-time online multiplayer tournament tracking
- Push notifications for match updates
- AI-powered match predictions and analytics
- Dark mode support
- Multi-language support
- Admin dashboard for organizers
- Online registration system
- Firebase authentication integration
- Live streaming integration for tournaments
--------------------------------------------------------------------------------------------------------------------------------------------------------------

# Author
Raksha Shetty
Android App Developer

GitHub: https://github.com/Raksha451
--------------------------------------------------------------------------------------------------------------------------------------------------------------

# License

This project is developed for educational and learning purposes.

