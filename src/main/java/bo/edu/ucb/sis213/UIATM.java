package bo.edu.ucb.sis213;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UIATM extends JFrame implements ActionListener {
    private JTextField pinField;
    private JLabel balanceLabel;
    private JTextField depositField;
    private JTextField withdrawField;
    private JTextField newPinField;
    private JTextField confirmPinField;

    private static int usuarioId;
    private static double saldo;
    private static int pinActual;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static final String DATABASE = "atm";

    public UIATM() {
        // Set up the frame
        setTitle("ATM");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the PIN panel
        JPanel pinPanel = new JPanel();
        pinPanel.setLayout(new FlowLayout());
        pinPanel.add(new JLabel("Enter PIN:"));
        pinField = new JTextField(10);
        pinPanel.add(pinField);
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(this);
        pinPanel.add(enterButton);
        add(pinPanel, BorderLayout.NORTH);

        // Create the balance panel
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new FlowLayout());
        balanceLabel = new JLabel("Balance: $0.00");
        balancePanel.add(balanceLabel);
        add(balancePanel, BorderLayout.CENTER);

        // Create the transaction panel
        JPanel transactionPanel = new JPanel();
        transactionPanel.setLayout(new GridLayout(4, 2));
        transactionPanel.add(new JLabel("Deposit:"));
        depositField = new JTextField(10);
        transactionPanel.add(depositField);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(this);
        transactionPanel.add(depositButton);

        JLabel withdrawLabel = new JLabel("Withdraw:");
        transactionPanel.add(withdrawLabel);
        withdrawField = new JTextField(10);
        transactionPanel.add(withdrawField);
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(this);
        transactionPanel.add(withdrawButton);

        JLabel newPinLabel = new JLabel("New PIN:");
        transactionPanel.add(newPinLabel);
        newPinField = new JTextField(10);
        transactionPanel.add(newPinField);

        JLabel confirmPinLabel = new JLabel("Confirm PIN:");
        transactionPanel.add(confirmPinLabel);
        confirmPinField = new JTextField(10);
        transactionPanel.add(confirmPinField);

        JButton changePinButton = new JButton("Change PIN");
        changePinButton.addActionListener(this);
        transactionPanel.add(changePinButton);
        add(transactionPanel, BorderLayout.SOUTH);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Cierra la aplicación al presionar el botón "Exit"
            }
        });
        transactionPanel.add(exitButton);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button clicks here
        String command = e.getActionCommand();
        if (command.equals("Enter")) {
            // Validate the entered PIN and update the balance label
            int pinIngresado = Integer.parseInt(pinField.getText());
            Connection connection = null;
            try {
                connection = getConnection();
            } catch (SQLException ex) {
                System.err.println("No se puede conectar a Base de Datos");
                ex.printStackTrace();
                System.exit(1);
            }
            if (validarPIN(connection, pinIngresado)) {
                pinActual = pinIngresado;
                balanceLabel.setText("Balance: $" + saldo);
            } else {
                JOptionPane.showMessageDialog(this, "PIN incorrecto.");
            }
        } else if (command.equals("Deposit")) {
            // Deposit the entered amount and update the balance label
            double cantidad = Double.parseDouble(depositField.getText());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad no válida.");
            } else {
                saldo += cantidad;
                balanceLabel.setText("Balance: $" + saldo);
                Connection connection = null;
                try {
                    connection = getConnection();
                } catch (SQLException ex) {
                    System.err.println("No se puede conectar a Base de Datos");
                    ex.printStackTrace();
                    System.exit(1);
                }
                String historico = "INSERT INTO historico (usuario_id, tipo_operacion, cantidad, fecha) VALUES (?, ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(historico);
                    preparedStatement.setInt(1, usuarioId);
                    preparedStatement.setString(2, "deposito");
                    preparedStatement.setDouble(3, cantidad);
                    preparedStatement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                    preparedStatement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String usuario = "UPDATE usuarios SET saldo = ? WHERE id = ?";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(usuario);
                    preparedStatement.setDouble(1, saldo);
                    preparedStatement.setInt(2, usuarioId);
                    preparedStatement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }depositField.setText("");
        } else if (command.equals("Withdraw")) {
            // Withdraw the entered amount and update the balance label
            double cantidad = Double.parseDouble(withdrawField.getText());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad no válida.");
            } else if (cantidad > saldo) {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
            } else {
                saldo -= cantidad;
                balanceLabel.setText("Balance: $" + saldo);
                Connection connection = null;
                try {
                    connection = getConnection();
                } catch (SQLException ex) {
                    System.err.println("No se puede conectar a Base de Datos");
                    ex.printStackTrace();
                    System.exit(1);
                }
                String historico = "INSERT INTO historico (usuario_id, tipo_operacion, cantidad, fecha) VALUES (?, ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(historico);
                    preparedStatement.setInt(1, usuarioId);
                    preparedStatement.setString(2, "retiro");
                    preparedStatement.setDouble(3, cantidad);
                    preparedStatement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                    preparedStatement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String usuario = "UPDATE usuarios SET saldo = ? WHERE id = ?";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(usuario);
                    preparedStatement.setDouble(1, saldo);
                    preparedStatement.setInt(2, usuarioId);
                    preparedStatement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            } else if (command.equals("Change PIN")) {
                // Change the user's PIN
                int nuevoPin = Integer.parseInt(newPinField.getText());
                int confirmacionPin = Integer.parseInt(confirmPinField.getText());
                if (nuevoPin == confirmacionPin) {
                    pinActual = nuevoPin;
                    JOptionPane.showMessageDialog(this, "PIN actualizado con éxito.");
                    newPinField.setText("");
                    confirmPinField.setText("");
                    Connection connection = null;
                    try {
                        connection = getConnection();
                    } catch (SQLException ex) {
                        System.err.println("No se puede conectar a Base de Datos");
                        ex.printStackTrace();
                        System.exit(1);
                    }
                    String usuario = "UPDATE usuarios SET pin = ? WHERE id = ?";
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(usuario);
                        preparedStatement.setInt(1, pinActual);
                        preparedStatement.setInt(2, usuarioId);
                        preparedStatement.executeUpdate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Los PINs no coinciden.");
                }
            }withdrawField.setText("");
        }

    public static Connection getConnection() throws SQLException {
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found.", e);
        }
        return DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
    }

    public static boolean validarPIN(Connection connection, int pin) {
        String query = "SELECT id, saldo FROM usuarios WHERE pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                usuarioId = resultSet.getInt("id");
                saldo = resultSet.getDouble("saldo");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        UIATM atmGUI = new UIATM();
        atmGUI.setVisible(true);
    }
}
