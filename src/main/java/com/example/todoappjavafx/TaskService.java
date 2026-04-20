package com.example.todoappjavafx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final DatabaseConnector conncetor;

    public TaskService() {
        this.conncetor = new DatabaseConnector();
    }

    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "is_done BOOLEAN DEFAULT FALSE" +
                    ");";
        try (Connection conn = conncetor.getConnection();
            Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Table 'tasks' is ready");
        } catch (SQLException e) {
            System.err.println("Error in creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addTask(String title) {
        String sql = "INSERT INTO tasks (title) VALUES (?)";

        try(Connection conn = conncetor.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.executeUpdate();
            System.out.println("Task '" + title + "' saved in the cloud!");
        } catch(SQLException e) {
            System.err.println("Error add task: " + e.getMessage());
        }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try(Connection conn = conncetor.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while(rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getBoolean("is_done")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void updateTaskStatus(int id, boolean isDone) {
        String sql = "UPDATE tasks SET is_done = ? WHERE id = ?";

        try(Connection conn = conncetor.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isDone);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Status " + id + " updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try(Connection conn = conncetor.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("task " + id + " deleted");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCompletedTasks() {
        String sql = "DELETE FROM tasks WHERE is_done = true";

        try(Connection conn = conncetor.getConnection();
        Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
