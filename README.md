# AI Document Assistant

A fully functional, beginner-friendly portfolio project that allows users to upload documents (PDF, DOCX, TXT), extract their text, and interact with the Google Gemini AI API to generate summaries, key points, simple explanations, and important topics.

## Features

*   **Document Upload:** Supports `.pdf`, `.docx`, and `.txt` files.
*   **Text Extraction:** Uses Apache PDFBox and POI to extract text cleanly.
*   **AI Integration:** Connects to the Google Gemini AI API for intelligent text analysis.
*   **Multiple AI Actions:**
    *   Summarize Document
    *   Extract Key Points
    *   Simple Explanation
    *   Identify Important Topics
*   **Clean Frontend:** Modern, responsive, drag-and-drop enabled interface using HTML/CSS/Vanilla JS.
*   **Save Results:** Copy or download the AI-generated results as a text file.
*   **Database Storage:** Stores document metadata in a MySQL database.

## Technologies Used

*   **Backend:** Java 17, Spring Boot, Maven, Spring Data JPA, REST APIs.
*   **Database:** MySQL.
*   **Libraries:** Apache PDFBox (PDF), Apache POI (DOCX), org.json.
*   **Frontend:** HTML5, CSS3, Vanilla JavaScript, Fetch API.
*   **AI Service:** Google Gemini API.

## Project Structure

```text
AI-Document-Assistant/
├── frontend/             # HTML, CSS, JS files (Open index.html in browser)
├── backend/              # Spring Boot Java Application
├── uploads/              # Uploaded documents are saved here locally
└── .gitignore            # Git ignore file
```

## How the application works

1.  **Frontend:** The user opens `index.html` and uploads a document. The frontend sends a `multipart/form-data` POST request to the backend.
2.  **Backend (Upload):** `DocumentController` receives the file. `DocumentService` saves the file locally in the `uploads/` folder and calls `TextExtractionService`.
3.  **Extraction:** `TextExtractionService` uses the right tool (PDFBox for PDF, POI for DOCX, standard IO for TXT) to read the text.
4.  **Database:** `DocumentService` saves the file details and the extracted text to the MySQL database via `DocumentRepository`.
5.  **Frontend (AI):** The user is redirected to `result.html` and clicks "Summarize". The frontend calls the AI endpoint.
6.  **Backend (AI):** `AiController` receives the request. `AiService` builds a prompt with the extracted text and sends an HTTP POST request to the Google Gemini API.
7.  **Result:** The Gemini API response is parsed, returned to the frontend, and displayed to the user.

## Requirements

*   **Java Development Kit (JDK) 17+**
*   **Apache Maven**
*   **MySQL Server**
*   **Google Gemini API Key**

## Setup Instructions

### 1. Database Setup

1. Install MySQL if you haven't already.
2. Open your MySQL client (e.g., MySQL Workbench or terminal).
3. Create the database:
   ```sql
   CREATE DATABASE ai_document_assistant;
   ```
4. By default, the application uses `root` as the username and an empty password. If your MySQL setup uses a different username or has a password, update the `application.properties` file located at `backend/src/main/resources/application.properties`.

### 2. Gemini API Setup

1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey).
2. Create a new API key.
3. You need to provide this key to the application using an environment variable.

#### Setting the Environment Variable (`GEMINI_API_KEY`)

**On Windows (Command Prompt):**
```cmd
set GEMINI_API_KEY=your_actual_api_key_here
```

**On Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="your_actual_api_key_here"
```

**On Mac/Linux:**
```bash
export GEMINI_API_KEY="your_actual_api_key_here"
```

*Note: Alternatively, you can temporarily paste the key directly into `backend/src/main/resources/application.properties` for local testing, but NEVER commit your API key to GitHub.*

### 3. Run the Backend (Spring Boot)

1. Open a terminal and navigate to the `backend` folder.
2. Compile and run the application using Maven:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
3. The server will start on `http://localhost:8080`.
4. Ensure you don't have errors in the console. The `Document` table will be created automatically in your MySQL database.

### 4. Run the Frontend

1. You don't need a complex web server for the frontend.
2. Simply navigate to the `frontend` folder and double-click `index.html` to open it in your web browser.
3. Or, if you use VS Code, you can use the "Live Server" extension.

## API Endpoints

*   `POST /api/documents/upload` - Uploads a document and extracts text.
*   `GET /api/documents` - Gets a list of all documents.
*   `GET /api/documents/{id}` - Gets details of a specific document.
*   `DELETE /api/documents/{id}` - Deletes a document.
*   `POST /api/ai/summarize/{id}` - Returns an AI summary.
*   `POST /api/ai/key-points/{id}` - Returns AI key points.
*   `POST /api/ai/explain/{id}` - Returns a simple AI explanation.
*   `POST /api/ai/topics/{id}` - Returns important AI topics.

## Common Troubleshooting Steps

*   **Database Error on Startup:** Ensure MySQL is running, the database `ai_document_assistant` exists, and your username/password in `application.properties` are correct.
*   **CORS Error in Browser Console:** Make sure the backend is running on `http://localhost:8080`. The `@CrossOrigin` annotation is already added to the controllers.
*   **"Gemini API key is missing" Error:** Make sure you set the `GEMINI_API_KEY` environment variable in the same terminal window where you run `mvn spring-boot:run`.
*   **"Unsupported file format" Error:** Ensure you are only uploading `.txt`, `.pdf`, or `.docx` files.

## GitHub Push Commands

To push this project to your GitHub repository:

```bash
git init
git add .
git commit -m "Initial commit: AI Document Assistant"
git branch -M main
git remote add origin <your-repository-url>
git push -u origin main
```

## Future Improvements

*   Add user authentication (Login/Register).
*   Deploy the backend to Render/Heroku and the frontend to Vercel/Netlify.
*   Support for image files (OCR).
*   Add a chat interface to ask specific questions about the document.

## Author
[Your Name] - Beginner Java Developer
