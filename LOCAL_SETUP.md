# Local Setup Guide

[← Back to README](README.md)

This guide provides step-by-step instructions for setting up and running the Price Alert Tracking System locally on both Windows and Mac operating systems.

## Prerequisites

### For Windows Users
1. Install Java Development Kit (JDK) 21 or later
   - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21) or [OpenJDK](https://adoptium.net/)
   - Set JAVA_HOME environment variable
   - Add Java to PATH
2. Install Maven
   - Download from [Maven website](https://maven.apache.org/download.cgi)
   - Extract to a directory (e.g., `C:\Program Files\Apache\maven`)
   - Add Maven bin directory to PATH
3. Install Git
   - Download from [Git website](https://git-scm.com/download/win)
   - Use default installation options

### For Mac Users
1. Install Homebrew (if not already installed)
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
2. Install Java
   ```bash
   brew install openjdk@21
   ```
3. Install Maven
   ```bash
   brew install maven
   ```
4. Install Git
   ```bash
   brew install git
   ```

## Project Setup

### Clone the Repository
```bash
git clone https://github.com/your-username/price-alert-api.git
cd price-alert-api
```

### Verify Installation

#### Windows
```powershell
java -version
mvn -version
```

#### Mac
```bash
java -version
mvn -version
```

## Running the Application

### Using Maven

#### Windows
```powershell
# Clean and build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Mac
```bash
# Clean and build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## Verifying the Setup

1. The application will start on port 8081
2. Open your browser and navigate to:
   ```
   http://localhost:8081/api/products
   ```
3. You should see a JSON response with the list of products

## Common Issues and Solutions

### Port Already in Use
If port 8081 is already in use, you can change it by:
1. Open `src/main/resources/application.properties`
2. Add or modify:
   ```properties
   server.port=8082
   ```

### Database Issues
The application uses H2 in-memory database. If you encounter database-related issues:
1. Check if the application has proper permissions to create temporary files
2. Verify that the products.json file exists in `src/main/resources/static/`
 

[← Back to README](README.md)
 