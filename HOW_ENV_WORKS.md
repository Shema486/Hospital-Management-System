# How .env File Works in This Project
## Complete Guide to Environment Variables

---

## 📋 What is a .env File?

A `.env` file is a **configuration file** that stores sensitive information like database credentials, API keys, and other environment-specific settings. It's kept separate from your code for security reasons.

**Why Use .env?**
- ✅ **Security**: Keeps sensitive data out of your code
- ✅ **Flexibility**: Different settings for development/production
- ✅ **Best Practice**: Industry standard for configuration management
- ✅ **Version Control**: `.env` is in `.gitignore` (not committed to Git)

---

## 🏗️ How It Works in This Project

### Architecture Flow

```
.env file (on disk)
    ↓
EnvLoader.java (reads .env)
    ↓
DBConnection.java (uses values)
    ↓
Database connection
```

---

## 📝 .env File Format

### Basic Structure

Create a file named `.env` in your project root directory:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=postgres
DB_PASSWORD=your_password_here

# Alternative: Full Database URL
# DB_URL=jdbc:postgresql://localhost:5432/hospital_db
```

### Format Rules

1. **Key=Value pairs**: One per line
2. **No spaces around =**: `DB_HOST=localhost` (not `DB_HOST = localhost`)
3. **Comments**: Start with `#`
4. **Quotes optional**: Values can be quoted or unquoted
5. **Case sensitive**: Keys are case-sensitive (but EnvLoader normalizes to uppercase)

### Example .env File

```env
# Hospital Management System - Database Configuration
# Copy this file to .env and update with your values

# Database Host (default: localhost)
DB_HOST=localhost

# Database Port (default: 5432)
DB_PORT=5432

# Database Name
DB_NAME=hospital_db

# Database Username
DB_USER=postgres

# Database Password (CHANGE THIS!)
DB_PASSWORD=mypassword123

# Optional: Full Database URL (overrides host/port/name)
# DB_URL=jdbc:postgresql://localhost:5432/hospital_db
```

---

## 🔧 How EnvLoader Works

### File: `EnvLoader.java`

**Purpose**: Loads environment variables from multiple sources

**Loading Order** (priority from highest to lowest):

1. **System Environment Variables** (highest priority)
2. **Custom ENV_PATH** (if set)
3. **Current directory `.env`** (project root)
4. **Parent directories `.env`** (searches up to 6 levels)
5. **Classpath `.env`** (from resources folder)

### Key Methods

#### 1. `loadEnv()` - Main loader
```java
private static void loadEnv() {
    // 1. Load system environment variables
    envVars.putAll(System.getenv());
    
    // 2. Try custom path
    String customPath = System.getenv("ENV_PATH");
    if (customPath != null) {
        loadFromFile(Paths.get(customPath));
    }
    
    // 3. Try current directory
    Path cwdEnv = Paths.get(System.getProperty("user.dir")).resolve(".env");
    loadFromFile(cwdEnv);
    
    // 4. Try parent directories
    loadFromParentDirs(Paths.get(System.getProperty("user.dir")));
    
    // 5. Try classpath
    loadFromClasspath(".env");
    loadFromClasspath("hospital/hospital_management_system/.env");
}
```

#### 2. `get(key, defaultValue)` - Get value
```java
public static String get(String key, String defaultValue) {
    return envVars.getOrDefault(key, defaultValue);
}
```

**Usage**:
```java
String dbHost = EnvLoader.get("DB_HOST", "localhost"); // Returns value or "localhost"
```

---

## 💾 How DBConnection Uses .env

### File: `DBConnection.java`

**How it reads from .env**:

