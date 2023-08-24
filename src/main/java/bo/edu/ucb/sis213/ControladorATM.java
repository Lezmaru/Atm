package bo.edu.ucb.sis213;

import javax.swing.*;

public class ControladorATM {
    private UIATM ui;
    private ServicioATM atmService;

    public ControladorATM(UIATM ui) {
        this.ui = ui;
        atmService = new ServicioATM();
    }

    public void handleUserAction(String action) {
        if (action.equals("Ingresar")) {
            int pin = Integer.parseInt(ui.getPinFieldText());
            boolean isValid = atmService.validarPIN(pin);
            if (isValid) {
                double saldo = atmService.getSaldo();
                ui.updateSaldoLabel(saldo);
            } else {
                JOptionPane.showMessageDialog(ui, "PIN incorrecto.");
            }
        } else if (action.equals("Depositar")) {
            double cantidad = Double.parseDouble(ui.getDepositFieldText());
            atmService.depositar(cantidad);
            double nuevoSaldo = atmService.getSaldo();
            ui.updateSaldoLabel(nuevoSaldo);
            ui.clearDepositField();
        } else if (action.equals("Retirar")) {
            double cantidad = Double.parseDouble(ui.getWithdrawFieldText());
            boolean success = atmService.retirar(cantidad);
            if (success) {
                double nuevoSaldo = atmService.getSaldo();
                ui.updateSaldoLabel(nuevoSaldo);
                ui.clearWithdrawField();
            } else {
                JOptionPane.showMessageDialog(ui, "Saldo insuficiente.");
            }
        } else if (action.equals("Cambiar PIN")) {
            int nuevoPin = Integer.parseInt(ui.getNewPinFieldText());
            int confirmarPin = Integer.parseInt(ui.getConfirmPinFieldText());
            if (nuevoPin == confirmarPin) {
                atmService.cambiarPIN(nuevoPin);
                ui.clearNewPinFields();
                JOptionPane.showMessageDialog(ui, "PIN actualizado con éxito.");
            } else {
                JOptionPane.showMessageDialog(ui, "Los PINs no coinciden.");
            }
        }
    }
}

