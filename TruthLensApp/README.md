# 🛡️ TruthLens – AI Fake News Detector & Personalized News System
## Java Full Stack (Spring Boot 3 + JPA/H2 + Web & React Native Mobile)

TruthLens is a complete **Java Full Stack (JFS)** application featuring:
1. **Spring Boot 3 Backend** (Java 21/17) with RESTful APIs, Spring Data JPA, and embedded persistent H2 Database.
2. **Interactive Web Dashboard** served directly by Spring Boot at `http://localhost:8080`.
3. **React Native (Expo Go) Mobile App** in `mobile/`.
4. **Original Flask Backend** preserved in `backend-flask/` for reference.

---

## 📁 Project Structure

```
TruthLensApp/
├── backend/                       ← Spring Boot 3 Java Full Stack Backend
│   ├── pom.xml                    ← Maven configuration (Spring Boot, JPA, H2, Validation)
│   ├── mvnw / mvnw.cmd            ← Maven Wrapper (zero pre-install required)
│   ├── run-backend.bat            ← Windows one-click start script
│   ├── run-backend.ps1            ← PowerShell one-click start script
│   └── src/
│       ├── main/
│       │   ├── java/com/truthlens/
│       │   │   ├── TruthLensApplication.java     ← Main Application Class
│       │   │   ├── config/                       ← CORS & RestTemplate configurations
│       │   │   ├── controller/                   ← REST Controllers (Analyze, News, Profile, History, Health)
│       │   │   ├── dto/                          ← Data Transfer Objects & API Schemas
│       │   │   ├── entity/                       ← JPA Entities (AnalysisRecord, UserProfile)
│       │   │   ├── repository/                   ← Spring Data JPA Repositories
│       │   │   └── service/                      ← Business logic (Heuristics, GNews client, Profile, History)
│       │   └── resources/
│       │       ├── application.properties        ← H2 Database & Server configs
│       │       └── static/                       ← Full Stack Web Dashboard SPA
│       │           ├── index.html                ← Interactive Web UI (Analyze, Feed, Search, History, Profile)
│       │           ├── css/style.css             ← Modern dark-theme responsive styling
│       │           └── js/app.js                 ← Pure JS SPA Client
│       └── test/java/com/truthlens/              ← Unit & Integration Tests
│
├── mobile/                        ← React Native Expo Mobile App
│   ├── App.js
│   ├── screens/                   ← Mobile screens (Analyze, Feed, Search, History, Profile)
│   └── utils/api.js               ← API client pointing to Spring Boot backend
│
├── backend-flask/                 ← Original Python Flask Backend (preserved)
│   └── app.py
│
├── run-backend.bat                ← Root shortcut to launch Spring Boot backend
├── run-backend.ps1                ← Root PowerShell script to launch Spring Boot backend
└── README.md
```

---

## 🚀 Quick Start (Spring Boot Backend)

### Method 1: Using One-Click Scripts
Double-click `run-backend.bat` or run:
```powershell
.\run-backend.ps1
```

### Method 2: Using Maven Wrapper
```bash
cd backend
.\mvnw.cmd spring-boot:run     # Windows
# or ./mvnw spring-boot:run     # Linux / macOS
```

### Method 3: Using Pre-Built JAR
```bash
cd backend
java -jar target/truthlens-backend-1.0.0.jar
```

The backend starts at: **http://localhost:8080**

- **🌐 Interactive Web Dashboard:** [http://localhost:8080](http://localhost:8080)
- **💾 H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:file:./data/truthlensdb`
  - User: `sa`
  - Password: *(leave empty)*
- **⚡ Health Check:** [http://localhost:8080/api/health](http://localhost:8080/api/health)

---

## 📱 Mobile App (React Native Expo)

1. Open `mobile/utils/api.js`.
2. Configure `BASE_URL`:
   - For PC Web / Local testing: `'http://localhost:8080'`
   - For Android Emulator: `'http://10.0.2.2:8080'`
   - For Physical Phone via Expo Go: `'http://<YOUR_LAN_IP>:8080'` (e.g. `http://192.168.1.10:8080`)
3. Launch mobile app:
   ```bash
   cd mobile
   npm install
   npx expo start
   ```
4. Scan the QR code using the **Expo Go** mobile app.

---

## ⚡ REST API Specification

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health status and Java runtime environment info |
| `POST` | `/api/analyze` | Heuristic credibility analyzer for URL, title, or body text |
| `POST` | `/api/batch` | Batch credibility analysis for up to 20 articles |
| `GET` | `/api/news/feed` | Personalized news feed filtered by topics (`?topics=technology,science&apikey=...`) |
| `GET` | `/api/news/search` | Search news articles by query (`?q=climate&apikey=...`) |
| `GET` | `/api/profile` | Get user profile preferences and scan statistics |
| `POST` | `/api/profile` | Update user name, topic interests, and GNews API key |
| `GET` | `/api/history` | Retrieve past scan records from the database |
| `DELETE` | `/api/history/{id}` | Delete a specific analysis record |
| `DELETE` | `/api/history` | Clear all past analysis history |

---

## 🧠 Credibility Scoring Engine

The Spring Boot backend implements a multi-factor heuristic trust assessment engine:
- **Domain Reputation:** Verified against curated databases of trusted news networks (+30) and known misinformation sites (-40).
- **Transport Security:** Verifies HTTPS protocol (+5).
- **Clickbait Pattern Matching:** Evaluates headlines and body against clickbait regex patterns.
- **Headline Capitalization:** Detects sensationalist excessive capitalization ratios.
- **Journalistic Attribution:** Detects author bylines and investigative reporting attribution signals (+8).
- **Citation Signals:** Analyzes peer-reviewed study and empirical data citations (+10).
- **Emotional Manipulation:** Flags emotionally charged rhetoric and outrage-bait words (-12).
- **In-Depth Analysis:** Scores substantive long-form reporting vs short unverified snippets.
- **Verdict Classification:**
  - `CREDIBLE` (Score 65–100): High trust metrics.
  - `MIXED` (Score 35–64): Conflicting signals; cross-verification recommended.
  - `LIKELY FAKE` (Score 0–34): High red-flag density.

---

## 🔑 GNews API Setup (Optional)
To fetch real-time global news:
1. Register for a free API key at [gnews.io](https://gnews.io) (100 free requests/day).
2. Enter your API key in the **Profile** tab of the Web Dashboard or Mobile App.
3. If no key is configured, TruthLens automatically falls back to curated sample news.
