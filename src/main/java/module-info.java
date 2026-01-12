module hospital.hospital_management_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;      // needed for JDBC
    requires java.base;     // usually implicit

    exports hospital.hospital_management_system;
    exports hospital.hospital_management_system.model;
    exports hospital.hospital_management_system.dao;
    exports hospital.hospital_management_system.utils;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.postgresql.jdbc;
    requires hospital.hospital_management_system;


    opens hospital.hospital_management_system to javafx.fxml;
    opens hospital.hospital_management_system.utils to javafx.fxml;
    exports hospital.hospital_management_system.controller;
    opens hospital.hospital_management_system.controller to javafx.fxml;
    //exports hospital.hospital_management_system.launcher;
    opens hospital.hospital_management_system.launcher to javafx.fxml;
}
