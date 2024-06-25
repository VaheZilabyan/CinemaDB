import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class clientWindow extends JFrame {
    JTextField name_textField = new JTextField(20);
    JTextField surname_textField = new JTextField(20);
    JTextField age_textField = new JTextField(20);
    JTextField phone_textField = new JTextField("099999999",20);
    clientWindow() {
        setTitle("New Client");
        setSize(new Dimension(320, 200));
        setLocationRelativeTo(null);

        JButton add_Button = new JButton("Add new client");
        JLabel name = new JLabel("Name: ");
        JLabel surname = new JLabel("Surname: ");
        JLabel age = new JLabel("Age: ");
        JLabel phone = new JLabel("Phone: ");

        phone_textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                phone_textField.setText("");
            }
            public void focusLost(FocusEvent e) {}
        });

        JPanel gridPanel = new JPanel(new GridLayout(4,2));
        gridPanel.add(name);
        gridPanel.add(name_textField);
        gridPanel.add(surname);
        gridPanel.add(surname_textField);
        gridPanel.add(age);
        gridPanel.add(age_textField);
        gridPanel.add(phone);
        gridPanel.add(phone_textField);

        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(add_Button, BorderLayout.SOUTH);

        add_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Checker.isValidNameOrSurname(name_textField.getText())
                        || !Checker.isValidNameOrSurname(surname_textField.getText()) ) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect name or surname",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidAge(age_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect age",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidPhone(phone_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect phone number",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    addData();
                    JOptionPane.showMessageDialog(getContentPane(),
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
            String sql = "INSERT INTO clients (name, surname, age, phone_number) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name_textField.getText());
            statement.setString(2, surname_textField.getText());
            statement.setInt(3, Integer.parseInt(age_textField.getText()));
            statement.setString(4, phone_textField.getText());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new client was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
