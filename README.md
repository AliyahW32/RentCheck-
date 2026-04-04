# RentCheck Me

Separated demo application with:

- `frontend/`: dynamic React client served by a lightweight Node static server on port `3000`
- `backend/`: Java backend on port `3001`

Java backend structure:

- `backend/src/main/java/com/rentcheckme/backend/controller`: REST controllers
- `backend/src/main/java/com/rentcheckme/backend/service`: business logic
- `backend/src/main/java/com/rentcheckme/backend/repository`: in-memory repositories
- `backend/src/main/java/com/rentcheckme/backend/model`: domain models
- `backend/src/main/java/com/rentcheckme/backend/dto`: request/response DTOs
- `backend/src/main/resources/application.properties`: Spring port config
- `backend/pom.xml`: Spring Boot project file

Implemented features:

- Role-aware users: renter, agent, and admin
- Separate frontend pages for welcome/get started, profile, area search, and AI assistant
- Manual neighborhood map selection that filters listings
- Backend budget calculation and listing evaluation
- Housing-only assistant endpoint that answers site-related questions and rejects unrelated prompts
- Java controller/service/repository layering for users, dashboard calculation, listings, maps, and assistant responses

Run locally:

```powershell
cd .\backend
.\mvnw.cmd spring-boot:run

cd ..\frontend
node .\server.js
```

Then open `http://localhost:3000`.

Frontend routes:

- `/`
- `/profile`
- `/search`
- `/assistant`

If you want to use the Java backend instead of the Node prototype, the main class is:

`backend/src/main/java/com/rentcheckme/backend/RentCheckMeApplication.java`
