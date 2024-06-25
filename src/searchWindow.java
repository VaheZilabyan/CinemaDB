import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import java.util.ArrayList;

public class searchWindow extends JFrame {
    private JComboBox<String> columnComboBox;
    private JTextField conditionTextField;
    private JTextField valueTextField;
    private JTextField limitTextField;
    private DefaultTableModel model;
    private String tableName;

    public searchWindow(DefaultTableModel model, String tableName) {
        setTitle("Search");
        setSize(new Dimension(360, 200));
        setLocationRelativeTo(null);
        this.model = model;
        this.tableName = tableName;

        JLabel columnLabel = new JLabel("Select Column:");
        ArrayList<String> tableMetaData = DBConnection.getTableMetaData(tableName);
        String currId = tableMetaData.get(0);
        tableMetaData.remove(0);

        columnComboBox = new JComboBox<>();
        for (int i = 0; i < tableMetaData.size(); ++i) {
            columnComboBox.insertItemAt(tableMetaData.get(i), i);
        }

        JLabel conditionLabel = new JLabel("Enter Condition:");
        conditionTextField = new JTextField("=, !=, >, <, >=, <=, LIKE",10);

        JLabel valueLabel = new JLabel("Enter Value:");
        valueTextField = new JTextField(10);

        JLabel limitLabel = new JLabel("Enter Limit:");
        limitTextField = new JTextField(10);

        conditionTextField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                conditionTextField.setText("");
            }
            public void focusLost(FocusEvent e) {}
        });

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Checker.isCorrectCondition(conditionTextField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Input correct condition",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.isNumber(limitTextField.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "Input Number for LIMIT",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    search();
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(columnLabel);
        panel.add(columnComboBox);
        panel.add(conditionLabel);
        panel.add(conditionTextField);
        panel.add(valueLabel);
        panel.add(valueTextField);
        panel.add(limitLabel);
        panel.add(limitTextField);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(searchButton, BorderLayout.SOUTH);
    }

    private void search() {
        String column = (String) columnComboBox.getSelectedItem();
        String condition = conditionTextField.getText();
        String value = valueTextField.getText();
        String limit = limitTextField.getText();

        String searchSQL = "SELECT * FROM " + tableName + " WHERE " + column + " " + condition + " ?";
        if (!limit.isEmpty()) {
            searchSQL += " LIMIT " + limit;
        }

        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(searchSQL);
            statement.setString(1, value);
            ResultSet resultSet = statement.executeQuery();

            model.setColumnCount(0);
            model.setRowCount(0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names to model
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Add data rows to model
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}