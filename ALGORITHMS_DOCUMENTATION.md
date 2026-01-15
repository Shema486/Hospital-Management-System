# Algorithms and Data Structures Documentation

This document explains the algorithms, data structures, and optimization techniques used in the Hospital Management System.

## Table of Contents

1. [Caching (Hash Maps)](#caching-hash-maps)
2. [Searching Algorithms](#searching-algorithms)
3. [Sorting Algorithms](#sorting-algorithms)
4. [Database Indexing](#database-indexing)
5. [Performance Optimization](#performance-optimization)

---

## 1. Caching (Hash Maps)

### What is Caching?
Caching stores frequently accessed data in memory (RAM) so it can be retrieved quickly without accessing the slower database.

### Implementation in This Project

**Location**: Service classes (`PatientService.java`, `DoctorService.java`, `DepartmentService.java`)

**Data Structure Used**: `HashMap<Long, Object>`

**How It Works**:
```java
// In PatientService.java
private final Map<Long, Patient> patientCache = new HashMap<>();

// When getting a patient:
public Patient getPatientById(long patientId) {
    // 1. Check cache first (fast - O(1) lookup)
    if (patientCache.containsKey(patientId)) {
        return patientCache.get(patientId);  // Return from cache
    }
    
    // 2. If not in cache, get from database (slower)
    Patient patient = patientDAO.searchPatientById(patientId);
    
    // 3. Store in cache for next time
    if (patient != null) {
        patientCache.put(patientId, patient);
    }
    
    return patient;
}
```

### Benefits
- **Speed**: Cache lookups are extremely fast (O(1) - constant time)
- **Reduced Database Load**: Fewer database queries
- **Better User Experience**: Faster response times

### When Cache is Updated
- When data is added: New data is added to cache
- When data is updated: Cache is updated with new data
- When data is deleted: Data is removed from cache
- Manual clearing: `clearCache()` method can clear the cache

---

## 2. Searching Algorithms

### Database-Level Search

**Method**: SQL queries with pattern matching

**Example**: Search patients by last name
```sql
SELECT * FROM patients WHERE last_name ILIKE '%Smith%'
```

**Implementation**:
- Uses `ILIKE` for case-insensitive search
- `%` wildcard allows partial matches
- Executed directly in PostgreSQL (very efficient)

**Location**: DAO classes (e.g., `PatientDAO.searchPatientByLastName()`)

### In-Memory Search

**Method**: Java Streams API for filtering

**Example**: Search cached patients
```java
public List<Patient> searchPatientByLastNameUsingStreams(String lastName) {
    return patientCache.values().stream()
        .filter(patient -> patient.getLastName()
            .toLowerCase().contains(lastName.toLowerCase()))
        .collect(Collectors.toList());
}
```

**How It Works**:
1. Get all values from the cache (HashMap)
2. Convert to a Stream
3. Filter items that match the search criteria
4. Collect results into a List

**When to Use**:
- Database search: When data might not be in cache
- In-memory search: When data is already cached (faster)

---

## 3. Sorting Algorithms

### Implementation

**Method**: `Collections.sort()` with custom `Comparator`

**Algorithm Used**: Java uses TimSort (a hybrid stable sorting algorithm)

**Example**: Sort patients by last name
```java
public List<Patient> sortPatientsByLastName(List<Patient> patients) {
    List<Patient> sortedList = new ArrayList<>(patients);
    
    Collections.sort(sortedList, new Comparator<Patient>() {
        @Override
        public int compare(Patient p1, Patient p2) {
            // Compare last names alphabetically
            return p1.getLastName().compareToIgnoreCase(p2.getLastName());
        }
    });
    
    return sortedList;
}
```

### How Comparators Work

The `compare()` method returns:
- **Negative number**: First object comes before second
- **Zero**: Objects are equal
- **Positive number**: First object comes after second

### Available Sorting Methods

**PatientService**:
- `sortPatientsByLastName()` - Sort by last name (A-Z)
- `sortPatientsById()` - Sort by ID (1, 2, 3...)
- `sortPatientsByFirstName()` - Sort by first name (A-Z)

**DoctorService**:
- `sortDoctorsByLastName()` - Sort by last name
- `sortDoctorsById()` - Sort by ID
- `sortDoctorsBySpecialization()` - Sort by specialization

### Time Complexity
- **Best Case**: O(n log n)
- **Average Case**: O(n log n)
- **Worst Case**: O(n log n)

---

## 4. Database Indexing

### What are Indexes?

Indexes are data structures that improve the speed of data retrieval operations on database tables. Think of them like an index in a book - they help you find information quickly.

### How Indexes Work

**Without Index**:
- Database scans entire table row by row
- Time complexity: O(n) - linear search
- Slow for large tables

**With Index**:
- Database uses index to find data directly
- Time complexity: O(log n) - binary search-like
- Much faster for large tables

### Indexes in This Project

**Recommended Indexes** (should be created in database):

```sql
-- Index on patient last name (for searching)
CREATE INDEX idx_patients_last_name ON patients(last_name);

-- Index on doctor specialization (for filtering)
CREATE INDEX idx_doctors_specialization ON doctors(specialization);

-- Index on appointment date (for date queries)
CREATE INDEX idx_appointments_date ON appointments(appointment_date);

-- Index on foreign keys (for joins)
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments(doctor_id);
```

### Trade-offs

**Benefits**:
- ✅ Faster SELECT queries
- ✅ Faster JOIN operations
- ✅ Better performance on large datasets

**Costs**:
- ❌ Slightly slower INSERT operations
- ❌ Uses additional disk space
- ❌ Needs to be maintained

---

## 5. Performance Optimization

### Performance Measurement

**Utility Class**: `PerformanceMeasurer.java`

**Purpose**: Measure how long operations take to execute

**Usage Example**:
```java
// Measure database query time
long time1 = PerformanceMeasurer.measureTime("Database Query", () -> {
    List<Patient> patients = patientService.getAllPatients();
});

// Measure cached query time
long time2 = PerformanceMeasurer.measureTime("Cached Query", () -> {
    Patient patient = patientService.getPatientById(1L);
});

// Compare the results
System.out.println("Database: " + time1 + "ms, Cache: " + time2 + "ms");
```

### Optimization Strategies Used

1. **Caching**: Store frequently accessed data in memory
2. **Indexing**: Create indexes on frequently searched columns
3. **Parameterized Queries**: Prevent SQL injection and allow query optimization
4. **Connection Pooling**: Reuse database connections (handled by JDBC)

### Performance Comparison

**Typical Performance Improvements**:

| Operation | Without Optimization | With Optimization | Improvement |
|-----------|---------------------|-------------------|-------------|
| Get Patient by ID (DB) | ~50-100ms | ~50-100ms | - |
| Get Patient by ID (Cache) | - | ~1-5ms | **20x faster** |
| Search by Name (no index) | ~100-200ms | - | - |
| Search by Name (with index) | - | ~10-20ms | **10x faster** |

*Note: Actual times vary based on hardware, database size, and system load.*

---

## Summary

This project demonstrates several important computer science concepts:

1. **Data Structures**: HashMap for caching
2. **Algorithms**: Sorting (TimSort), Searching (Linear/Binary)
3. **Database Optimization**: Indexing
4. **Performance Measurement**: Timing utilities

All implementations use simple, beginner-friendly code that is easy to understand and explain.
