# Git Branching Setup Guide

## Step 1: Initialize Repository (if not done)
```bash
cd "C:\Users\Amalitech\OneDrive\Desktop\Amalitec\HospitalHealthcare Management System\hospital_management_system"
git init
```

## Step 2: Create Main Branch with Base Code
```bash
# Add .gitignore first
git add .gitignore .env.example
git commit -m "Add gitignore and env example"

# Add base structure (database, models, utils)
git add database/ src/main/java/hospital/hospital_management_system/model/
git add src/main/java/hospital/hospital_management_system/utils/
git add pom.xml module-info.java
git commit -m "Add base project structure and database schema"

# Push to GitHub
git remote add origin <your-github-repo-url>
git branch -M main
git push -u origin main
```

## Step 3: Create Develop Branch
```bash
git checkout -b develop
git push -u origin develop
```

## Step 4: Create Feature Branches

### Feature 1: Patient Management
```bash
git checkout develop
git checkout -b feature/patient-management

# Add patient-related files
git add src/main/java/hospital/hospital_management_system/model/Patient.java
git add src/main/java/hospital/hospital_management_system/dao/PatientDAO.java
git add src/main/java/hospital/hospital_management_system/services/PatientService.java
git add src/main/java/hospital/hospital_management_system/controller/PatientController.java
git add src/main/resources/hospital/hospital_management_system/PatientView.fxml
git commit -m "Add patient management module with CRUD operations"
git push -u origin feature/patient-management
```

### Feature 2: Doctor Management
```bash
git checkout develop
git checkout -b feature/doctor-management

# Add doctor-related files
git add src/main/java/hospital/hospital_management_system/model/Doctor.java
git add src/main/java/hospital/hospital_management_system/model/Department.java
git add src/main/java/hospital/hospital_management_system/dao/DoctorDAO.java
git add src/main/java/hospital/hospital_management_system/dao/DepartmentDAO.java
git add src/main/java/hospital/hospital_management_system/services/DoctorService.java
git add src/main/java/hospital/hospital_management_system/services/DepartmentService.java
git add src/main/java/hospital/hospital_management_system/controller/DoctorController.java
git add src/main/java/hospital/hospital_management_system/controller/DepartmentController.java
git add src/main/resources/hospital/hospital_management_system/DoctorView.fxml
git add src/main/resources/hospital/hospital_management_system/DepartmentView.fxml
git commit -m "Add doctor and department management modules"
git push -u origin feature/doctor-management
```

### Feature 3: Appointment System
```bash
git checkout develop
git checkout -b feature/appointment-system

# Add appointment-related files
git add src/main/java/hospital/hospital_management_system/model/Appointment.java
git add src/main/java/hospital/hospital_management_system/dao/AppointmentDAO.java
git add src/main/java/hospital/hospital_management_system/services/AppointmentService.java
git add src/main/java/hospital/hospital_management_system/controller/AppointmentController.java
git add src/main/resources/hospital/hospital_management_system/Appointment-view.fxml
git commit -m "Add appointment scheduling system with search functionality"
git push -u origin feature/appointment-system
```

### Feature 4: Prescription Management
```bash
git checkout develop
git checkout -b feature/prescription-management

# Add prescription-related files
git add src/main/java/hospital/hospital_management_system/model/Prescriptions.java
git add src/main/java/hospital/hospital_management_system/model/PrescriptionItems.java
git add src/main/java/hospital/hospital_management_system/dao/PrescriptionDAO.java
git add src/main/java/hospital/hospital_management_system/dao/PrescriptionItemDAO.java
git add src/main/java/hospital/hospital_management_system/services/PrescriptionService.java
git add src/main/java/hospital/hospital_management_system/services/PrescriptionItemService.java
git add src/main/java/hospital/hospital_management_system/controller/PrescriptionController.java
git add src/main/java/hospital/hospital_management_system/controller/PrescriptionItemController.java
git add src/main/resources/hospital/hospital_management_system/PrescriptionView.fxml
git add src/main/resources/hospital/hospital_management_system/PrescriptionItemView.fxml
git commit -m "Add prescription management with inventory integration"
git push -u origin feature/prescription-management
```

### Feature 5: Inventory Management
```bash
git checkout develop
git checkout -b feature/inventory-management

# Add inventory-related files
git add src/main/java/hospital/hospital_management_system/model/MedicalInventory.java
git add src/main/java/hospital/hospital_management_system/dao/MedicalInventoryDAO.java
git add src/main/java/hospital/hospital_management_system/services/MedicalInventoryService.java
git add src/main/java/hospital/hospital_management_system/controller/MedicalInventoryController.java
git add src/main/resources/hospital/hospital_management_system/MedicalInventoryView.fxml
git commit -m "Add medical inventory management with stock tracking"
git push -u origin feature/inventory-management
```

### Feature 6: Reports
```bash
git checkout develop
git checkout -b feature/reports

# Add reports-related files
git add src/main/java/hospital/hospital_management_system/model/PatientFeedback.java
git add src/main/java/hospital/hospital_management_system/dao/PatientFeedbackDAO.java
git add src/main/java/hospital/hospital_management_system/services/PatientFeedbackService.java
git add src/main/java/hospital/hospital_management_system/controller/PatientFeedbackController.java
git add src/main/java/hospital/hospital_management_system/controller/ReportsController.java
git add src/main/resources/hospital/hospital_management_system/PatientFeedbackView.fxml
git add src/main/resources/hospital/hospital_management_system/ReportsView.fxml
git commit -m "Add reports and patient feedback modules"
git push -u origin feature/reports
```

### Feature 7: UI Improvements
```bash
git checkout develop
git checkout -b feature/ui-improvements

# Add UI-related files
git add src/main/java/hospital/hospital_management_system/MainApp.java
git add src/main/java/hospital/hospital_management_system/controller/MainController.java
git add src/main/resources/hospital/hospital_management_system/MainView.fxml
git add src/main/resources/hospital/hospital_management_system/modern-styles.css
git commit -m "Add modern UI with indigo theme and responsive design"
git push -u origin feature/ui-improvements
```

## Step 5: Merge Features to Develop
```bash
# Merge each feature one by one
git checkout develop
git merge feature/patient-management
git merge feature/doctor-management
git merge feature/appointment-system
git merge feature/prescription-management
git merge feature/inventory-management
git merge feature/reports
git merge feature/ui-improvements

# Push develop
git push origin develop
```

## Step 6: Merge Develop to Main (Production)
```bash
git checkout main
git merge develop
git push origin main
```

## Quick Reference Commands
```bash
# Check current branch
git branch

# Switch branch
git checkout <branch-name>

# See all branches
git branch -a

# Delete local branch
git branch -d <branch-name>

# Delete remote branch
git push origin --delete <branch-name>
```
