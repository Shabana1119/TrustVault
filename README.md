# TrustVault

TrustVault is a document-sharing web application demo that records document upload activity. It aims to demonstrate authentication, document ownership, and audit logging.

## Live Demo

- **Website:** https://trustvault-mcbw.onrender.com/
- **GitHub Repository:** https://github.com/Shabana1119/TrustVault

## Current Features

- Login using a configured administrator account
- Upload a file up to 5 MB
- Display uploaded document names and sizes
- Record upload activity in an audit trail
- Record the username, action, document name, and timestamp
- Basic owner-based filtering in the document controller

## Technology Stack

- Java 21
- Spring Boot
- Spring Security
- HTML, CSS, and JavaScript
- Maven
- Docker
- Render

## Run Locally

### Requirements

- JDK 21
- Git

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/Shabana1119/TrustVault.git
   ```

2. Open the project folder:

   ```bash
   cd TrustVault
   ```

3. Configure your own demo login credentials in PowerShell:

   ```powershell
   $env:TRUSTVAULT_USERNAME="admin"
   $env:TRUSTVAULT_PASSWORD="ChooseYourOwnStrongPassword"
   ```

   Replace the example password with your own strong password. Do not commit passwords to GitHub.

4. Start the application:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. Open http://localhost:8081 in your browser.

## Current Limitations

This is an early development demo, not a production-ready secure document-sharing system.

- Only one configured administrator account is available.
- Document contents are not permanently stored by the current implementation.
- Document metadata and audit records are held in memory and may be lost when the application restarts.
- Separate user accounts and complete document-level permissions are not implemented.
- Secure document downloads and permanent audit storage are not implemented.
- Owner-based filtering has not yet been verified with multiple independent users.

## Security Notice

Do not upload confidential or sensitive documents. Further implementation and security testing are required before using TrustVault for real document sharing.
