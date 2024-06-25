import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checker {
    public static boolean isValidTime(String input) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setLenient(false);

        try {
            timeFormat.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public static boolean isValidDate(String input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public static boolean isValidID(String input) {
        try {
            int id = Integer.parseInt(input);
            return id > 0;
        } catch (NumberFormatException e1) {
            return false;
        }
    }
    public static boolean isValidHallCapacity(String input) {
        try {
            int capacity = Integer.parseInt(input);
            if (capacity <= 0 || capacity > 200) return false;
            return true;
        } catch (NumberFormatException e1) {
            return false;
        }
    }
    public static boolean isValidMovieRate(String input) {
        try {
            int rating = Integer.parseInt(input);
            if (rating < 0 || rating > 10) return false;
            return true;
        } catch (NumberFormatException e1) {
            try {
                double rating = Double.parseDouble(input);
                if (rating < 0 || rating > 10) return false;
                return true;
            } catch (NumberFormatException e2) {
                return false;
            }
        }
    }
    public static boolean isCorrectCondition(String text) {
        String regex = "(=|!=|>|<|>=|<=|LIKE)";
        return text.matches(regex);
    }
    public static boolean isValidMovieLength(String text) {
        try {
            int length = Integer.parseInt(text);
            if (length <= 10 || length > 250) return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isNumber(String text) {
        try {
            if (text.isEmpty()) return true;
            int t = Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isValidPhone(String text) {
        String regex = "0(94|93|77|98|91|99|96|43|33|97|55|41|44|66|50)\\d{6}";
        return text.matches(regex);
    }
    public static boolean isValidAge(String text) {
        try {
            int age = Integer.parseInt(text);
            if (age <= 0 || age > 120) return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isTextWithoutNumbers(String text) {
        String regex = "^[^\\d]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
    public static boolean isValidNameOrSurname(String name) {
        String regex = "^[A-Z][a-zA-Z]*$";
        return name.matches(regex);
    }
    public static boolean isValidNameAndSurname(String name) {
        String regex = "^[A-Z][a-z]+\s[A-Z][a-z]+";
        return name.matches(regex);
    }
    public static boolean existsInDatabase(String tableName, String genreName, String text) {
        Connection connection = DBConnection.getConnection();
        try {
            String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + genreName + " = ?";
            System.out.println(sql);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, text);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            // Genre name already exists in the database
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean checkIDExists(String tableName, String key_name, int id) {
        boolean exists = false;
        String sql = "SELECT * FROM " + tableName + " WHERE " + key_name + " = ?";
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                exists = resultSet.next(); // If resultSet.next() returns true, the ID exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
}
