package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import java.sql.*;

@SpringBootApplication
@RestController
public class Buyer_post {

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

        String url = "jdbc:mysql://localhost:3306/educational";
        String user = "Hello";
        String password = "12345678";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_name", "goffman", "izrael1976")) {
            if (!isTableExists(connection, "Buyer")) {
                createBuyerTable(connection);
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

    public static void main(String[] args) {
        SpringApplication.run(Buyer_post.class, args);
    }

    private void createBuyerTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE Buyer (id INT AUTO_INCREMENT, name VARCHAR(255), gender VARCHAR(1), age INT, PRIMARY KEY (id))";
            statement.executeUpdate(createTableSQL);
        }
    }

    private boolean isTableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet tables = meta.getTables(null, null, tableName, null);
        return tables.next();
    }
}
