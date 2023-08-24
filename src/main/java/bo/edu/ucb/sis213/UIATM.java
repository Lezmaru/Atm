package bo.edu.ucb.sis213;
import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;

public class UIATM extends JFrame implements ActionListener {
    private ControladorATM atmController;

    private JTextField pinField;
    private JLabel saldoLabel;
    private JTextField depositarField;
    private JTextField retirarField;
    private JTextField nuevoPinField;
    private JTextField confirmarPinField;

    public UIATM() {
        initializeUI();
        atmController = new ControladorATM(this);
    }

    private void initializeUI() {
        setTitle("ATM");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel pinPanel = new JPanel();
        pinPanel.setLayout(new FlowLayout());
        pinPanel.add(new JLabel("Ingrese el PIN:"));
        pinField = new JTextField(10);
        pinPanel.add(pinField);
        JButton enterButton = new JButton("Ingresar");
        enterButton.addActionListener(this);
        pinPanel.add(enterButton);
        add(pinPanel, BorderLayout.NORTH);

        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new FlowLayout());
        saldoLabel = new JLabel("Saldo: $0.00");
        balancePanel.add(saldoLabel);
        add(balancePanel, BorderLayout.CENTER);

        JPanel transactionPanel = new JPanel();
        transactionPanel.setLayout(new GridLayout(4, 2));
        transactionPanel.add(new JLabel("Depositar:"));
        depositarField = new JTextField(10);
        transactionPanel.add(depositarField);

        JButton depositButton = new JButton("Depositar");
        depositButton.addActionListener(this);
        transactionPanel.add(depositButton);

        JLabel withdrawLabel = new JLabel("Retirar:");
        transactionPanel.add(withdrawLabel);
        retirarField = new JTextField(10);
        transactionPanel.add(retirarField);
        JButton withdrawButton = new JButton("Retirar");
        withdrawButton.addActionListener(this);
        transactionPanel.add(withdrawButton);

        JLabel newPinLabel = new JLabel("Nuevo PIN:");
        transactionPanel.add(newPinLabel);
        nuevoPinField = new JTextField(10);
        transactionPanel.add(nuevoPinField);

        JLabel confirmPinLabel = new JLabel("Confirmar PIN:");
        transactionPanel.add(confirmPinLabel);
        confirmarPinField = new JTextField(10);
        transactionPanel.add(confirmarPinField);

        JButton changePinButton = new JButton("Cambiar PIN");
        changePinButton.addActionListener(this);
        transactionPanel.add(changePinButton);
        add(transactionPanel, BorderLayout.SOUTH);

        JButton exitButton = new JButton("Salir");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        transactionPanel.add(exitButton);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        atmController.handleUserAction(e.getActionCommand());
    }

    public void updateSaldoLabel(double saldo) {
        saldoLabel.setText("Saldo: $" + saldo);
    }

    public void clearDepositField() {
        depositarField.setText("");
    }

    public void clearWithdrawField() {
        retirarField.setText("");
    }
    public String getPinFieldText() {
        return pinField.getText();
    }

    public String getDepositFieldText() {
        return depositarField.getText();
    }

    public String getWithdrawFieldText() {
        return retirarField.getText();
    }

    public String getNewPinFieldText() {
        return nuevoPinField.getText();
    }

    public String getConfirmPinFieldText() {
        return confirmarPinField.getText();
    }

    public void setSaldoLabelText(String text) {
        saldoLabel.setText(text);
    }
    public void clearNewPinFields() {
        nuevoPinField.setText("");
        confirmarPinField.setText("");
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIATM atmGUI = new UIATM();
            atmGUI.setVisible(true);
        });
    }
}
