import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class modifyWindow extends JFrame {
    String tableName;
    String columnName;
    JSpinner id_forModify;
    JTextField newValue;
    modifyWindow(String tableName) {
        setTitle("Modify");
        setSize(new Dimension(320, 160));
        setLocationRelativeTo(null);
        this.tableName = tableName;

        DBConnection db = new DBConnection();
        ArrayList<String> tableMetaData = db.getTableMetaData(tableName);
        String currId = tableMetaData.get(0);
        tableMetaData.remove(0);

        JComboBox<String> comboBox = new JComboBox<>();
        for (int i = 0; i < tableMetaData.size(); ++i) {
            comboBox.insertItemAt(tableMetaData.get(i), i);
        }

        JLabel selectLabel = new JLabel("Select Column");
        JLabel newValueLabel = new JLabel("New Value: ");
        JLabel input_id = new JLabel("Input ID: ");

        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        id_forModify = new JSpinner(spinnerModel);
        newValue = new JTextField(20);
        JButton modifyButton = new JButton("Modify");

        JPanel panel = new JPanel(new GridLayout(3,2));
        panel.add(selectLabel);
        panel.add(comboBox);
        panel.add(input_id);
        panel.add(id_forModify);
        panel.add(newValueLabel);
        panel.add(newValue);

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected item
                columnName = (String) comboBox.getSelectedItem();
                System.out.println(columnName);
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isUpdated = updateData(columnName, currId);
                if (columnName.equals("tickets_count")) {
                    JOptionPane.showMessageDialog(getContentPane(),
                            "You can't change tickets count",
                            "Modified",  // Title
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else if (isUpdated) {
                    if (tableName.equals("sessions")) {
                        modifyTableTickets(columnName, newValue.getText(), currId);
                    }
                    JOptionPane.showMessageDialog(getContentPane(),
                            "Table updated successfully!",
                            "Modified",  // Title
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(getContentPane(),
                            "Wrong input ID or value",
                            "Modified",  // Title
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        getContentPane().add(modifyButton, BorderLayout.SOUTH);
        getContentPane().add(panel, BorderLayout.CENTER);
    }
    private boolean updateData(String columnName, String currId) {
        Connection connection = DBConnection.getConnection();
        try {
            // Assuming `newValue` and `id_forModify` are JTextFields or similar components
            String sql = "UPDATE " + tableName
                    + " SET " + columnName + " = ?"
                    + " WHERE " + currId + " = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newValue.getText());
            statement.setString(2, id_forModify.getValue().toString());

            int rowsUpdated;
            try {
                rowsUpdated = statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }

            if (rowsUpdated > 0) {
                System.out.println("Table updated successfully!");
                return true;
            } else {
                System.out.println("No rows were updated.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    private boolean modifyTableTickets(String columnName, String newValue, String currId) {
        Connection connection = DBConnection.getConnection();
        try {
            // Assuming `newValue` and `id_forModify` are JTextFields or similar components
            String sql = "UPDATE tickets"
                    + " SET " + columnName + " = ?"
                    + " WHERE " + currId + " = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newValue);
            statement.setString(2, id_forModify.getValue().toString());

            int rowsUpdated;
            try {
                rowsUpdated = statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }

            if (rowsUpdated > 0) {
                System.out.println("Table Tickets updated successfully!");
                return true;
            } else {
                System.out.println("No rows were updated in Tickets.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
