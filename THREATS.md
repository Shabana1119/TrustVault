# TrustVault Threat Model

## 1. Purpose

TrustVault is a document-sharing project designed to demonstrate document upload tracking and audit logging. The goal is to improve control over who can access documents and provide a record of document activity.

## 2. Assets to Protect

- Document contents and filenames
- User login credentials
- Document ownership and access permissions
- Audit records, including usernames, actions, and timestamps

## 3. Possible Threats

### Threat 1: Unauthorized Access
**Risk:** An unauthorized person may access a document.

**Mitigation:** Require authentication and implement document-level authorization before allowing users to view or download documents.

**Current status:** Login is implemented, but separate users and document-level permissions are not yet implemented.

### Threat 2: Exposed Credentials
**Risk:** Hard-coded or leaked passwords could allow unauthorized login.

**Mitigation:** Read credentials from environment variables, use password hashing, and never commit passwords to GitHub.

**Current status:** Environment variables and BCrypt password hashing are configured. Credentials must still be protected and rotated if exposed.

### Threat 3: Malicious File Upload
**Risk:** An attacker may upload an excessively large or dangerous file.

**Mitigation:** Limit file size, validate filenames, and validate file types before storing files.

**Current status:** A 5 MB size limit and basic filename validation are implemented. File-type validation is still needed.

### Threat 4: Forged or Missing Audit Records
**Risk:** Audit records could be missing, changed, or lost after a restart.

**Mitigation:** Store audit events persistently and restrict modification of audit records. Consider tamper-evident logging.

**Current status:** Upload events are recorded in memory. Permanent and tamper-evident audit storage is not implemented.

### Threat 5: Cross-User Data Exposure
**Risk:** One user may see another user's documents or audit records.

**Mitigation:** Check the authenticated user's identity and permissions on every document and audit request.

**Current status:** Basic owner-based filtering has been added, but separate user accounts and complete authorization tests are still needed.

### Threat 6: Cross-Site Request Forgery (CSRF)
**Risk:** A malicious website may attempt to perform actions using another user's authenticated session.

**Mitigation:** Enable CSRF protection and validate CSRF tokens for state-changing requests.

**Current status:** Spring Security CSRF protection is enabled and the upload flow has been tested.

## 4. Known Limitations

- The application currently uses a single configured admin account.
- Document metadata and audit events are held in memory.
- Uploaded file contents are not permanently stored by the current implementation.
- Separate user accounts and document-level sharing permissions are incomplete.
- File download, persistent storage, and tamper-evident audit logging require further development.

## 5. Testing Plan

- Test login with valid and invalid credentials.
- Test upload with an empty file and a file larger than 5 MB.
- Test filenames containing directory paths.
- Verify that each user's documents and audit events are isolated.
- Verify CSRF protection for upload requests.
- Restart the application and check whether records persist once persistent storage is implemented.

## 6. Responsible Use

This project is a development demonstration. Do not upload confidential documents or use it as a production secure document-sharing system until the missing security controls and persistent storage are implemented.
