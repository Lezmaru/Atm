package bo.edu.ucb.sis213;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServicioATM {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static final String DATABASE = "atm";

    private int usuarioId;
    private double saldo;
    private int pinActual;

    public boolean validarPIN(int pin) {
        try (Connection connection = getConnection()) {
            return validarPIN(connection, pin);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void depositar(double cantidad) {
        try (Connection connection = getConnection()) {
            String depositQuery = "UPDATE usuarios SET saldo = saldo + ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(depositQuery)) {
                preparedStatement.setDouble(1, cantidad);
                preparedStatement.setInt(2, usuarioId);
                preparedStatement.executeUpdate();
            }
            actualizarSaldo(connection);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public double getSaldo() {
        return saldo;
    }

    public void cambiarPIN(int nuevoPin) {
        try (Connection connection = getConnection()) {
            String updatePinQuery = "UPDATE usuarios SET pin = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updatePinQuery)) {
                preparedStatement.setInt(1, nuevoPin);
                preparedStatement.setInt(2, usuarioId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Otros métodos de negocio
    // ...

    private Connection getConnection() throws SQLException {
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found.", e);
        }
        return DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
    }

    private boolean validarPIN(Connection connection, int pin) {
        String query = "SELECT id, saldo FROM usuarios WHERE pin = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, pin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    usuarioId = resultSet.getInt("id");
                    saldo = resultSet.getDouble("saldo");
                    pinActual = pin;
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void actualizarSaldo(Connection connection) throws SQLException {
        String query = "SELECT saldo FROM usuarios WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, usuarioId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    saldo = resultSet.getDouble("saldo");
                }
            }
        }
    }

    public boolean retirar(double cantidad) {
        if (cantidad <= 0) {
            return false;
        }

        try (Connection connection = getConnection()) {
            if (saldo >= cantidad) {
                String withdrawQuery = "UPDATE usuarios SET saldo = saldo - ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(withdrawQuery)) {
                    preparedStatement.setDouble(1, cantidad);
                    preparedStatement.setInt(2, usuarioId);
                    preparedStatement.executeUpdate();
                }
                actualizarSaldo(connection);
                return true;
            } else {
                return false; // Fondos insuficientes
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}


