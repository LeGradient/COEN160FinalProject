package edu.scu.coen160.finalproject.system;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMain {
    public static void main(String[] args) {
        // dynamically load the SQLITE driver class
        // apparently nothing happens if this executes twice
        // but that doesn't mean we should do it
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        // print the working directory
        try {
            System.out.println(new File(".").getCanonicalPath());
        } catch (IOException e) {
            System.out.println(e);
        }

        // verify that a SQLITE database exists in the expected directory
        File db = new File("TransactionRecords.db");
        assert(db.exists());
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:TransactionRecords.db")) {
            // do nothing
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
