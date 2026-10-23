# TrustVault AI Prompt Log

This file records how AI assistance was used during TrustVault development. The entries summarize the actual development work and testing performed.

## Entry 1 — 2026-10-19
**Goal:** Understand the challenge requirements.

**Prompt summary:** Help me understand the TrustVault secure document-sharing challenge and break it into beginner-friendly development steps.

**Result:** Identified the need for authentication, document access control, an audit trail, documentation, GitHub, and deployment.

## Entry 2 — 2026-10-20
**Goal:** Set up the development environment and application.

**Prompt summary:** Guide me step by step through setting up the Java project, using VS Code and Git, and preparing a web application.

**Result:** Set up the project and prepared it for GitHub and deployment.

## Entry 3 — 2026-10-21
**Goal:** Configure login and web security.

**Prompt summary:** Help configure Spring Security, read login credentials from environment variables, and resolve CSRF handling issues during document upload.

**Result:** Updated the security configuration and tested the login and upload flow.

## Entry 4 — 2026-10-22
**Goal:** Improve document metadata and audit filtering.

**Prompt summary:** Review the document controller and help add owner-based filtering, filename validation, and a file-size limit.

**Result:** Updated `DocumentController.java`, built the project successfully, committed the change, and pushed it to GitHub. The implementation still needs separate user accounts and authorization tests.

## Entry 5 — 2026-10-23
**Goal:** Verify deployment and upload activity.

**Prompt summary:** Help check the Render deployment and test whether document upload and audit logging work.

**Result:** Confirmed that the deployed application accepted a small test file, displayed its name and size, and recorded the username, upload action, and timestamp. This test did not verify permanent storage or complete access control.

## Reflection

AI assistance helped me understand the requirements, troubleshoot code, configure security, and prepare documentation. I reviewed the suggested changes, built the application, deployed it, and tested the upload and audit trail. The application remains a development demo, and the known limitations are documented in `THREATS.md`.
