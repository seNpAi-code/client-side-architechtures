[README.md](https://github.com/user-attachments/files/29729099/README.md)
# MLOps Pipeline Management API

## 1. Overview
The MLOps Pipeline Management API is a robust, scalable RESTful service built using Java and JAX-RS (Jersey) with an embedded Grizzly HTTP server. It is designed to manage Machine Learning Workspaces, the AI Models deployed within them, and the Evaluation Metrics generated during model training/evaluation. All data is managed efficiently in-memory to simulate a lightweight cloud-native environment without external database dependencies.

## 2. Build & Launch Instructions

### Prerequisites
- JDK 17 (or newer) installed.
- No global Maven installation required (a Maven wrapper is included).

### Build the Project
Open a terminal in the root of the project (where this README is located) and run the following command to compile the code and build an executable "fat" JAR:

**Windows:**
```powershell
.\mvnw.cmd clean package
```

**Linux/Mac:**
```bash
./mvnw clean package
```

### Launch the Server
You can launch the embedded Grizzly server directly using the Maven plugin:
```powershell
.\mvnw.cmd exec:java
```
Or you can run the packaged JAR file directly:
```powershell
java -jar target/mlops-api-1.0-SNAPSHOT.jar
```
The server will start at `http://localhost:8080/api/v1/`

---

## 3. Sample cURL Commands

Here are 5 commands demonstrating the core functionality:

**1. Discover API Metadata (GET /api/v1)**
```bash
curl -X GET http://localhost:8080/api/v1
```

**2. Create a Workspace (POST /api/v1/workspaces)**
```bash
curl -X POST http://localhost:8080/api/v1/workspaces \
  -H "Content-Type: application/json" \
  -d "{\"teamName\":\"Computer Vision Lab\", \"storageQuotaGb\":500}"
```
*(Make note of the returned `id`, e.g., "WS-1234", for the next command)*

**3. Register a Model to the Workspace (POST /api/v1/models)**
```bash
curl -X POST http://localhost:8080/api/v1/models \
  -H "Content-Type: application/json" \
  -d "{\"framework\":\"TensorFlow\", \"workspaceId\":\"WS-1234\"}"
```
*(Make note of the returned `id`, e.g., "MOD-5678", for the next command)*

**4. Add an Evaluation Metric to a Model (POST /api/v1/models/{modelId}/metrics)**
```bash
curl -X POST http://localhost:8080/api/v1/models/MOD-5678/metrics \
  -H "Content-Type: application/json" \
  -d "{\"timestamp\":1712056000000, \"accuracyScore\":0.94}"
```

**5. Fetch Models filtered by Status (GET /api/v1/models?status=TRAINING)**
```bash
curl -X GET "http://localhost:8080/api/v1/models?status=TRAINING"
```

---

## 4. Conceptual Report Answers

### Part 1
**Q: Explain the role of a MessageBodyWriter or a JSON provider (like Jackson) in this conversion process.**
A: In JAX-RS, returning a plain Java Object (POJO) from an endpoint doesn't automatically mean the client receives JSON. A `MessageBodyWriter` acts as a serializer bridge between the Java runtime and the HTTP response stream. A JSON provider like Jackson implements this interface for the `application/json` media type. When a method returns a Java object, the JAX-RS runtime searches its registered providers for a `MessageBodyWriter` that supports the object's type and the requested media type. Jackson takes the Java object, uses reflection to read its properties, converts them into a valid JSON string, and writes that string into the HTTP response body.

**Q: Define what statelessness means in this context and explain why it makes cloud APIs easier to scale horizontally across multiple servers.**
A: In REST, statelessness means that the server does not store any client context or session state between requests. Every single HTTP request from a client must contain all the information necessary for the server to understand and process it. This makes horizontal scaling (adding more servers) incredibly easy because a load balancer can route any request to any available server; the server doesn't need to know what happened in previous requests. There is no need for complex session-synchronization or "sticky routing" between servers, eliminating memory bottlenecks and improving overall resilience.

### Part 2
**Q: Discuss how implementing HTTP Cache-Control headers on the GET workspaces endpoint could improve performance for the client and reduce unnecessary processing load on the server.**
A: Adding a `Cache-Control` header (e.g., `Cache-Control: max-age=60`) instructs the client (or intermediary proxies/CDNs) that the response data is considered fresh for a specific duration (60 seconds). If the client needs the workspace list again within that window, it can serve the data from its local cache instead of making a network trip. This dramatically reduces network latency for the client (improving performance/UI responsiveness) and reduces processing load on the server, as it avoids executing the endpoint logic, serializing JSON, and allocating bandwidth for repeated identical requests.

**Q: If a client needs to verify whether a specific workspace exists but wants to save bandwidth by not downloading the entire JSON body, which HTTP method should they use instead of GET? Explain your reasoning.**
A: The client should use the `HEAD` HTTP method. The `HEAD` method is explicitly designed to ask the server for the exact same HTTP headers (including the status code like 200 OK or 404 Not Found) it would return for a `GET` request, but without returning the actual response body. This perfectly satisfies the requirement: the client receives a 200 OK if the workspace exists (verifying existence) while saving bandwidth because the potentially large JSON body of the workspace is omitted from the network transfer.

### Part 3
**Q: Discuss the security and data integrity reasons behind the architectural choice of the server generating the unique id.**
A: Allowing clients to generate and provide IDs (Client-Side ID Generation) poses significant data integrity risks. A malicious or buggy client could easily generate duplicate IDs, attempting to overwrite existing records (a form of ID-collision or hijacking). It also requires the server to perform expensive "does this ID already exist?" lookups before insertion. By delegating ID generation to the server (e.g., using UUIDs), the system guarantees cryptographic uniqueness, prevents unauthorized overwrites, ensures consistent ID formatting, and shifts the trust boundary back to the server, protecting the overall integrity of the data store.

**Q: If a user attempts to search for a framework containing spaces or special characters, how must the client modify the URL, and why is this encoding necessary?**
A: The client must URL-encode the parameters. For example, `Scikit Learn & Tools` becomes `Scikit%20Learn%20%26%20Tools` (where `%20` represents a space and `%26` represents the ampersand). This is strictly necessary because the HTTP specification defines URLs using a limited subset of ASCII characters. Spaces are invalid in URLs, and special characters like `&` and `?` have reserved structural meanings (defining query boundaries). If the ampersand isn't encoded, the server will incorrectly interpret `& Tools` as the start of a completely new, separate query parameter.

### Part 4
**Q: What is the benefit of class-level placement, and how does method-level overriding work?**
A: Placing `@Produces(MediaType.APPLICATION_JSON)` at the class level enforces a default media type for every endpoint within that resource class, promoting DRY (Don't Repeat Yourself) principles and keeping the code much cleaner by omitting repetitive annotations on every single method. 
Method-level overriding occurs when a specific method defines its own `@Produces` annotation. In JAX-RS, method-level annotations take precedence over class-level ones. This allows you to have a class that generally serves JSON, but override a single specific method to serve something else, like `@Produces("text/csv")` for an export endpoint, providing flexibility while maintaining a clean default.

### Part 5
**Q: Explain fundamentally why a validation failure caused by the user providing a non-existent workspaceId must return a 4xx code rather than a 5xx code.**
A: The HTTP status code taxonomy dictates that `4xx` (Client Error) codes indicate that the *client* made a mistake (e.g., sending invalid data, a malformed request, or an ID that doesn't exist). A `5xx` (Server Error) code indicates that the request was perfectly valid, but the *server* failed to fulfill it due to an internal bug, crash, or misconfiguration. Returning a 5xx for a validation failure is fundamentally incorrect because the server functioned exactly as designed (it caught the bad input); the client must be informed via a 4xx code (like 422 Unprocessable Entity or 400 Bad Request) so it knows it needs to fix its payload before trying again.

**Q: How does the JAX-RS runtime determine which mapper to execute?**
A: The JAX-RS runtime resolves exception mappers based on a "closest match" inheritance hierarchy. When an exception is thrown, the runtime checks the type of the exception and looks for a registered `ExceptionMapper` parameterized with that exact type. If an exact match (e.g., `ExceptionMapper<LinkedWorkspaceNotFoundException>`) exists, it is chosen. If no exact match exists, the runtime walks up the exception's class hierarchy (checking superclasses) until it finds a match. Therefore, a specific custom mapper will always take precedence over a generic `ExceptionMapper<Throwable>`, which acts as a final fallback catch-all at the very top of the hierarchy.

**Q: List two pieces of crucial HTTP metadata you can extract from these contexts that are highly valuable for debugging server issues.**
A: Two highly valuable pieces of metadata are:
1. **HTTP Headers (e.g., User-Agent, Authorization, X-Request-ID):** Extractable via `requestContext.getHeaders()`. For example, tracing a request through a microservices architecture using an `X-Request-ID` is essential for debugging where exactly an error occurred.
2. **The Request URI / Path:** Extractable via `requestContext.getUriInfo().getRequestUri()`. Knowing the exact path and query parameters that triggered an error is critical for reproducing the issue locally and understanding what resource the client was attempting to interact with.
