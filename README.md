# Smart Campus Sensor & Room Management API

## Coursework Submission README + Conceptual Report

This repository contains a JAX-RS RESTful API developed for the **5COSC022W Client-Server Architectures coursework**. The specification requires a **public GitHub repository**, a **README.md with project guidance**, and the **conceptual report answers organised inside the README.md on GitHub**. It also requires the solution to use **JAX-RS only**, avoid databases, and rely on in-memory Java collections instead.

---

## 1. Project Overview

The **Smart Campus Sensor & Room Management API** simulates a smart-campus backend where rooms can be created, sensors can be linked to rooms, and historical sensor readings can be stored and retrieved. The API follows RESTful design principles using JAX-RS and exposes versioned endpoints under `/api/v1` as required by the coursework.

The implementation is based on the uploaded project ZIP and currently contains:

- a `RestConfig` class with `@ApplicationPath("/api/v1")`
- a root discovery endpoint
- room management endpoints
- sensor management and filtering endpoints
- a sub-resource locator for sensor readings
- custom exception mappers for `409`, `422`, `403`, and a global `500`
- a request/response logging filter
- an in-memory singleton `DataStore`

All data is stored in memory, so the state resets whenever the server restarts.

---

## 2. API Design Summary

The coursework describes three core resource models: **Room**, **Sensor**, and **SensorReading**. It also expects a logical resource hierarchy that reflects the campus structure and sensor relationships.

### Main resources

- **Room**
  - Represents a physical campus room.
  - Fields in the current implementation: `id`, `name`, `capacity`

- **Sensor**
  - Represents a device installed in a room.
  - Fields in the current implementation: `id`, `type`, `status`, `currentValue`, `roomId`

- **SensorReading**
  - Represents a reading recorded for a sensor.
  - Fields in the current implementation: `timestamp`, `value`

### Resource hierarchy

- `/api/v1/rooms`
- `/api/v1/rooms/{id}`
- `/api/v1/sensors`
- `/api/v1/sensors?type=Temperature`
- `/api/v1/sensors/{sensorId}/readings`

This structure keeps top-level collections separate and uses a nested endpoint for readings because readings exist in the context of a specific sensor. That aligns with the coursework’s sub-resource requirement.

---

## 3. Technology Stack

- **Java 17**
- **JAX-RS / Jakarta REST**
- **Jersey 3.1.5**
- **Apache Tomcat**
- **Maven**
- **WAR packaging**

The coursework explicitly states that **JAX-RS must be used** and other frameworks such as Spring Boot are not allowed.

---

## 4. Project Structure

The current uploaded ZIP contains the following important files:

```text
smart-campus-api/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/com/janindu/smart/campus/api/
│       │   ├── exception/
│       │   │   ├── GlobalSafetyNet.java
│       │   │   ├── LinkedNotFoundMapper.java
│       │   │   ├── RoomNotEmptyMapper.java
│       │   │   └── SensorUnavailableMapper.java
│       │   ├── filter/
│       │   │   └── LoggingFilter.java
│       │   ├── model/
│       │   │   ├── Room.java
│       │   │   ├── Sensor.java
│       │   │   └── SensorReading.java
│       │   ├── resources/
│       │   │   ├── DiscoveryResource.java
│       │   │   ├── SensorReadingResource.java
│       │   │   ├── SensorResource.java
│       │   │   └── SensorRoomResource.java
│       │   └── service/
│       │       ├── DataStore.java
│       │       └── RestConfig.java
│       └── webapp/
│           ├── index.html
│           └── WEB-INF/
│               ├── beans.xml
│               └── web.xml
└── target/
```

### Package responsibilities

- **model**: POJO classes for API data
- **resources**: REST endpoints
- **service**: API bootstrapping and in-memory storage
- **exception**: custom exception mappers and global safety net
- **filter**: request/response logging

---

## 5. In-Memory Data Storage

The system does not use a database. Instead, it stores data in a singleton `DataStore` using concurrent collections.

Current implementation details:

- `rooms` -> `ConcurrentHashMap<String, Room>`
- `sensors` -> `ConcurrentHashMap<String, Sensor>`
- `sensorReadings` -> `ConcurrentHashMap<String, List<SensorReading>>`
- reading lists use `CopyOnWriteArrayList`

This design is consistent with the coursework requirement to avoid databases and use in-memory structures instead.

---

## 6. How to Build and Run

### 6.1 Clone the repository

