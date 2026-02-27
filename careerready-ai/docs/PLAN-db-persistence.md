# PLAN: Database Persistence for CareerReady AI

This plan defines the shift from `localStorage` to a server-side H2 database to ensure persistent chat sessions across reboots, identifying sessions by unique IDs and implementing lazy loading for message history.

## Phase 1: Infrastructure Setup
- [ ] **Dependency Update**: Add `spring-boot-starter-data-jpa` and `com.h2database:h2` to `backend/pom.xml`.
- [ ] **Configuration**: Set up `application.properties` for H2 file-based persistence (e.g., `jdbc:h2:file:./data/chatbotdb`).

## Phase 2: Schema & Data Model
- [ ] **ChatSession Entity**:
    - `String id` (UUID)
    - `String title` (First user message snippet)
    - `LocalDateTime createdAt`
- [ ] **ChatMessageEntity**:
    - `Long id`
    - `String sessionId` (Fk to ChatSession)
    - `String role` (user/assistant)
    - `String content` (text)
    - `LocalDateTime timestamp`

## Phase 3: Backend Implementation
- [ ] **Repositories**: `ChatSessionRepository` and `ChatMessageRepository`.
- [ ] **Service Layer**:
    - `createSession()`: Initializes a new UUID session.
    - `saveMessage(sessionId, role, content)`: Persists messages.
    - `getSessionHistory(sessionId, limit, offset)`: Implements **Lazy Loading** logic.
- [ ] **Controller Layer**:
    - `POST /api/chat`: Refactor to accept `sessionId` and persist interaction.
    - `GET /api/sessions`: List all persisted sessions.
    - `GET /api/sessions/{id}/messages?page=0&size=20`: Fetch paginated messages.

## Phase 4: Frontend Implementation
- [ ] **Session Tracking**: Store current `sessionId` in JavaScript memory/cookie.
- [ ] **Sidebar Sync**: Fetch saved sessions on load from `/api/sessions`.
- [ ] **Lazy Load Trigger**: "Load More" button or intersection observer for historical messages.

## Verification Checklist
- [ ] Verify `chatbotdb.mv.db` file is created in project root.
- [ ] Verify data remains after `mvn spring-boot:run` restart.
- [ ] Verify frontend successfully renders list of previous sessions.
