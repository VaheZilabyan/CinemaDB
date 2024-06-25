import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class openHallWindow extends JFrame {
    JTextField name_textField = new JTextField(20);
    JTextField capacity_textField = new JTextField(20);
    openHallWindow() {
        setTitle("New Hall");
        setSize(new Dimension(320, 130));
        setLocationRelativeTo(null);

        JPanel gridPanel = new JPanel(new GridLayout(2,2));

        JButton add_Button = new JButton("Add Hall");
        JLabel name = new JLabel("Name: ");
        JLabel capacity = new JLabel("Capacity: ");

        gridPanel.add(name);
        gridPanel.add(name_textField);
        gridPanel.add(capacity);
        gridPanel.add(capacity_textField);

        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(add_Button, BorderLayout.SOUTH);

        add_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Checker.existsInDatabase("halls", "name", name_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "input hall's name exsists in database!",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidNameOrSurname(name_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Wrong input or empty text!",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidHallCapacity(capacity_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect capacity size!",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    addData();
                    JOptionPane.showMessageDialog(null,
                            "Successfully added",
                            "Added",  // Title
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                }
            }
        });
    }
    private void addData() {
        Connection connection = DBConnection.getConnection();
        try {
            String sql = "INSERT INTO halls (name, capacity) " +
                    "VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name_textField.getText());
            statement.setInt(2, Integer.parseInt(capacity_textField.getText()));

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new Hall was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
