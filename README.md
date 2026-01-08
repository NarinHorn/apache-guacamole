# Gucamole - Apache Guacamole Spring Boot Integration

A full-stack remote desktop gateway application that integrates **Apache Guacamole** with **Spring Boot** (backend) and **Next.js** (frontend). This project enables browser-based remote access to SSH, RDP, VNC, and other protocols through the Guacamole protocol.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
  - [1. Install and Configure guacd](#1-install-and-configure-guacd)
  - [2. Backend Setup (Spring Boot)](#2-backend-setup-spring-boot)
  - [3. Frontend Setup (Next.js)](#3-frontend-setup-nextjs)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing Flow](#testing-flow)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

**Gucamole** is a web-based remote desktop gateway that allows users to access remote machines directly from their browser. It leverages:

- **Apache Guacamole**: An open-source clientless remote desktop gateway supporting VNC, RDP, SSH, and Telnet.
- **Spring Boot**: Backend server that handles tunnel connections between the browser and guacd.
- **Next.js**: Modern React frontend that renders the remote desktop display.

### Key Features

- Browser-based remote desktop access (no client installation required)
- SSH protocol support (extensible to RDP, VNC, Telnet)
- Real-time keyboard input forwarding
- CORS-enabled for cross-origin frontend communication

---

## Architecture

```
┌─────────────────┐     HTTP Tunnel     ┌──────────────────┐     Guacamole Protocol    ┌─────────┐
│   Next.js UI    │ ◄─────────────────► │  Spring Boot     │ ◄──────────────────────►  │  guacd  │
│  (Port 3000)    │                     │  (Port 8080)     │                           │ (4822)  │
└─────────────────┘                     └──────────────────┘                           └────┬────┘
                                                                                            │
                                                                                            ▼
                                                                                    ┌───────────────┐
                                                                                    │ Remote Server │
                                                                                    │  (SSH/RDP/VNC)│
                                                                                    └───────────────┘
```

---

## Technology Stack

### Backend
| Component | Version | Description |
|-----------|---------|-------------|
| Java | 21 | Programming language |
| Spring Boot | 4.0.1 | Web framework |
| Guacamole Common | 1.5.5 | Guacamole Java library |
| Guacamole Ext | 1.5.5 | Guacamole extension library |
| Gradle | 8.x | Build tool |
| Lombok | - | Boilerplate code reduction |

### Frontend
| Component | Version | Description |
|-----------|---------|-------------|
| Next.js | 16.1.1 | React framework |
| React | 19.2.3 | UI library |
| guacamole-common-js | 1.5.0 | Guacamole JavaScript client |
| TailwindCSS | 4.x | Styling |

---

## Prerequisites

Before setting up the project, ensure you have the following installed:

1. **Java 21** (JDK)
   ```bash
   java -version
   ```

2. **Node.js 18+** and **npm**
   ```bash
   node -v
   npm -v
   ```

3. **guacd** (Guacamole Daemon)
   - This is the core Guacamole proxy daemon that handles remote connections
   - Must be running on port **4822** (default)

4. **A target remote server** for testing (SSH, RDP, or VNC enabled)

---

## Project Setup

### 1. Install and Configure guacd

**guacd** is the Guacamole proxy daemon that communicates with remote desktop servers.

#### Option A: Using Docker (Recommended)

```bash
docker run -d --name guacd -p 4822:4822 guacamole/guacd
```

#### Option B: Install on Linux

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install guacd

# Start the service
sudo systemctl start guacd
sudo systemctl enable guacd
```

#### Verify guacd is running

```bash
# Check if port 4822 is listening
netstat -tlnp | grep 4822

# Or using Docker
docker logs guacd
```

---

### 2. Backend Setup (Spring Boot)

#### Step 1: Clone or navigate to the project directory

```bash
cd d:\hunesion\apache_guacamole\apache-guacamole-spring-boot\gucamole\gucamole
```

#### Step 2: Verify Guacamole JAR files

Ensure the following JAR files exist in `guacamole-client/` directory:
- `guacamole-common-1.5.5.jar`
- `guacamole-ext-1.5.5.jar`

These are already included in the project.

#### Step 3: Configure the connection settings

Edit `src/main/java/com/hunesion/gucamole/GuacamoleController.java` to set your target server:

```java
config.setProtocol("ssh");                    // Protocol: ssh, rdp, vnc, telnet
config.setParameter("hostname", "YOUR_HOST"); // Target server IP/hostname
config.setParameter("port", "22");            // Port number
config.setParameter("username", "YOUR_USER"); // Username
config.setParameter("password", "YOUR_PASS"); // Password
```

#### Step 4: Build the project

```bash
# Windows
.\gradlew.bat build

# Linux/macOS
./gradlew build
```

#### Step 5: Run the backend server

```bash
# Windows
.\gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```

The backend will start on **http://localhost:8080**.

---

### 3. Frontend Setup (Next.js)

#### Step 1: Navigate to the UI directory

```bash
cd UI-Guacamole
```

#### Step 2: Install dependencies

```bash
npm install
```

#### Step 3: Start the development server

```bash
npm run dev
```

The frontend will start on **http://localhost:3000**.

---

## Configuration

### Backend Configuration

| File | Description |
|------|-------------|
| `src/main/resources/application.yml` | Spring Boot application properties |
| `GuacamoleController.java` | Connection configuration (protocol, host, credentials) |
| `TunnelCorsFilterConfig.java` | CORS settings for frontend communication |
| `GuacServletConfig.java` | Servlet registration for tunnel endpoint |

### CORS Configuration

The backend is configured to accept requests from `http://localhost:3000`. To change this, modify `TunnelCorsFilterConfig.java`:

```java
if ("http://localhost:3000".equals(origin)) {
    // Change to your frontend URL
}
```

### Frontend Configuration

The tunnel endpoint is configured in `UI-Guacamole/app/page.js`:

```javascript
const tunnel = new Guacamole.HTTPTunnel("http://localhost:8080/tunnel", true);
```

---

## Running the Application

### Quick Start (All Components)

1. **Start guacd** (if using Docker):
   ```bash
   docker start guacd
   ```

2. **Start the backend** (Terminal 1):
   ```bash
   cd d:\hunesion\apache_guacamole\apache-guacamole-spring-boot\gucamole\gucamole
   .\gradlew.bat bootRun
   ```

3. **Start the frontend** (Terminal 2):
   ```bash
   cd d:\hunesion\apache_guacamole\apache-guacamole-spring-boot\gucamole\gucamole\UI-Guacamole
   npm run dev
   ```

4. **Open your browser**: Navigate to **http://localhost:3000**

---

## Testing Flow

### 1. Unit Tests (Backend)

Run the Spring Boot test suite:

```bash
# Windows
.\gradlew.bat test

# Linux/macOS
./gradlew test
```

The test class `GucamoleApplicationTests.java` verifies that the Spring application context loads correctly.

### 2. Integration Testing

#### Step 1: Verify guacd connectivity

```bash
# Test if guacd is reachable
telnet localhost 4822
```

#### Step 2: Test the tunnel endpoint

```bash
# Using curl to test the backend endpoint
curl -X OPTIONS http://localhost:8080/tunnel -v
```

Expected response: HTTP 204 (No Content) with CORS headers.

#### Step 3: End-to-End Testing

1. Ensure all three components are running:
   - guacd on port 4822
   - Spring Boot backend on port 8080
   - Next.js frontend on port 3000

2. Open **http://localhost:3000** in your browser

3. Observe the status bar:
   - `Init...` → Initial state
   - `Loading guacamole-common-js...` → Loading client library
   - `Connecting...` → Establishing tunnel connection
   - `Connected (or connecting...)` → Tunnel established

4. If successful, you should see the remote terminal/desktop in the browser

### 3. Manual Testing Checklist

| Test Case | Expected Result |
|-----------|-----------------|
| Backend starts without errors | Server running on port 8080 |
| Frontend starts without errors | Development server on port 3000 |
| CORS preflight request | HTTP 204 response |
| Tunnel connection | Status shows "Connected" |
| Keyboard input | Characters appear in remote terminal |
| Connection disconnect | Clean disconnect on page close |

### 4. Debugging

Check backend logs for connection details:

```
INFO  - Tunnel connection requested!
INFO  - Remote address: 127.0.0.1
INFO  - Connecting to guacd at localhost:4822
INFO  - Connection successful!
```

If connection fails, check:
- guacd is running and accessible
- Target server credentials are correct
- Network connectivity to target server

---

## Project Structure

```
gucamole/
├── src/
│   ├── main/
│   │   ├── java/com/hunesion/gucamole/
│   │   │   ├── GucamoleApplication.java       # Main application entry point
│   │   │   ├── GuacamoleController.java       # Tunnel controller & connection config
│   │   │   ├── GuacServletConfig.java         # Servlet registration
│   │   │   ├── TunnelCorsFilterConfig.java    # CORS filter configuration
│   │   │   ├── CorsConfig.java                # (Commented) MVC CORS config
│   │   │   └── TutorialGuacamoleTunnelServlet.java  # (Template) Tunnel servlet
│   │   └── resources/
│   │       ├── application.yml                # Application configuration
│   │       ├── static/                        # Static resources
│   │       └── templates/                     # Template files
│   └── test/
│       └── java/com/hunesion/gucamole/
│           └── GucamoleApplicationTests.java  # Unit tests
├── UI-Guacamole/                              # Next.js Frontend
│   ├── app/
│   │   ├── page.js                            # Main Guacamole client page
│   │   ├── layout.js                          # Root layout
│   │   └── globals.css                        # Global styles
│   ├── package.json                           # Node dependencies
│   └── next.config.mjs                        # Next.js configuration
├── guacamole-client/                          # Guacamole JAR libraries
│   ├── guacamole-common-1.5.5.jar
│   └── guacamole-ext-1.5.5.jar
├── build.gradle                               # Gradle build configuration
├── settings.gradle                            # Gradle settings
└── README.md                                  # This file
```

---

## Troubleshooting

### Common Issues

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| `Connection refused on port 4822` | guacd not running | Start guacd: `docker start guacd` |
| `CORS error in browser` | Origin mismatch | Update `TunnelCorsFilterConfig.java` with correct origin |
| `Failed to connect` | Wrong credentials | Check hostname, port, username, password in `GuacamoleController.java` |
| `Class not found: GuacamoleHTTPTunnelServlet` | Missing JAR files | Verify `guacamole-client/*.jar` files exist |
| Frontend shows `Error:` | Network/backend issue | Check backend logs and ensure it's running |

### Useful Commands

```bash
# Check if ports are in use
netstat -ano | findstr :8080
netstat -ano | findstr :3000
netstat -ano | findstr :4822

# View Docker container logs
docker logs guacd

# Clean and rebuild
.\gradlew.bat clean build
```

---

## License

This project integrates with Apache Guacamole, which is licensed under the Apache License 2.0.

---

## References

- [Apache Guacamole Official Documentation](https://guacamole.apache.org/doc/gug/)
- [Guacamole Common JS](https://github.com/apache/guacamole-client)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Next.js Documentation](https://nextjs.org/docs)
