import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class movieWindow extends JFrame {
    JTextField name_textField = new JTextField(20);
    JTextField year_textField = new JTextField(20);
    JTextField rating_textField = new JTextField(20);
    JTextField length_textField = new JTextField(20);
    JTextField director_textField = new JTextField(20);
    movieWindow() {
        setTitle("New Movie");
        setSize(new Dimension(320, 240));
        setLocationRelativeTo(null);

        JButton add_Button = new JButton("Add Movie");
        JLabel name = new JLabel("Name: ");
        JLabel release_year = new JLabel("Release Year: ");
        JLabel rating = new JLabel("Rating: ");
        JLabel length = new JLabel("Length: ");
        JLabel director = new JLabel("Director: ");

        JPanel gridPanel = new JPanel(new GridLayout(5,2));
        gridPanel.add(name);
        gridPanel.add(name_textField);
        gridPanel.add(release_year);
        gridPanel.add(year_textField);
        gridPanel.add(rating);
        gridPanel.add(rating_textField);
        gridPanel.add(length);
        gridPanel.add(length_textField);
        gridPanel.add(director);
        gridPanel.add(director_textField);

        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(add_Button, BorderLayout.SOUTH);

        add_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Integer.parseInt(year_textField.getText()) < 1800
                        || Integer.parseInt(year_textField.getText()) > 2040
                        || year_textField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Wrong release year input!",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidMovieRate(rating_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect movie rating",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidMovieLength(length_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect movie length",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isValidNameAndSurname(director_textField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect name and surname",
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
            String sql = "INSERT INTO movies (title, release_year, rating, length, director) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name_textField.getText());
            statement.setString(2, year_textField.getText());
            statement.setDouble(3, Double.parseDouble(rating_textField.getText()));
            statement.setInt(4, Integer.parseInt(length_textField.getText()));
            statement.setString(5, director_textField.getText());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new movie was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
