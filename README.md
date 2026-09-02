# 🧠 LifeOS — Personal Digital Dashboard

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/AI-Google%20Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white"/>
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
</p>

<p align="center">
  <b>A personal digital dashboard designed to bring everyday information and tools together in one place.</b>
</p>

<p align="center">
  <a href="https://github.com/mdmahadih673/LifeOS-Personal-Digital-Dashboard-.">View Repository</a>
  •
  <a href="https://ai.studio/apps/010abb3d-cd32-4c2c-994e-027c5d1922c3">View in AI Studio</a>
</p>

---

## 📖 About The Project

**LifeOS** is a Personal Digital Dashboard project developed as an Android application.

The project is structured as an Android Studio project and includes integration with the **Google Gemini API** through an environment variable.

The repository is designed to be opened and run locally using Android Studio.

---

## ✨ Highlights

* 📱 Android application
* 🤖 Gemini API integration
* 🔐 Environment-based API key configuration
* 🛠️ Gradle-based Android project
* 📂 Organized Android project structure
* 📲 Supports emulator or physical Android devices

---

## 🛠️ Technology Stack

<div align="center">

<img src="https://skillicons.dev/icons?i=androidstudio,gradle,kotlin" />

</div>

<br>

* **Platform:** Android
* **IDE:** Android Studio
* **Build System:** Gradle / Kotlin DSL
* **AI Integration:** Google Gemini API
* **Configuration:** `.env`

---

## 📂 Project Structure

```text
LifeOS/
├── app/
├── assets/
├── gradle/
├── .env.example
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── metadata.json
├── settings.gradle.kts
└── README.md
```

The repository currently contains the Android application module, Gradle configuration and environment example files.

---

## ⚙️ Run Locally

### 1. Prerequisites

Install:

* Android Studio
* Android SDK
* An Android emulator or physical Android device

### 2. Clone the repository

```bash
git clone https://github.com/mdmahadih673/LifeOS-Personal-Digital-Dashboard-.git
```

### 3. Open the project

Open **Android Studio** and select the cloned project directory.

Allow Android Studio to resolve any required project compatibility/configuration changes.

### 4. Configure Gemini API

Create a `.env` file in the project root.

Add:

```env
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

Use `.env.example` as the configuration reference.

### 5. Build Configuration

Before running the project, remove the following line from `app/build.gradle.kts` if it exists:

```kotlin
signingConfig = signingConfigs.getByName("debugConfig")
```

### 6. Run

Run the application using:

* Android Emulator
* Physical Android Device

These are the repository's documented local setup steps.

---

## 🔐 Environment Variables

| Variable         | Description                                   |
| ---------------- | --------------------------------------------- |
| `GEMINI_API_KEY` | Google Gemini API key used by the application |

**Never commit your real API key to GitHub.**

---

## 🌐 Links

* 📦 **GitHub:** https://github.com/mdmahadih673/LifeOS-Personal-Digital-Dashboard-.
* 🤖 **Google AI Studio:** https://ai.studio/apps/010abb3d-cd32-4c2c-994e-027c5d1922c3

---

## 👨‍💻 Author

**Md Mahadi Hasan**

GitHub: https://github.com/mdmahadih673

---

<p align="center">
  ⭐ If you find this project interesting, consider giving it a star.
</p>