```bash
git clone https://github.com/Janindu-Ekanayake/smart_campus_api.git
cd smart_campus_api
```

### 6.2 Build the WAR file

```bash
mvn clean package
```

### 6.3 Locate the generated WAR

```text
target/smart-campus-api-1.0-SNAPSHOT.war
```

### 6.4 Deploy to Tomcat

Copy the WAR file into the `webapps` folder of **Apache Tomcat 10.1+**.

```text
<TOMCAT_HOME>/webapps/smart-campus-api-1.0-SNAPSHOT.war
```

### 6.5 Start Tomcat

Run Tomcat normally.

### 6.6 Access the API

Base URL:

```text
http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1
```

Discovery endpoint:

```text
http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/
```

The specification requires explicit build instructions and server launch guidance in the GitHub README.

---

## 7. Available Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/` | Discovery endpoint |
| GET | `/api/v1/rooms` | Get all rooms |
| GET | `/api/v1/rooms/{id}` | Get one room by id |
| POST | `/api/v1/rooms` | Create a room |
| DELETE | `/api/v1/rooms/{id}` | Delete a room if no sensors are linked |
| GET | `/api/v1/sensors` | Get all sensors |
| GET | `/api/v1/sensors?type=Temperature` | Filter sensors by type |
| POST | `/api/v1/sensors` | Create a sensor linked to a room |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get reading history for one sensor |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a reading for one sensor |

---

## 8. Sample cURL Commands

The specification requires at least **five sample curl commands** in the README.

For convenience, define the base URL first:

```bash
BASE="http://localhost:8080/smart-campus-api/api/v1/"
```

### 8.1 Discovery endpoint

```bash
curl -X GET "$BASE/"
```

### 8.2 Get all rooms

```bash
curl -X GET "$BASE/rooms"
```

### 8.3 Create a room

```bash
curl -X POST "$BASE/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "R2",
    "name": "Library Lab",
    "capacity": 40
  }'
```

### 8.4 Get one room

```bash
curl -X GET "$BASE/rooms/R2"
```

### 8.5 Create a sensor linked to a valid room

```bash
curl -X POST "$BASE/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "S2",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 23.4,
    "roomId": "R2"
  }'
```

### 8.6 Try to create a sensor with an invalid roomId

```bash
curl -X POST "$BASE/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "S3",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 420.0,
    "roomId": "R999"
  }'
```

Expected result: `422 Unprocessable Entity`

### 8.7 Filter sensors by type

```bash
curl -X GET "$BASE/sensors?type=Temperature"
```

### 8.8 Get readings for a sensor

```bash
curl -X GET "$BASE/sensors/S1/readings"
```

### 8.9 Add a reading to a sensor

```bash
curl -X POST "$BASE/sensors/S1/readings" \
  -H "Content-Type: application/json" \
  -d '{
    "value": 24.8
  }'
```

### 8.10 Delete a room with no linked sensors

```bash
curl -X DELETE "$BASE/rooms/R2"
```

### 8.11 Attempt to delete a room that still has sensors

```bash
curl -X DELETE "$BASE/rooms/R1"
```

Expected result: `409 Conflict`

---

## 9. Example Error Handling

The API is designed to return structured JSON instead of raw server error pages.

### 409 Conflict
Returned when deleting a room that still has linked sensors.

### 422 Unprocessable Entity
Returned when a sensor is posted with a `roomId` that does not exist.

### 403 Forbidden
Returned when posting a reading to a sensor in `MAINTENANCE` mode.

### 500 Internal Server Error
Returned by the global safety-net mapper for unexpected runtime failures.

This aligns with the coursework requirement to make the API leak-proof and avoid raw stack traces in client-facing responses.

---

## 10. Logging

The uploaded project includes a `LoggingFilter` that logs:

- the HTTP method and URI for incoming requests
- the final HTTP status code for outgoing responses

The coursework specifically requires request/response logging through JAX-RS filters for observability.

---

## Conceptual Report (Question Answers)
 
---
 
### Q1.1 – JAX-RS Resource Lifecycle & Impact on In-Memory Data Management
 
By default, JAX-RS creates a **new resource instance per request**. Instance fields are not shared between requests. To persist data across calls, a `DataStore` singleton holds `ConcurrentHashMap` and `CopyOnWriteArrayList` — both thread-safe structures. This prevents race conditions when multiple requests run concurrently. Using a plain `HashMap` would risk lost updates or `ConcurrentModificationException` under concurrent load.
 
