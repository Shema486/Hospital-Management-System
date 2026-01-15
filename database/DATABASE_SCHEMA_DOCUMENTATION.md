# Database Schema Documentation
## Hospital Management System

---

## Table of Contents

1. [Database Overview](#database-overview)
2. [Normalization](#normalization)
3. [Entity Relationship Diagram](#entity-relationship-diagram)
4. [Table Definitions](#table-definitions)
5. [Relationships and Foreign Keys](#relationships-and-foreign-keys)
6. [Indexes](#indexes)
7. [Constraints and Business Rules](#constraints-and-business-rules)
8. [Data Dictionary](#data-dictionary)
9. [Sample Queries](#sample-queries)

---

## Database Overview

**Database System**: PostgreSQL  
**Database Name**: `hospital_db`  
**Normalization Level**: Third Normal Form (3NF)  
**Total Tables**: 8  
**Total Indexes**: 6 (plus primary keys)

### Entities
- Departments
- Doctors
- Patients
- Appointments
- Medical Inventory
- Prescriptions
- Prescription Items
- Patient Feedback

---

## Normalization

### Third Normal Form (3NF)

The database is normalized to **3NF**, which means:
- ✅ **1NF**: All attributes are atomic (no repeating groups)
- ✅ **2NF**: All non-key attributes are fully dependent on the primary key
- ✅ **3NF**: No transitive dependencies (non-key attributes don't depend on other non-key attributes)

### Example of Normalization
- **Appointments** table stores only appointment-specific data
- Patient information is stored in **Patients** table (not duplicated)
- Doctor information is stored in **Doctors** table (not duplicated)
- Relationships are maintained through foreign keys

---

## Entity Relationship Diagram

```
departments (1) ────────< (many) doctors
    │                           │
    │                           │
    └───────────────────────────┘
                               │
                               │
patients (1) ──────< (many) appointments (many) >────── (1) doctors
    │                   │
    │                   │
    │                   └───────< (1) prescriptions (many) >────── (1) appointments
    │                                                   │
    │                                                   │
    │                                        (many) prescription_items (many)
    │                                                   │
    │                                                   │
    └───────< (many) patient_feedback                  │
                                                       │
                                    medical_inventory (1)
```

---

## Table Definitions

### 1. departments

**Purpose**: Stores hospital department information

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| dept_id | SERIAL | PRIMARY KEY | Unique department identifier (auto-increment) |
| dept_name | VARCHAR(100) | NOT NULL, UNIQUE | Department name (e.g., "Cardiology") |
| location_floor | INT | CHECK (>= 0) | Floor number where department is located |

**Sample Data**:
```
dept_id | dept_name    | location_floor
--------|--------------|---------------
1       | Cardiology   | 2
2       | Neurology    | 3
3       | Pediatrics   | 1
```

---

### 2. doctors

**Purpose**: Stores doctor information and their department assignment

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| doctor_id | SERIAL | PRIMARY KEY | Unique doctor identifier |
| first_name | VARCHAR(50) | NOT NULL | Doctor's first name |
| last_name | VARCHAR(50) | NOT NULL | Doctor's last name |
| specialization | VARCHAR(100) | NOT NULL | Medical specialization |
| dept_id | INT | FOREIGN KEY | Department ID (can be NULL) |
| email | VARCHAR(100) | UNIQUE | Email address (optional, unique) |
| phone | VARCHAR(15) | - | Phone number (optional) |

**Foreign Keys**:
- `dept_id` → `departments(dept_id)` ON DELETE SET NULL

**Sample Data**:
```
doctor_id | first_name | last_name | specialization | dept_id | email
----------|------------|-----------|----------------|---------|------------------
1         | John       | Smith     | Cardiologist   | 1       | john.s@hosp.com
2         | Sarah      | Johnson   | Neurologist    | 2       | sarah.j@hosp.com
```

---

### 3. patients

**Purpose**: Stores patient demographic and contact information

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| patient_id | SERIAL | PRIMARY KEY | Unique patient identifier |
| first_name | VARCHAR(50) | NOT NULL | Patient's first name |
| last_name | VARCHAR(50) | NOT NULL | Patient's last name |
| dob | DATE | NOT NULL | Date of birth |
| gender | VARCHAR(10) | NOT NULL, CHECK | Gender: 'Male', 'Female', or 'Other' |
| contact_number | VARCHAR(15) | NOT NULL | Contact phone number |
| address | TEXT | - | Full address (optional) |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |

**Sample Data**:
```
patient_id | first_name | last_name | dob        | gender | contact_number
-----------|------------|-----------|------------|--------|---------------
1          | Alice      | Brown     | 1990-05-15 | Female | 555-1001
2          | Bob        | Davis     | 1985-08-22 | Male   | 555-1002
```

---

### 4. appointments

**Purpose**: Links patients with doctors for scheduled appointments

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| appointment_id | SERIAL | PRIMARY KEY | Unique appointment identifier |
| patient_id | INT | NOT NULL, FOREIGN KEY | Patient ID |
| doctor_id | INT | NOT NULL, FOREIGN KEY | Doctor ID |
| appointment_date | TIMESTAMP | NOT NULL | Scheduled date and time |
| status | VARCHAR(15) | DEFAULT 'Scheduled', CHECK | Status: 'Scheduled', 'Completed', or 'Cancelled' |
| reason | TEXT | - | Reason for appointment (optional) |

**Foreign Keys**:
- `patient_id` → `patients(patient_id)` ON DELETE CASCADE
- `doctor_id` → `doctors(doctor_id)` ON DELETE CASCADE

**Sample Data**:
```
appointment_id | patient_id | doctor_id | appointment_date      | status    | reason
---------------|------------|-----------|----------------------|-----------|------------
1              | 1          | 1         | 2024-01-15 10:00:00  | Scheduled | Checkup
2              | 2          | 2         | 2024-01-16 14:30:00  | Completed | Consultation
```

---

### 5. medical_inventory

**Purpose**: Stores medical supplies and medications inventory

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| item_id | SERIAL | PRIMARY KEY | Unique inventory item identifier |
| item_name | VARCHAR(100) | NOT NULL | Name of the item/medication |
| stock_quantity | INT | NOT NULL, DEFAULT 0, CHECK (>= 0) | Current stock quantity |
| unit_price | NUMERIC(10,2) | NOT NULL | Price per unit |

**Sample Data**:
```
item_id | item_name      | stock_quantity | unit_price
--------|----------------|----------------|------------
1       | Aspirin 100mg  | 500            | 5.50
2       | Bandages       | 1000           | 2.00
3       | Antiseptic     | 250            | 8.75
```

---

### 6. prescriptions

**Purpose**: Stores prescription information linked to appointments

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| prescription_id | SERIAL | PRIMARY KEY | Unique prescription identifier |
| appointment_id | INT | NOT NULL, UNIQUE, FOREIGN KEY | Appointment ID (one-to-one) |
| date_issued | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Prescription issue date |
| notes | TEXT | - | Additional notes (optional) |

**Foreign Keys**:
- `appointment_id` → `appointments(appointment_id)` UNIQUE

**Sample Data**:
```
prescription_id | appointment_id | date_issued           | notes
----------------|----------------|----------------------|------------
1               | 1              | 2024-01-15 10:30:00  | Take with food
2               | 2              | 2024-01-16 15:00:00  | Complete course
```

---

### 7. prescription_items

**Purpose**: Links prescriptions with inventory items (many-to-many relationship)

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| prescription_id | INT | NOT NULL, FOREIGN KEY, PRIMARY KEY | Prescription ID |
| item_id | INT | NOT NULL, FOREIGN KEY, PRIMARY KEY | Inventory item ID |
| dosage_instruction | VARCHAR(255) | - | Dosage instructions (optional) |
| quantity_dispensed | INT | NOT NULL | Quantity of item dispensed |

**Primary Key**: Composite (`prescription_id`, `item_id`)  
**Foreign Keys**:
- `prescription_id` → `prescriptions(prescription_id)` ON DELETE CASCADE
- `item_id` → `medical_inventory(item_id)` ON DELETE RESTRICT

**Sample Data**:
```
prescription_id | item_id | dosage_instruction      | quantity_dispensed
----------------|---------|------------------------|-------------------
1               | 1       | 1 tablet twice daily   | 30
1               | 2       | Apply as needed        | 5
2               | 1       | 2 tablets daily        | 60
```

---

### 8. patient_feedback

**Purpose**: Stores patient feedback and ratings

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| feedback_id | SERIAL | PRIMARY KEY | Unique feedback identifier |
| patient_id | INT | NOT NULL, FOREIGN KEY | Patient ID |
| rating | INT | CHECK (1-5) | Rating from 1 to 5 |
| comments | TEXT | - | Feedback comments (optional) |
| feedback_date | DATE | DEFAULT CURRENT_DATE | Date of feedback |

**Foreign Keys**:
- `patient_id` → `patients(patient_id)`

**Sample Data**:
```
feedback_id | patient_id | rating | comments              | feedback_date
------------|------------|--------|----------------------|---------------
1           | 1          | 5      | Excellent service    | 2024-01-15
2           | 2          | 4      | Very good care       | 2024-01-16
```

---

## Relationships and Foreign Keys

### Relationship Summary

| From Table | To Table | Relationship Type | Foreign Key | On Delete Action |
|------------|----------|-------------------|-------------|------------------|
| doctors | departments | Many-to-One | dept_id | SET NULL |
| appointments | patients | Many-to-One | patient_id | CASCADE |
| appointments | doctors | Many-to-One | doctor_id | CASCADE |
| prescriptions | appointments | One-to-One | appointment_id | - |
| prescription_items | prescriptions | Many-to-One | prescription_id | CASCADE |
| prescription_items | medical_inventory | Many-to-One | item_id | RESTRICT |
| patient_feedback | patients | Many-to-One | patient_id | - |

### Foreign Key Details

1. **doctors.dept_id → departments.dept_id**
   - **Action**: SET NULL
   - **Meaning**: If a department is deleted, doctors in that department will have NULL dept_id

2. **appointments.patient_id → patients.patient_id**
   - **Action**: CASCADE
   - **Meaning**: If a patient is deleted, all their appointments are deleted

3. **appointments.doctor_id → doctors.doctor_id**
   - **Action**: CASCADE
   - **Meaning**: If a doctor is deleted, all their appointments are deleted

4. **prescriptions.appointment_id → appointments.appointment_id**
   - **Action**: Default (RESTRICT)
   - **Meaning**: One appointment can have only one prescription (UNIQUE constraint)

5. **prescription_items.prescription_id → prescriptions.prescription_id**
   - **Action**: CASCADE
   - **Meaning**: If a prescription is deleted, all its items are deleted

6. **prescription_items.item_id → medical_inventory.item_id**
   - **Action**: RESTRICT
   - **Meaning**: Cannot delete inventory item if it's used in any prescription

---

## Indexes

### Primary Indexes (Automatic)
- All PRIMARY KEY columns automatically have indexes

### Secondary Indexes (Performance Optimization)

| Index Name | Table | Column(s) | Purpose |
|------------|-------|-----------|---------|
| idx_patient_lastname | patients | last_name | Speed up patient search by last name |
| idx_appointment_date | appointments | appointment_date | Speed up appointment queries by date |
| idx_doctor_dept | doctors | dept_id | Speed up doctor lookups by department |
| idx_doctor_specialization | doctors | specialization | Speed up doctor search by specialization |
| idx_appointment_patient_id | appointments | patient_id | Speed up patient appointment queries |
| idx_appointment_doctor_id | appointments | doctor_id | Speed up doctor appointment queries |
| idx_appointment_status | appointments | status | Speed up status filtering |

### Performance Impact
- **Without Index**: O(n) - Linear scan
- **With Index**: O(log n) - Binary search (much faster for large datasets)

---

## Constraints and Business Rules

### Check Constraints

1. **departments.location_floor >= 0**
   - Floor numbers cannot be negative

2. **patients.gender IN ('Male', 'Female', 'Other')**
   - Only valid gender values allowed

3. **appointments.status IN ('Scheduled', 'Completed', 'Cancelled')**
   - Only valid status values allowed

4. **medical_inventory.stock_quantity >= 0**
   - Stock cannot be negative

5. **patient_feedback.rating BETWEEN 1 AND 5**
   - Rating must be between 1 and 5

### Unique Constraints

1. **departments.dept_name** - UNIQUE
   - Department names must be unique

2. **doctors.email** - UNIQUE
   - Doctor emails must be unique

3. **prescriptions.appointment_id** - UNIQUE
   - One appointment can have only one prescription

### NOT NULL Constraints

- Critical fields marked as NOT NULL to ensure data integrity
- Examples: patient names, appointment dates, doctor names

---

## Data Dictionary

### Data Types Used

| Data Type | Description | Example |
|-----------|-------------|---------|
| SERIAL | Auto-incrementing integer (1, 2, 3...) | Primary keys |
| VARCHAR(n) | Variable-length string (max n characters) | Names, emails |
| TEXT | Unlimited length string | Addresses, notes |
| INT | Integer number | IDs, quantities, floors |
| DATE | Date (YYYY-MM-DD) | Birth dates |
| TIMESTAMP | Date and time | Appointment dates |
| NUMERIC(10,2) | Decimal number (10 digits, 2 decimal places) | Prices |

### Naming Conventions

- **Table names**: Lowercase, plural (e.g., `patients`, `doctors`)
- **Column names**: Lowercase with underscores (e.g., `first_name`, `patient_id`)
- **Primary keys**: `table_name_id` (e.g., `patient_id`, `doctor_id`)
- **Foreign keys**: Same as referenced primary key (e.g., `patient_id`, `dept_id`)
- **Index names**: `idx_table_column` (e.g., `idx_patient_lastname`)

---

## Sample Queries

### 1. Find all appointments for a patient
```sql
SELECT a.appointment_id, a.appointment_date, a.status, a.reason,
       d.first_name || ' ' || d.last_name AS doctor_name,
       d.specialization
FROM appointments a
JOIN doctors d ON a.doctor_id = d.doctor_id
WHERE a.patient_id = 1
ORDER BY a.appointment_date DESC;
```

### 2. Find all doctors in a specific department
```sql
SELECT d.doctor_id, d.first_name || ' ' || d.last_name AS doctor_name,
       d.specialization, dept.dept_name
FROM doctors d
JOIN departments dept ON d.dept_id = dept.dept_id
WHERE dept.dept_name = 'Cardiology';
```

### 3. Get prescription details with items
```sql
SELECT p.prescription_id, p.date_issued, p.notes,
       mi.item_name, pi.quantity_dispensed, pi.dosage_instruction,
       mi.unit_price, (pi.quantity_dispensed * mi.unit_price) AS total_price
FROM prescriptions p
JOIN prescription_items pi ON p.prescription_id = pi.prescription_id
JOIN medical_inventory mi ON pi.item_id = mi.item_id
WHERE p.prescription_id = 1;
```

### 4. Search patients by last name (using index)
```sql
SELECT patient_id, first_name, last_name, contact_number
FROM patients
WHERE last_name ILIKE '%Smith%'
ORDER BY last_name, first_name;
```

### 5. Get appointment statistics
```sql
SELECT status, COUNT(*) AS count
FROM appointments
GROUP BY status;
```

### 6. Find low stock inventory items
```sql
SELECT item_id, item_name, stock_quantity, unit_price
FROM medical_inventory
WHERE stock_quantity < 50
ORDER BY stock_quantity ASC;
```

### 7. Get patient feedback summary
```sql
SELECT p.first_name || ' ' || p.last_name AS patient_name,
       pf.rating, pf.comments, pf.feedback_date
FROM patient_feedback pf
JOIN patients p ON pf.patient_id = p.patient_id
ORDER BY pf.feedback_date DESC;
```

---

## Database Setup Instructions

1. **Create Database**:
   ```sql
   CREATE DATABASE hospital_db;
   ```

2. **Connect to Database**:
   ```bash
   psql -U postgres -d hospital_db
   ```

3. **Run Schema Script**:
   ```bash
   psql -U postgres -d hospital_db -f schema.sql
   ```

4. **Verify Tables**:
   ```sql
   \dt  -- List all tables
   \d+ patients  -- Describe patients table
   ```

---

## Maintenance Notes

- **Backup**: Regular backups recommended
- **Index Maintenance**: PostgreSQL automatically maintains indexes
- **Vacuum**: Run VACUUM ANALYZE periodically for optimal performance
- **Monitoring**: Monitor query performance using EXPLAIN ANALYZE

---

**Last Updated**: 2024  
**Version**: 1.0  
**Database**: PostgreSQL
