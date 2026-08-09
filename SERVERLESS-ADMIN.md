# Current admin content backend

The active PEEC admin content service is the Lambda implementation under `lambda-admin-content/`. It stores content in DynamoDB and uploads administrator-managed pictures to S3.

Permissions:

- `SuperAdmin`: announcements, events, giving, pictures, and picture uploads.
- `ContentEditor`: announcements and events only.

The older Spring Boot application is retained for history and must not replace the active Lambda service.
