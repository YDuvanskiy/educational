package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
@SpringBootApplication
@RestController
public class Buyer_post {
    String url = "jdbc:mysql://localhost:3306/educational";
    String user = "Student";
    String password = "12345678";
    @PostMapping("/buyer")
    public ResponseEntity<String> createBuyer(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        if (!jsonObject.has("name") || jsonObject.getString("name").isEmpty()) {
            return new ResponseEntity<>("не указано имя", HttpStatus.BAD_REQUEST);
        }
        if (!jsonObject.has("age")) {
            return new ResponseEntity<>("не указан возраст", HttpStatus.BAD_REQUEST);
        }
        String name = jsonObject.getString("name");
        String gender = jsonObject.optString("gender", null);
        int age = jsonObject.getInt("age");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "Buyer", null);
            if (!tables.next()) {
                Statement statement = connection.createStatement();
                String createTableSQL = "CREATE TABLE Buyer (id INT AUTO_INCREMENT, name VARCHAR(255), gender VARCHAR(1), age INT, PRIMARY KEY (id))";
                statement.executeUpdate(createTableSQL);
            }
            String insertSQL = "INSERT INTO Buyer (name, gender, age) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, name);
                if (gender != null) {
                    preparedStatement.setString(2, gender);
                } else {
                    preparedStatement.setNull(2, Types.VARCHAR);
                }
                preparedStatement.setInt(3, age);
                preparedStatement.executeUpdate();
            }
            return new ResponseEntity<>("201 OK", HttpStatus.CREATED);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/product")
    public ResponseEntity<String> createProduct(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);

        if (!jsonObject.has("item") || jsonObject.getString("item").isEmpty()) {
            return new ResponseEntity<>("Не указано название товара", HttpStatus.BAD_REQUEST);
        }
        if (!jsonObject.has("price")) {
            return new ResponseEntity<>("Не указана цена товара", HttpStatus.BAD_REQUEST);
        }
        if (!jsonObject.has("quantity")) {
            return new ResponseEntity<>("Не указана цена товара", HttpStatus.BAD_REQUEST);
        }
        String item = jsonObject.getString("item");
        int price = jsonObject.getInt("price");
        int quantity = jsonObject.getInt("quantity");
        String url = "jdbc:mysql://localhost:3306/educational";
        String user = "Student";
        String password = "12345678";
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "Product", null);
            if (!tables.next()) {
                Statement statement = connection.createStatement();
                String createTableSQL = "CREATE TABLE Product (id INT AUTO_INCREMENT, item VARCHAR(255) , price INT, quantity INT, PRIMARY KEY (id))";
                statement.executeUpdate(createTableSQL);
            }
            String sql = "SELECT COUNT(*) AS count FROM Product WHERE item = ?";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, item);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count > 0) {
                        return new ResponseEntity<>("Данное наименование уже есть на складе", HttpStatus.BAD_REQUEST);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String insertSQL = "INSERT INTO Product (item, price, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, item);
                preparedStatement.setInt(2, price);
                preparedStatement.setInt(3, quantity);
                preparedStatement.executeUpdate();
            }
            return new ResponseEntity<>("201 OK", HttpStatus.CREATED);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public class BuyerController {
        @Autowired
        private JdbcTemplate jdbcTemplate;
        @GetMapping("/buyer")
        public ResponseEntity<Object> getBuyerData(@RequestParam Long id) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String sql = "SELECT * FROM Buyer WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setLong(1, id);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        Map<String, Object> buyerData = new HashMap<>();
                        buyerData.put("id", resultSet.getLong("id"));
                        buyerData.put("name", resultSet.getString("name"));
                        buyerData.put("gender", resultSet.getString("gender"));
                        buyerData.put("age", resultSet.getInt("age"));
                        return new ResponseEntity<>(buyerData, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("No buyer found with ID: " + id, HttpStatus.NOT_FOUND);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error occurred while fetching buyer data", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(Buyer_post.class, args);
    }
}