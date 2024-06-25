import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class movieAndGenres extends JFrame {
    JTextField movie_id = new JTextField(20);
    JTextField genre_id = new JTextField(20);
    movieAndGenres() {
        setTitle("New Genre");
        setSize(new Dimension(320, 140));
        setLocationRelativeTo(null);

        JPanel gridPanel = new JPanel(new GridLayout(2,2));

        JButton add_Button = new JButton("Add");
        JLabel jmovie = new JLabel("Movie id: ");
        JLabel jgenre = new JLabel("Genre id: ");

        gridPanel.add(jmovie);
        gridPanel.add(movie_id);
        gridPanel.add(jgenre);
        gridPanel.add(genre_id);

        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(add_Button, BorderLayout.SOUTH);

        add_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Checker.isValidID(movie_id.getText()) || !Checker.isValidID(genre_id.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Wrong input ID!",
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
                                "Movie_Genre",  // Title
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });
    }
    private boolean addData() {
        Connection connection = DBConnection.getConnection();
        try {
            String sql = "INSERT INTO movie_genre (movie_id, genre_id) " +
                    "VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(movie_id.getText()));
            statement.setInt(2, Integer.parseInt(genre_id.getText()));

            int rowsInserted;
            try {
                rowsInserted = statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }

            //int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new movie_Genre was inserted successfully!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
