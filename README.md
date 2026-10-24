# TrustVault

TrustVault is a document management web application designed to demonstrate controlled document access and an audit trail for tracking document activity.

## Live Demo
- **Website:** https://trustvault-mcbw.onrender.com/
- **GitHub Repository:** https://github.com/Shabana1119/TrustVault

## Features
- Login using configured application credentials
- Upload files up to 5 MB
- Display uploaded document names and sizes
- View and download supported documents
- Store document metadata and audit records in PostgreSQL
- Record document actions, usernames, and timestamps
- Apply basic document-owner filtering

## Technology Stack
- Java
- Spring Boot
- Spring Security
- PostgreSQL
- HTML, CSS, and JavaScript
- Maven
- Docker
- Render

## Run Locally
1. Install a compatible JDK and Git.
2. Clone the repository:
   ```bash
   git clone https://github.com/Shabana1119/TrustVault.git
   cd TrustVault
   ```
3. Configure the PostgreSQL connection using environment variables:
   `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.
4. Configure the application's login credentials according to its security configuration. Do not commit passwords or other secrets.
5. Start the application on Windows:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
6. Open http://localhost:8081 in your browser.

## Limitations
- The current demo does not yet implement sharing with selected individual users.
- Complete multi-user, document-level access permissions have not been implemented.
- The audit trail records application actions; it is not a tamper-proof audit system.
- Further security testing is needed before production use.

## Security Notice
This project is a learning and demonstration application. Do not upload confidential documents or use it as a production secure-sharing service without additional security controls and testing.