```java
public final class DBConnection {
    // Read from .env file (or use defaults)
    private static final String DB_HOST = EnvLoader.get("DB_HOST", "localhost");
    private static final String DB_PORT = EnvLoader.get("DB_PORT", "5432");
    private static final String DB_NAME = EnvLoader.get("DB_NAME", "hospital_db");
    private static final String DB_USER = EnvLoader.get("DB_USER", "postgres");
    private static final String DB_PASSWORD = EnvLoader.get("DB_PASSWORD", "postgres");
    
    public static Connection getConnection() throws SQLException {
        // Build connection URL from .env values
        String url = String.format("jdbc:postgresql://%s:%s/%s", 
                                   DB_HOST, DB_PORT, DB_NAME);
        
        // Connect using credentials from .env
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }
}
```

**What happens**:
1. `EnvLoader` reads `.env` file when class loads
2. `DBConnection` gets values using `EnvLoader.get()`
3. If `.env` doesn't exist, uses default values
4. Builds database connection string
5. Connects to database

---

## 🚀 Setup Instructions

### Step 1: Create .env File

**Location**: Project root directory (same level as `pom.xml`)

```
hospital_management_system/
├── .env          ← Create this file here
├── pom.xml
├── src/
└── ...
```

### Step 2: Add Your Database Credentials

Create `.env` file with your PostgreSQL settings:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=postgres
DB_PASSWORD=your_actual_password
```

**Important**: Replace `your_actual_password` with your real PostgreSQL password!

### Step 3: Verify .env is in .gitignore

Check that `.env` is listed in `.gitignore`:

```gitignore
.env
```

**Why?**: Prevents committing sensitive passwords to Git

### Step 4: Test Connection

Run your application - it should automatically read from `.env`:

```bash
mvn javafx:run
```

---

## 🔍 How to Find .env File Location

### EnvLoader Searches in This Order:

1. **System Environment Variables**
   - Windows: `set DB_HOST=localhost`
   - Linux/Mac: `export DB_HOST=localhost`

2. **Custom Path** (if `ENV_PATH` is set)
   ```bash
   export ENV_PATH=/path/to/.env
   ```

3. **Current Directory** (project root)
   ```
   hospital_management_system/.env
   ```

4. **Parent Directories** (searches up 6 levels)
   ```
   ../.env
   ../../.env
   etc.
   ```

5. **Classpath** (from resources folder)
   ```
   src/main/resources/.env
   src/main/resources/hospital/hospital_management_system/.env
   ```

---

## 📊 Environment Variables Used

### Database Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL server address |
| `DB_PORT` | `5432` | PostgreSQL port number |
| `DB_NAME` | `hospital_db` | Database name |
| `DB_USER` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `DB_URL` | (none) | Full JDBC URL (overrides host/port/name) |
| `DATABASE_URL` | (none) | Alternative name for DB_URL |

### Example Configurations

#### Local Development
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=postgres
DB_PASSWORD=postgres
```

#### Remote Database
```env
DB_HOST=192.168.1.100
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=admin
DB_PASSWORD=secure_password_123
```

#### Using Full URL
```env
DB_URL=jdbc:postgresql://localhost:5432/hospital_db
DB_USER=postgres
DB_PASSWORD=postgres
```

---

## 🛡️ Security Best Practices

### ✅ DO:

1. **Keep .env in .gitignore**
   ```gitignore
   .env
   *.env
   ```

2. **Use strong passwords**
   ```env
   DB_PASSWORD=StrongP@ssw0rd!123
   ```

3. **Don't commit .env to Git**
   - Check: `git status` should NOT show `.env`

