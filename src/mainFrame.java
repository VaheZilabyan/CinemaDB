import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class mainFrame extends JFrame {
    DefaultTableModel model;
    private String tableName;
    JPanel main_panel = new JPanel(new BorderLayout());
    JButton addNewButton = new JButton("Add new");
    JLabel idLabel = new JLabel("ID");
    public mainFrame() {
        setTitle("DB Cinema Server");
        setSize(new Dimension(700, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTable table = new JTable();
        model = new DefaultTableModel();
        table.setModel(model);
        JScrollPane scrollPane = new JScrollPane(table);

        DBConnection db = new DBConnection();
        ArrayList<String> tableMetaData = db.getTables();

        JComboBox<String> comboBox = new JComboBox<>();
        for (int i = 0; i < tableMetaData.size(); ++i) {
            comboBox.insertItemAt(tableMetaData.get(i), i);
        }
        comboBox.setSelectedIndex(0);

        JTextField textField = new JTextField(5);
        JButton deleteButton = new JButton("Delete");
        JPanel panel = new JPanel();
        panel.add(idLabel);
        panel.add(textField);
        panel.add(deleteButton, BorderLayout.SOUTH);

        JPanel northPanel = new JPanel(new GridLayout(2,1));
        northPanel.add(comboBox);
        northPanel.add(addNewButton);

        main_panel.add(panel, BorderLayout.CENTER);
        main_panel.add(northPanel, BorderLayout.NORTH);

        tableName = (String) comboBox.getItemAt(0);
        db.printTable(model, tableName);
        updatePanel(main_panel, tableName);

        JPanel southPanel = new JPanel(new GridLayout(1,3));
        JButton deleteAllData = new JButton("Delete All Data");
        JButton modifyButton = new JButton("Modify");
        JButton updateButton = new JButton("Update Table");
        JButton searchButton = new JButton("Search");
        southPanel.add(modifyButton);
        southPanel.add(searchButton);
        southPanel.add(deleteAllData);
        southPanel.add(updateButton);

        getContentPane().add(main_panel, BorderLayout.WEST);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableName = (String) comboBox.getSelectedItem();

                if (tableName.equals("movie_genre") || tableName.equals("tickets")) {
                    modifyButton.setVisible(false);
                } else {
                    modifyButton.setVisible(true);
                }

                db.printTable(model, tableName);
                updatePanel(main_panel, tableName);
            }
        });



        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableName = (String) comboBox.getSelectedItem();
                db.printTable(model, tableName);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //int id = Integer.parseInt(textField.getText());
                String key_name = DBConnection.getPrimaryKeyColumnName(tableName);
                tableName = (String) comboBox.getSelectedItem();
                if (!Checker.isNumber(textField.getText()) || textField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Input correct ID",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!Checker.checkIDExists(tableName, key_name, Integer.parseInt(textField.getText()))) {
                    JOptionPane.showMessageDialog(null,
                            "Wrong ID",
                            "Validation error",  // Title
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    int id = Integer.parseInt(textField.getText());
                    if (tableName.equals("sessions")) {
                        openSessionWindow.deleteSessionRow(id);
                    } else if (tableName.equals("tickets")) {
                        openSessionWindow.deleteTicket(id);
                    } else {
                        DBConnection.deleteRow(tableName, id);
                    }
                    JOptionPane.showMessageDialog(null,
                            "Deleted row with id = " + id,
                            "Deleted!",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    textField.setText("");
                    DBConnection.printTable(model, tableName);
                }
            }
        });

        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch(tableName) {
                    case "movies":
                        openMovieWindow();
                        break;
                    case "halls":
                        openHallWindow();
                        break;
                    case "sessions":
                        openSessionWindow();
                        break;
                    case "genres":
                        openGenreWindow();
                        break;
                    case "clients":
                        openClientWindow();
                        break;
                    case "movie_genre":
                        addGenresForMovie();
                        break;
                    default:
                        // code block
                }
                DBConnection.printTable(model, tableName);
                System.out.println("Update");
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openModifyWindow(tableName);
                DBConnection.printTable(model, tableName);
            }
        });

        deleteAllData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null,
                        "Аre you sure?",
                        "Delete All Data",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (option == JOptionPane.YES_OPTION) {
                    if (tableName.equals("sessions")) {
                        openSessionWindow.deleteAllSessions();
                    } else if (tableName.equals("tickets")) {
                        openSessionWindow.deleteAllTickets();
                    } else {
                        DBConnection.deleteAllData(tableName);
                    }
                    DBConnection.printTable(model, tableName);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSearchWindow();
            }
        });
    }
    private void openSearchWindow() {
        searchWindow sw = new searchWindow(model, tableName);
        sw.setVisible(true);
    }

    private void updatePanel(JPanel panel, String tableName) {
        addNewButton.setVisible(true);
        switch(tableName) {
            case "movies":
                addNewButton.setText("Add new Movie");
                idLabel.setText("Movie ID");
                break;
            case "halls":
                addNewButton.setText("Add new Hall");
                idLabel.setText("Hall ID");
                break;
            case "sessions":
                addNewButton.setText("Add new Session");
                idLabel.setText("Session ID");
                break;
            case "genres":
                addNewButton.setText("Add new Genre");
                idLabel.setText("Genre ID");
                break;
            case "clients":
                addNewButton.setText("Add new Client");
                idLabel.setText("Client ID");
                break;
            case "tickets":
                addNewButton.setVisible(false);
                idLabel.setText("Tickets ID");
                break;
            case "movie_genre":
                addNewButton.setText("Add new");
                idLabel.setText("Add");
            default:
                addNewButton.setText("Add new");
                idLabel.setText("Add");
                break;
        }
    }

    private void addGenresForMovie() {
        movieAndGenres mg = new movieAndGenres();
        mg.setVisible(true);
    }

    private void openMovieWindow() {
        movieWindow mw = new movieWindow();
        mw.setVisible(true);
    }
    private void openHallWindow() {
        openHallWindow hw = new openHallWindow();
        hw.setVisible(true);
    }
    private void openSessionWindow() {
        openSessionWindow sw = new openSessionWindow();
        sw.setVisible(true);
    }
    private void openGenreWindow() {
        genreWindow gw = new genreWindow();
        gw.setVisible(true);
    }
    private void openClientWindow() {
        clientWindow cw = new clientWindow();
        cw.setVisible(true);
    }
    private void openModifyWindow(String tableName) {
        modifyWindow mw = new modifyWindow(tableName);
        mw.setVisible(true);
    }
}