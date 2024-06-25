import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;

public class openSessionWindow extends JFrame {
    JTextField hall_id_textField = new JTextField(20);
    JTextField movie_id_textField = new JTextField(20);
    JTextField date_textField = new JTextField("yyyy-MM-dd",20);
    JTextField hour_textField = new JTextField("HH:mm:ss",20);
    openSessionWindow() {
        setTitle("New Session");
        setSize(new Dimension(320, 200));
        setLocationRelativeTo(null);

        JPanel gridPanel = new JPanel(new GridLayout(4,2));

        date_textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                date_textField.setText("");
            }
            public void focusLost(FocusEvent e) {}
        });
        hour_textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                hour_textField.setText("");
            }
            public void focusLost(FocusEvent e) {}
        });

        JButton add_Button = new JButton("Add New Session");
        JLabel hall_id = new JLabel("Hall_id: ");
        JLabel movie_id = new JLabel("Movie_id: ");
        JLabel date = new JLabel("Date: ");
        JLabel hour = new JLabel("Hour: ");

        gridPanel.add(hall_id);
        gridPanel.add(hall_id_textField);
        gridPanel.add(movie_id);
        gridPanel.add(movie_id_textField);
        gridPanel.add(date);
        gridPanel.add(date_textField);
        gridPanel.add(hour);
        gridPanel.add(hour_textField);

        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(add_Button, BorderLayout.SOUTH);

        add_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Checker.isValidID(hall_id_textField.getText())
                    && !Checker.isValidID(movie_id_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect hall or movie ID",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidDate(date_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect DATE",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidTime(hour_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect TIME",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    boolean isAdded = addData();
                    if (isAdded) {
                        JOptionPane.showMessageDialog(null,
                                "Successfully added",
                                "Added",  // Title
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(getContentPane(),
                                "Wrong input foreign key ID",
                                "Validation Error",  // Title
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });
    }
    private boolean addData() {
        int generatedId = -1;
        Connection connection = DBConnection.getConnection();
        try {
            Date date = Date.valueOf(date_textField.getText());
            Time time = Time.valueOf(hour_textField.getText());

            String capacitySql = "SELECT capacity FROM halls WHERE hall_id = ?";
            PreparedStatement capacityStatement = connection.prepareStatement(capacitySql);
            capacityStatement.setInt(1, Integer.parseInt(hall_id_textField.getText()));
            ResultSet capacityResult = capacityStatement.executeQuery();
            int capacity = 0;
            if (capacityResult.next()) {
                capacity = capacityResult.getInt("capacity");
            }

            String sql = "INSERT INTO sessions (hall_id, movie_id, date, hour, tickets_count) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, Integer.parseInt(hall_id_textField.getText()));
            statement.setInt(2, Integer.parseInt(movie_id_textField.getText()));
            statement.setDate(3, date);
            statement.setTime(4, time);
            statement.setInt(5, capacity);

            int rowsInserted;
            try {
                rowsInserted = statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }
            if (rowsInserted > 0) {
                System.out.println("A new Session was inserted successfully!");
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1); // Retrieve the auto-generated ID
                    System.out.println("Generated ID: " + generatedId);
                    createTickets(generatedId, capacity);
                } else {
                    System.out.println("No ID generated");
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void createTickets(int session_id, int capacity) {
        Connection connection = DBConnection.getConnection();
        try {
            Date date = Date.valueOf(date_textField.getText());
            Time time = Time.valueOf(hour_textField.getText());

            for (int i = 1; i <= capacity; ++i) {
                String sql = "INSERT INTO tickets (session_id, movie_id, hall_id, date, hour, place) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, session_id);
                statement.setInt(2, Integer.parseInt(movie_id_textField.getText()));
                statement.setInt(3, Integer.parseInt(hall_id_textField.getText()));
                statement.setDate(4, date);
                statement.setTime(5, time);
                statement.setInt(6, i);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new ticket was inserted successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteSessionRow(int id) {
        String del_tickets = "DELETE FROM tickets WHERE session_id = ?";
        String del_sessions = "DELETE FROM sessions WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedSt = conn.prepareStatement(del_tickets);
             PreparedStatement preparedStatement = conn.prepareStatement(del_sessions)) {

            // Delete tickets associated with the session
            preparedSt.setInt(1, id);
            int ticketRowsAffected = preparedSt.executeUpdate();

            // Delete the session
            preparedStatement.setInt(1, id);
            int sessionRowsAffected = preparedStatement.executeUpdate();

            System.out.println("Tickets deleted: " + ticketRowsAffected);
            System.out.println("Sessions deleted: " + sessionRowsAffected);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteAllSessions() {
        String deleteAllSessionsSQL = "DELETE FROM sessions";
        String updateTicketsCountSQL = "UPDATE sessions SET tickets_count = 0";
        String checkZeroCountSQL = "SELECT session_id FROM sessions WHERE tickets_count = 0";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteAllSessionsSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateTicketsCountSQL);
             PreparedStatement checkStatement = connection.prepareStatement(checkZeroCountSQL)) {

            // Delete all sessions
            deleteStatement.executeUpdate();

            // Update tickets_count in sessions
            updateStatement.executeUpdate();

            // Check if tickets_count becomes zero
            ResultSet rs = checkStatement.executeQuery();
            while (rs.next()) {
                int session_id = rs.getInt("session_id");
                deleteSessionIfZeroCount(connection, session_id);
            }

            System.out.println("All sessions deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteTicket(int ticket_id) {
        String deleteTicketSQL = "DELETE FROM tickets WHERE ticket_id = ?";
        String updateSessionSQL = "UPDATE sessions SET tickets_count = tickets_count - 1 WHERE session_id = (SELECT session_id FROM tickets WHERE ticket_id = ?)";
        String checkZeroCountSQL = "SELECT session_id FROM sessions WHERE tickets_count = 0";

        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement deleteStatement = connection.prepareStatement(deleteTicketSQL);
            PreparedStatement updateStatement = connection.prepareStatement(updateSessionSQL);
            PreparedStatement checkStatement = connection.prepareStatement(checkZeroCountSQL);

            // Delete ticket
            deleteStatement.setInt(1, ticket_id);
            deleteStatement.executeUpdate();

            // Update tickets_count in sessions
            updateStatement.setInt(1, ticket_id);
            updateStatement.executeUpdate();

            // Check if tickets_count becomes zero
            ResultSet rs = checkStatement.executeQuery();
            while (rs.next()) {
                int session_id = rs.getInt("session_id");
                deleteSessionIfZeroCount(connection, session_id);
            }

            System.out.println("Ticket deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllTickets() {
        String deleteAllTicketsSQL = "TRUNCATE TABLE tickets";
        String updateSessionSQL = "UPDATE sessions SET tickets_count = 0";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteAllTicketsSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateSessionSQL)) {
            // Delete all tickets
            deleteStatement.executeUpdate();
            // Update tickets_count in sessions
            updateStatement.executeUpdate();
            System.out.println("All tickets deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void deleteSessionIfZeroCount(Connection connection, int session_id) throws SQLException {
        String deleteSessionSQL = "DELETE FROM sessions WHERE session_id = ?";
        PreparedStatement deleteStatement = connection.prepareStatement(deleteSessionSQL);
        deleteStatement.setInt(1, session_id);
        deleteStatement.executeUpdate();
        System.out.println("Session with ID " + session_id + " deleted due to zero ticket count.");
    }
}