4. **Create .env.example** (template without real values)
   ```env
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=hospital_db
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

### ❌ DON'T:

1. **Don't hardcode passwords in code**
   ```java
   // BAD!
   String password = "mypassword123";
   ```

2. **Don't commit .env to version control**
   - Never `git add .env`

3. **Don't share .env files**
   - Each developer should have their own

4. **Don't use default passwords in production**
   - Change from `postgres/postgres`

---

## 🐛 Troubleshooting

### Issue 1: "Database connection failed"

**Problem**: `.env` file not found or wrong credentials

**Solution**:
1. Check `.env` file exists in project root
2. Verify credentials are correct
3. Check file format (no spaces around `=`)
4. Restart application

### Issue 2: "Using default DB credentials" warning

**Problem**: `.env` not being read, using defaults

**Solution**:
```java
// Check if .env is being loaded
System.out.println("DB_HOST: " + EnvLoader.get("DB_HOST", "NOT_FOUND"));
```

### Issue 3: .env file not found

**Problem**: File in wrong location

**Solution**: Place `.env` in project root:
```
hospital_management_system/
├── .env          ← Here!
├── pom.xml
└── src/
```

### Issue 4: Values not updating

**Problem**: Cached values or file not reloaded

**Solution**: 
- Restart application
- Check file encoding (should be UTF-8)
- Verify no syntax errors in `.env`

---

## 📝 Example: Complete Setup

### 1. Create .env File

**Location**: `hospital_management_system/.env`

**Content**:
```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_db
DB_USER=postgres
DB_PASSWORD=mypassword
```

### 2. Verify .gitignore

**File**: `.gitignore`

**Should contain**:
```
.env
```

### 3. Test Connection

**Run application**:
```bash
mvn javafx:run
```

**Expected**: Application connects to database using credentials from `.env`

---

## 🔄 How Values Are Resolved

### Priority Order (highest to lowest):

1. **System Environment Variables**
   ```bash
   export DB_HOST=production-server
   ```

2. **ENV_PATH** (if set)
   ```bash
   export ENV_PATH=/custom/path/.env
   ```

3. **Current Directory `.env`**
   ```
   ./hospital_management_system/.env
   ```

4. **Parent Directories `.env`**
   ```
   ../.env
   ../../.env
   ```

5. **Classpath `.env`**
   ```
   src/main/resources/.env
   ```

6. **Default Values** (in code)
   ```java
   EnvLoader.get("DB_HOST", "localhost") // "localhost" is default
   ```

---

## 💡 Advanced Usage

### Using System Environment Variables

**Windows**:
```cmd
set DB_HOST=localhost
set DB_PASSWORD=mypassword
```

**Linux/Mac**:
```bash
export DB_HOST=localhost
export DB_PASSWORD=mypassword
```

### Using Custom ENV_PATH

```bash
export ENV_PATH=/path/to/my/.env
java -jar app.jar
```

### Multiple .env Files

EnvLoader will load from multiple locations, with later files overriding earlier ones.

---

## 📚 Summary

### Key Points

1. **`.env` file** stores sensitive configuration (database credentials)
2. **`EnvLoader.java`** reads `.env` file automatically
3. **`DBConnection.java`** uses values from `.env` to connect
4. **Default values** used if `.env` doesn't exist
5. **Security**: `.env` is in `.gitignore` (not committed)

### Quick Reference

| Task | Action |
|------|--------|
| **Create .env** | Create file in project root |
| **Add credentials** | `DB_HOST=localhost` format |
| **Test** | Run application |
| **Verify** | Check `.env` in `.gitignore` |

### File Structure

```
hospital_management_system/
├── .env                    ← Your database credentials
├── .gitignore              ← Contains .env (not committed)
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           └── hospital/
│               └── hospital_management_system/
│                   └── utils/
│                       ├── EnvLoader.java    ← Reads .env
│                       └── DBConnection.java ← Uses .env
└── ...
```

---

## 🎓 Interview Answer

**Q: How do you handle configuration in your project?**

**A**: 
> "I use a `.env` file to store sensitive configuration like database credentials. I created an `EnvLoader` utility class that automatically reads from the `.env` file when the application starts. The `DBConnection` class uses `EnvLoader` to get database credentials, with fallback to default values if the `.env` file doesn't exist. This approach keeps sensitive data out of the codebase and allows different configurations for development and production environments. The `.env` file is in `.gitignore` to prevent committing passwords to version control."

---

**That's how .env works in this project!** 🔐
