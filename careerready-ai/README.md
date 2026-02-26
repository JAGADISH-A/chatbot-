# CareerReady AI

CareerReady AI is a professional career advisor chatbot built with Spring Boot and a modern, glassmorphic frontend. It uses the Groq API (Mistral Saba 24B) to provide expert advice on resumes, interviews, and career planning.

## 🚀 How to Run the Project

### Step 1: Backend Setup (Spring Boot)
1. **Prerequisites**: Ensure you have JDK 17+ installed.
2. **Open the Project**: Open the `backend` folder in your favorite IDE (IntelliJ IDEA, VS Code, or Eclipse).
3. **Configure API Key**: 
   - Open `backend/src/main/resources/application.properties`.
   - Your Groq API key is already configured.
4. **Run the Application**: 
   - Run the `CareerReadyApplication.java` file.
   - The backend will start on `http://localhost:8080`.
   - **Verification**: Visit `http://localhost:8080/api/chat/health` in your browser. You should see "CareerReady AI Backend is running!".

### Step 2: Frontend Setup
1. **Locate the File**: Navigate to the `frontend` folder.
2. **Open in Browser**: Double-click `index.html` to open it in your web browser.
3. **Start Chatting**: You can now interact with CareerReady AI!

## 🛠️ Tech Stack
- **Frontend**: HTML5, CSS3 (Vanilla), JavaScript (ES6+), Glassmorphic Design.
- **Backend**: Java 17, Spring Boot 3.2.3, Lombok, RestTemplate.
- **AI Engine**: Groq Cloud API (Model: `mistral-saba-24b`).

## ⚠️ Troubleshooting
- **CORS Issues**: The backend is configured to allow all origins (`*`), which is necessary for local file-based frontend access.
- **Port Conflict**: If port 8080 is in use, you can change `server.port` in `application.properties`.
- **API Errors**: Ensure your internet connection is active for the Groq API calls.