---
 
### Q1.2 – Why HATEOAS Is a Hallmark of Advanced RESTful Design
 
HATEOAS embeds navigation links inside responses, making the API self-describing. Clients discover available actions at runtime rather than relying on hard-coded URLs. If server paths change, link-following clients adapt without code changes. This reduces coupling, improves discoverability, and guides clients toward valid next steps — advantages that static documentation alone cannot provide.
 
---
 
### Q2.1 – Returning Only IDs vs Full Room Objects in a List
 
Returning only IDs produces small payloads but forces clients to make a follow-up request per room — the "N+1 problem" — increasing round-trips and latency. Returning full objects costs more bandwidth but serves all data in one call. A practical balance is returning a concise summary (id, name, capacity) in the list, reserving complete detail for `GET /{roomId}`.
 
---
 
### Q2.2 – Is DELETE Idempotent in This Implementation?
 
Yes. The first DELETE removes the room and returns `204 No Content`. A second identical request calls `rooms.remove(id)` on a missing key — a no-op in `ConcurrentHashMap` — and also returns `204 No Content`. Server state is identical after both calls (room absent), satisfying HTTP idempotency. The business constraint (sensors must be removed first) applies equally to both attempts.
 
---
 
### Q3.1 – Consequences of Mismatched `Content-Type` on `@Consumes(APPLICATION_JSON)`
 
If a client sends `Content-Type: text/plain` to a method annotated `@Consumes(APPLICATION_JSON)`, Jersey finds no compatible message body reader and automatically returns **HTTP 415 Unsupported Media Type** before the resource method executes. This protects the server from parsing incompatible payloads and gives the client a precise, actionable error without any custom exception-handling code required.
 
---
 
### Q3.2 – Query Parameter vs Path Segment for Filtering
 
`?type=CO2` is preferred over `/sensors/type/CO2` because path segments imply a distinct resource exists at that URI, which is semantically wrong for a filter. Query parameters are universally understood as optional collection modifiers, compose naturally (`?type=CO2&status=ACTIVE`), and allow one endpoint to serve both filtered and unfiltered requests without extra route definitions.
 
---
 
### Q4.1 – Architectural Benefits of the Sub-Resource Locator Pattern
 
The Sub-Resource Locator delegates nested-path handling to a dedicated class (`SensorReadingResource`), keeping each class focused on one responsibility. This prevents a single monolithic controller from becoming unmanageable. New nested concerns (e.g., `/sensors/{id}/alerts`) can be added as independent classes without modifying existing code, improving maintainability, testability, and overall readability of the API codebase.
 
---
 
### Q5.2 – Why HTTP 422 Is More Accurate Than 404 for a Missing Linked Resource
 
404 signals the **request URL** was not found. Here, the URL (`POST /api/v1/sensors`) is valid and reachable. The problem lies inside the **request body** — `roomId` references a non-existent room. HTTP 422 Unprocessable Entity conveys that the payload was syntactically correct but semantically invalid, giving clients a precise signal: fix the data in the body, not the URL.
 
---
 
### Q5.4 – Cybersecurity Risks of Exposing Java Stack Traces
 
Stack traces reveal internal package names, class structures, library versions, and server file paths. Attackers cross-reference library versions against CVE databases to find exploits. Pinpointed line numbers help craft inputs that reliably trigger failures. The `GlobalSafetyNet` mapper intercepts all `Throwable` instances and returns a generic 500 response with no internal details, keeping the implementation opaque to external consumers.
 
---
 
### Q5.5 – Why JAX-RS Filters Are Superior to Manual Logging in Resource Methods
 
Filters apply automatically to every request and response without modifying resource classes, enforcing **Separation of Concerns**. Manual logging scatters boilerplate across every method, reducing readability. If the logging format changes, only the single filter class needs updating rather than every resource method. Filters are also independently testable and can be toggled globally via configuration with zero impact on business logic.

---

## 12. Conclusion

This API demonstrates the main RESTful patterns required by the Smart Campus coursework:

- versioned JAX-RS setup
- resource collections for rooms and sensors
- query-parameter filtering
- nested sub-resources for sensor readings
- custom exception mapping
- global error protection
- request/response logging
- in-memory state management without a database

With the README updated, the conceptual report answers clearly documented, and a few final refinements applied to the codebase, the project will be much better aligned with both the specification and the rubric.
