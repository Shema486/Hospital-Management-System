# Hospital Management System

A comprehensive desktop application for managing hospital operations, built with Java, JavaFX, and PostgreSQL. This system demonstrates advanced software engineering practices including 3-layer architecture, caching, database optimization, and performance measurement.

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [Project Structure](#-project-structure)
- [Key Features](#-key-features)
- [Performance Optimizations](#-performance-optimizations)
- [Database Schema](#-database-schema)
- [Documentation](#-documentation)
- [Contributing](#-contributing)

---

## ✨ Features

### Core Functionality
- ✅ **Patient Management** - Full CRUD operations with search and pagination
- ✅ **Doctor Management** - Manage doctors with specializations and departments
- ✅ **Department Management** - Organize hospital departments
- ✅ **Appointment Scheduling** - Schedule and manage patient appointments
- ✅ **Prescription Management** - Create and manage prescriptions with items
- ✅ **Medical Inventory** - Track medical supplies and medications
- ✅ **Patient Feedback** - Collect and view patient ratings and comments
- ✅ **Reports** - View patient appointment history and statistics

### Advanced Features
- ✅ **Pagination** - Efficient data loading (10 items per page)
- ✅ **Search Functionality** - Database and in-memory search
- ✅ **Sorting** - Multiple sorting algorithms (by name, ID, specialization)
- ✅ **Caching** - HashMap-based caching (90-98% performance improvement)
- ✅ **Database Indexing** - Optimized queries with B-tree indexes
- ✅ **Input Validation** - Comprehensive form validation
- ✅ **Error Handling** - Robust error handling throughout

---

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming language |
| **JavaFX** | 21.0.6 | Desktop GUI framework |
| **PostgreSQL** | Latest | Relational database |
| **JDBC** | Built-in | Database connectivity |
| **Maven** | Latest | Build tool & dependency management |

### Key Libraries
- JavaFX Controls & FXML
- PostgreSQL JDBC Driver (42.7.8)
- ControlsFX (11.2.1)
- JUnit 5 (for testing)

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

1. **Java Development Kit (JDK) 21** or higher
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify: `java -version`

2. **PostgreSQL Database**
   - Download from [PostgreSQL Official Site](https://www.postgresql.org/download/)
   - Verify: `psql --version`

3. **Maven** (optional, project includes Maven Wrapper)
   - Download from [Maven Official Site](https://maven.apache.org/download.cgi)
   - Verify: `mvn -version`

4. **Git** (for cloning the repository)
   - Download from [Git Official Site](https://git-scm.com/downloads)

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd hospital_management_system
```

### 2. Set Up PostgreSQL Database

#### Create Database
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE hospital_db;

# Exit psql
\q
```

#### Run Schema Script
```bash
# From project root
psql -U postgres -d hospital_db -f database/schema.sql
```

Or manually:
```bash
psql -U postgres -d hospital_db
\i database/schema.sql
\q
```

### 3. Configure Environment Variables

Create a `.env` file in the project root directory:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=postgres
DB_PASSWORD=your_password_here
```

**Note**: The `.env` file is in `.gitignore` and won't be committed. 

### 4. Build the Project

```bash
# Using Maven Wrapper (recommended)
./mvnw clean compile

# Or using Maven (if installed)
mvn clean compile
```

---

## ⚙️ Configuration

### Database Configuration

The application reads database credentials from a `.env` file. Create `.env` in the project root:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=postgres
DB_PASSWORD=your_actual_password
```

**Default Values** (if `.env` not found):
- Host: `localhost`
- Port: `5432`
- Database: `hospital_db`
- User: `postgres`
- Password: `postgres`

For more details,

---

## 🏃 Running the Application

### Start the Application

```bash
# Using Maven Wrapper
./mvnw javafx:run

# Or using Maven
mvn javafx:run
```

### Run Performance Demo

```bash
# See caching performance in action
mvn exec:java -Dexec.mainClass="hospital.hospital_management_system.test.PerformanceDemo"
```

### Run Tests

```bash
mvn test
```

---

## 📁 Project Structure

```
hospital_management_system/
├── database/
│   ├── schema.sql                          # Database schema
│   └── DATABASE_SCHEMA_DOCUMENTATION.md   # Schema documentation
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── hospital/
│   │   │       └── hospital_management_system/
│   │   │           ├── controller/         # UI controllers (MVC)
│   │   │           ├── dao/                # Data Access Objects
│   │   │           ├── model/              # Entity classes
│   │   │           ├── services/           # Business logic + caching
│   │   │           ├── utils/              # Utilities (DBConnection, EnvLoader)
│   │   │           └── test/                # Performance tests
│   │   └── resources/
│   │       └── hospital/
│   │           └── hospital_management_system/
│   │               ├── *.fxml              # UI layouts
│   │               └── app.css             # Styling
│   └── test/                               # Unit tests
├── .env                                    # Database credentials (not in Git)
├── .gitignore                              # Git ignore rules
├── pom.xml                                 # Maven configuration
└── README.md                               # This file
```

### Architecture

```
┌─────────────────────────────────────┐
│     PRESENTATION LAYER              │
│  (Controllers + FXML Views)        │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│     BUSINESS LOGIC LAYER            │
│  (Services + Caching)               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│     DATA ACCESS LAYER               │
│  (DAOs + JDBC)                      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│     DATABASE                        │
│  (PostgreSQL)                       │
└─────────────────────────────────────┘
```

---

## 🎯 Key Features

### 1. Pagination
- **10 items per page** for optimal performance
- **80-95% faster** page loads compared to loading all records
- Automatic page count calculation
- Disabled during search (shows all results)


### 2. Caching System
- **HashMap-based caching** in Service layer
- **O(1) lookup time** for cached data
- **90-98% performance improvement** for repeated access
- Automatic cache invalidation on updates/deletes


### 3. Database Optimization
- **B-tree indexes** on frequently searched columns
- **75-97% faster** searches with indexes
- **3NF normalization** for data integrity
- **PreparedStatement** for SQL injection prevention

### 4. Search & Sort
- **Database search**: Case-insensitive ILIKE queries
- **In-memory search**: Java Streams for cached data
- **Multiple sorting**: By name, ID, specialization, date
- **TimSort algorithm**: O(n log n) complexity

---

## ⚡ Performance Optimizations

| Optimization | Improvement | Implementation |
|--------------|-------------|----------------|
| **HashMap Caching** | 90-98% faster | Service layer |
| **Database Indexing** | 75-97% faster | SQL CREATE INDEX |
| **Pagination** | 80-95% faster | LIMIT/OFFSET |
| **Prepared Statements** | Security + Speed | JDBC best practice |

**Detailed Report**: [PERFORMANCE_REPORT.md](PERFORMANCE_REPORT.md)

---

## 🗄️ Database Schema

### Tables (8 total)
1. **patients** - Patient demographic information
2. **doctors** - Doctor information and specializations
3. **departments** - Hospital departments
4. **appointments** - Patient-doctor appointments
5. **prescriptions** - Prescription records
6. **prescription_items** - Items in prescriptions
7. **medical_inventory** - Medical supplies and medications
8. **patient_feedback** - Patient ratings and comments

### Relationships
- One-to-Many: Patient → Appointments, Doctor → Appointments
- Many-to-Many: Prescriptions ↔ Inventory (via prescription_items)
- One-to-One: Appointment → Prescription

**Full Documentation**: [database/DATABASE_SCHEMA_DOCUMENTATION.md](database/DATABASE_SCHEMA_DOCUMENTATION.md)

---




---

## 🧪 Testing

### Run Performance Demo
```bash
mvn exec:java -Dexec.mainClass="hospital.hospital_management_system.test.PerformanceDemo"
```

This demonstrates:
- Caching performance (90-98% improvement)
- Sorting algorithms
- Search functionality

### Run Unit Tests
```bash
mvn test
```

---

## 🐛 Troubleshooting

### Database Connection Issues

**Problem**: "Database connection failed"

**Solutions**:
1. Verify PostgreSQL is running: `pg_isready`
2. Check `.env` file exists and has correct credentials
3. Verify database exists: `psql -U postgres -l`
4. Check database name matches in `.env`

### Application Won't Start

**Problem**: "JavaFX runtime components are missing"

**Solutions**:
1. Ensure JDK 21+ is installed
2. Check JavaFX dependencies in `pom.xml`
3. Try: `mvn clean compile javafx:run`

### Pagination Not Working

**Problem**: Pagination shows wrong number of pages

**Solutions**:
1. Check database has data
2. Verify `getTotalPatientsCount()` returns correct value
3. Check pagination initialization in controller

---

## 🔒 Security

- ✅ **SQL Injection Prevention**: All queries use PreparedStatement
- ✅ **Environment Variables**: Sensitive data in `.env` (not in Git)
- ✅ **Input Validation**: Comprehensive validation in controllers
- ✅ **Error Handling**: Secure error messages (no sensitive data exposed)

---

## 📊 Project Statistics

- **8 Database Tables** with proper relationships
- **8 DAO Classes** for data access
- **8 Service Classes** with caching
- **8 Controllers** for UI handling
- **8 FXML Views** for user interface
- **~3000+ Lines of Code**
- **90-98% Performance Improvement** with caching
- **3NF Normalized Database**

---

## 🎓 Learning Outcomes

This project demonstrates:
- ✅ **3-Layer Architecture** (MVC pattern)
- ✅ **Database Design** (Normalization, Indexing)
- ✅ **Performance Optimization** (Caching, Indexing, Pagination)
- ✅ **Data Structures** (HashMap, ArrayList)
- ✅ **Algorithms** (Sorting, Searching)
- ✅ **Best Practices** (PreparedStatement, Error Handling)

---

## 🤝 Contributing

This is a learning project. Suggestions and improvements are welcome!

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is for educational purposes.

---

## 👤 Author

Hospital Management System - Educational Project

---

## 🙏 Acknowledgments

- JavaFX Community
- PostgreSQL Team
- Maven Project
- All contributors and reviewers

---

## 📞 Support

For issues, questions, or contributions:
- Check the [Documentation](#-documentation) section
- Review [Troubleshooting](#-troubleshooting) guide
- Open an issue in the repository

---

## 🚀 Quick Commands Reference

```bash
# Setup
psql -U postgres -d hospital_db -f database/schema.sql

# Build
mvn clean compile

# Run
mvn javafx:run

# Test Performance
mvn exec:java -Dexec.mainClass="hospital.hospital_management_system.test.PerformanceDemo"

# Run Tests
mvn test
```

---

**Happy Coding! 🎉**

For detailed information, check the [Documentation](#-documentation) section above.
