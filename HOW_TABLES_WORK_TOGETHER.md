# How Tables Work Together - Simple Explanation

## 🔗 Table Relationships

Your database tables are connected through **Foreign Keys**. Here's how:

### 1. **Patient ↔ Appointment ↔ Doctor**
```
Patient Table          Appointment Table       Doctor Table
┌──────────┐          ┌──────────────┐        ┌──────────┐
│ patient_id│◄─────────│ patient_id   │        │ doctor_id│
│ name      │          │ doctor_id    │────────►│ name     │
│ dob       │          │ date         │        │ specialty│
└──────────┘          │ status       │        └──────────┘
                      └──────────────┘
```

**What this means:**
- Each appointment belongs to ONE patient
- Each appointment has ONE doctor
- One patient can have MANY appointments
- One doctor can have MANY appointments

### 2. **Appointment → Prescription**
```
Appointment Table      Prescription Table
┌──────────────┐      ┌──────────────────┐
│ appointment_id│◄─────│ appointment_id   │
│ patient_id    │      │ prescription_id  │
│ doctor_id     │      │ notes            │
└──────────────┘      │ date_issued      │
                      └──────────────────┘
```

**What this means:**
- Each prescription is for ONE appointment
- One appointment can have MANY prescriptions

### 3. **Patient → Feedback**
```
Patient Table          PatientFeedback Table
┌──────────┐          ┌──────────────┐
│ patient_id│◄─────────│ patient_id   │
│ name      │          │ rating       │
│ contact   │          │ comments     │
└──────────┘          └──────────────┘
```

**What this means:**
- Each feedback is from ONE patient
- One patient can give MANY feedbacks

---

## 🎯 Real-World Example

### Scenario: John Doe visits the hospital

1. **Patient Table** - John Doe is registered
   ```
   patient_id: 1
   name: John Doe
   dob: 1990-01-01
   ```

2. **Appointment Table** - John books appointment with Dr. Smith
   ```
   appointment_id: 5
   patient_id: 1 (John Doe)
   doctor_id: 3 (Dr. Smith)
   date: 2024-01-15
   status: Scheduled
   ```

3. **Prescription Table** - After appointment, Dr. Smith prescribes medicine
   ```
   prescription_id: 10
   appointment_id: 5 (John's appointment)
   notes: "Take 2 tablets daily"
   ```

4. **Feedback Table** - John gives feedback
   ```
   feedback_id: 20
   patient_id: 1 (John Doe)
   rating: 5
   comments: "Great service!"
   ```

---

## 💡 How to See This in Action

### In Your Application:

1. **Go to Reports Tab** (new button added!)
2. **Select a patient** from dropdown
3. **See all their appointments** automatically!

This shows:
- Patient info (from Patient table)
- All appointments (from Appointment table)
- Doctor names (from Doctor table via foreign key)
- Status of each appointment

---

## 🔧 How It Works in Code

### Example: Get Patient's Appointments

```java
// 1. Get patient
Patient patient = patientService.getPatientById(1L);

// 2. Get all appointments
List<Appointment> allAppointments = appointmentService.getAll();

// 3. Filter appointments for this patient
List<Appointment> patientAppointments = allAppointments.stream()
    .filter(app -> app.getPatientId().equals(patient.getPatientId()))
    .toList();

// 4. Each appointment already has doctor info!
for (Appointment app : patientAppointments) {
    System.out.println("Doctor: " + app.getDoctor().getLastName());
}
```

### Why This Works:
- When you load an appointment, the DAO automatically loads the related doctor
- The foreign key `doctor_id` in appointments table links to `doctor_id` in doctors table
- Your DAO does a JOIN query behind the scenes!

---

## 📊 Database Query Example

When you get appointments, this SQL runs:

```sql
SELECT 
    a.appointment_id,
    a.appointment_date,
    a.status,
    p.patient_id,
    p.first_name,
    p.last_name,
    d.doctor_id,
    d.first_name AS doctor_first_name,
    d.last_name AS doctor_last_name
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN doctors d ON a.doctor_id = d.doctor_id
WHERE p.patient_id = 1;
```

This **joins** three tables together!

---

## 🎓 Key Concepts

### Foreign Key
- A column that references another table's primary key
- Example: `patient_id` in appointments table references `patient_id` in patients table

### One-to-Many Relationship
- One patient → Many appointments
- One doctor → Many appointments
- One appointment → Many prescriptions

### Join
- Combining data from multiple tables
- Your DAOs do this automatically!

---

## 🚀 Try It Yourself

1. **Add a patient** (Patients tab)
2. **Add a doctor** (Doctors tab)
3. **Create appointment** (Appointments tab) - select the patient and doctor
4. **Go to Reports tab** - select the patient
5. **See the appointment** with doctor name automatically shown!

This proves the tables are working together! 🎉

---

## 📝 Summary

**Tables work together through:**
- ✅ Foreign keys (patient_id, doctor_id, etc.)
- ✅ Relationships (one-to-many)
- ✅ JOIN queries in DAOs
- ✅ Object references in models (Patient has appointments, Appointment has doctor)

**You can see this in:**
- ✅ Appointment page (select patient and doctor from dropdowns)
- ✅ Prescription page (select appointment from dropdown)
- ✅ Reports page (see patient's full appointment history)

**This is called a RELATIONAL DATABASE!** 🔗
