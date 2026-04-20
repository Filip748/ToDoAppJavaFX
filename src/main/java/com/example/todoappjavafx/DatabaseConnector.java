package com.example.todoappjavafx;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

    public DatabaseConnector() {
        loadProperties();
    }

    private void loadProperties() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")){
            props.load(in);
            this.host = props.getProperty("db.host");
            this.port = props.getProperty("db.port");
            this.database = props.getProperty("db.databes");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");
        } catch (IOException e) {
            System.out.println("Błąd: Nie znaleziono pliku db.properties!");
            e.printStackTrace();
        }
    }

    public String getHost() { return host; }
    public String getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }

    public Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%s/%s?sslMode=REQUIRED",
                getHost(), getPort(), getDatabase());
        return DriverManager.getConnection(url, getUser(), getPassword());
    }
}
