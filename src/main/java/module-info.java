module com.example.smartherd {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client;
    requires com.google.api.services.gmail;
    requires org.apache.commons.codec;
    requires mail;
    requires com.google.auth.oauth2;
    requires com.google.api.client.extensions.jetty.auth;
    requires jdk.httpserver;
    requires activation;


    exports com.example.smartherd;
    exports com.example.smartherd.controllers;
    exports com.example.smartherd.classes.dashboard;
    exports com.example.smartherd.classes.database;
    opens com.example.smartherd to javafx.base, javafx.fxml;
    opens com.example.smartherd.classes.dashboard to javafx.base;
    opens com.example.smartherd.controllers to javafx.base, javafx.fxml;
}