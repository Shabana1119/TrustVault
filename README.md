# TrustVault

TrustVault is a document-sharing project designed to manage documents and record document activity.

## Current Features

- Login for a development administrator account
- Document upload with a 5 MB size limit
- Display uploaded document names and sizes
- Basic audit trail for document uploads
- Record the username, action, document name, and time

## Technology Stack

- Java 21
- Spring Boot
- Spring Security
- HTML, CSS, and JavaScript
- Maven

## Current Limitations

This is an early development version. Document contents are not permanently stored, and audit records are lost when the application restarts. Per-document permissions, separate user accounts, secure document downloads, and permanent audit storage are not implemented yet.

**Do not use this version for confidential or sensitive documents.**

## Run Locally

1. Install JDK 21.
2. Clone this repository.
3. Open a terminal in the project folder.
4. On Windows, run:

   `.\mvnw.cmd spring-boot:run`

5. Open `http://localhost:8081` in your browser.

Review the security configuration before deploying this application publicly.
