package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

class RMOSPanel extends JPanel {

    private class LoginPanel extends JPanel {
        private JLabel descLabel = new JLabel("Enter your login credentials below:");
        private JTextField userField = new JTextField(10);
        private JPasswordField passField = new JPasswordField(10);
        private JButton loginButton = new JButton("Login");

        private LoginPanel() {
            this.setLayout(new GridLayout(4, 1));

            JPanel userPanel = new JPanel(new FlowLayout());
            userPanel.add(new JLabel("Username: "));
            userPanel.add(this.userField);

            JPanel passPanel = new JPanel(new FlowLayout());
            passPanel.add(new JLabel("Password: "));
            passPanel.add(this.passField);

            this.add(this.descLabel);
            this.add(userPanel);
            this.add(passPanel);
            this.add(this.loginButton);
        }

        private void badCredentials() {
            this.descLabel.setText("Login failed! Try again.");
            this.descLabel.setForeground(Color.RED);
        }

        private void reset() {
            this.descLabel.setText("Enter your login credentials below:");
            this.descLabel.setForeground(Color.BLACK);
            this.userField.setText("");
            this.passField.setText("");
        }
    }

    private class InfoPanel extends JPanel {

        private class AddItemPanel extends JPanel {
            private JComboBox<String> rcmList;
            private JTextField materialField = new JTextField(10);
            private JTextField priceField = new JTextField(10);
            private JButton submitBtn = new JButton("Add Item");

            private AddItemPanel() {
                // generate a list of RCM names
                ArrayList<String> machineNames = new ArrayList<>();
                for (RecyclingMachine machine : RMOSPanel.this.RMOS.getMachines()) {
                    machineNames.add(machine.getTableName());
                }
                // initialize rcmList with the RCM names
                this.rcmList = new JComboBox<>(machineNames.toArray(new String[0]));

                // submit button action listener
                this.submitBtn.addActionListener(actionEvent -> {
                    // update the RCM
                    int index = rcmList.getSelectedIndex();
                    RecyclingMachine machine = RMOSPanel.this.RMOS.getMachines()[index];
                    machine.setPrice(this.materialField.getText(), Double.parseDouble(this.priceField.getText()));

                    // sanity check
                    System.out.println(machine.getPrice(this.materialField.getText()));

                    // clear the text fields
                    this.materialField.setText("");
                    this.priceField.setText("");
                });

                this.setLayout(new GridLayout(5, 1));

                JPanel rcmPanel = new JPanel(new FlowLayout());
                rcmPanel.add(new JLabel("RCM ID: "));
                rcmPanel.add(this.rcmList);

                JPanel materialPanel = new JPanel(new FlowLayout());
                materialPanel.add(new JLabel("Material: "));
                materialPanel.add(this.materialField);

                JPanel pricePanel = new JPanel(new FlowLayout());
                pricePanel.add(new JLabel("Price: "));
                pricePanel.add(this.priceField);

                this.add(new JLabel("Add an Accepted Item to a Recycling Machine"));
                this.add(rcmPanel);
                this.add(materialPanel);
                this.add(pricePanel);
                this.add(this.submitBtn);
            }
        }

        private class CheckStatusPanel extends JPanel {
            private JLabel capacityLabel = new JLabel();
            private JLabel moneyLabel = new JLabel();

            private CheckStatusPanel() {
                this.setLayout(new GridLayout(3, 1));

                // update fields when the RCM list changes
                InfoPanel.this.rcmList.addActionListener(actionEvent -> {
                    this.update();
                });

                JLabel titleLabel = new JLabel("Check Status");

                JButton emptyBtn = new JButton("Empty");
                emptyBtn.addActionListener(actionEvent -> {
                    RecyclingMachine machine = RMOSPanel.this.RMOS.getMachineAt(InfoPanel.this.rcmList.getSelectedIndex());
                    machine.empty();
                    this.update();
                });

                JPanel moneyPanel = new JPanel(new FlowLayout());

                JTextField moneyField = new JTextField(5);

                JButton moneyBtn = new JButton("Add Money");
                moneyBtn.addActionListener(actionEvent -> {
                    RecyclingMachine machine = RMOSPanel.this.RMOS.getMachineAt(InfoPanel.this.rcmList.getSelectedIndex());
                    double money = Double.parseDouble(moneyField.getText());
                    machine.addMoney(money);
                    this.update();
                });

                moneyPanel.add(moneyField);
                moneyPanel.add(moneyBtn);

                JPanel gridPanel = new JPanel(new GridLayout(2, 2));
                gridPanel.add(this.capacityLabel);
                gridPanel.add(emptyBtn);
                gridPanel.add(this.moneyLabel);
                gridPanel.add(moneyPanel);


                this.add(titleLabel);
                this.add(rcmList);
                this.add(gridPanel);
                this.update();
            }

            private void update() {
                RecyclingMachine machine = RMOSPanel.this.RMOS.getMachineAt(InfoPanel.this.rcmList.getSelectedIndex());
                this.capacityLabel.setText("Capacity: " + machine.getWeight() + " / " + machine.getCapacity());
                this.moneyLabel.setText("Money: " + machine.getMoney());
            }
        }

        private JComboBox<String> rcmList = new JComboBox<>(RMOSPanel.this.RMOS.getMachineNames());
        private AddItemPanel addItemPanel = new AddItemPanel();
        private CheckStatusPanel checkStatusPanel = new CheckStatusPanel();
        private JButton logoutBtn = new JButton("Logout");

        private InfoPanel() {
            this.setLayout(new BorderLayout());

            JPanel gridPanel = new JPanel(new GridLayout(1, 3));
            gridPanel.add(this.addItemPanel);
            gridPanel.add(this.checkStatusPanel);

            this.add(this.rcmList, BorderLayout.PAGE_START);
            this.add(gridPanel, BorderLayout.CENTER);
            this.add(this.logoutBtn, BorderLayout.PAGE_END);
        }

    }

    private RecyclingMonitor RMOS;
    private LoginPanel loginPanel = new LoginPanel();
    private InfoPanel infoPanel;

    RMOSPanel(RecyclingMonitor RMOS) {
        this.RMOS = RMOS;
        this.infoPanel = new InfoPanel();

        this.add(loginPanel);

        // login button action listener
        this.loginPanel.loginButton.addActionListener(actionEvent -> {
            // attempt to log in with the currently entered credentials
            if (this.RMOS.login(this.loginPanel.userField.getText(), this.loginPanel.passField.getText())) {
                // login succeeded
                // switch from the login panel to the info panel
                this.loginPanel.reset();
                this.remove(this.loginPanel);
                this.add(this.infoPanel);
                this.repaint();
            } else {
                // login failed
                // stay on the login panel and display a message
                this.loginPanel.badCredentials();
            }
        });

        // logout button action listener
        this.infoPanel.logoutBtn.addActionListener(actionEvent -> {
            // switch from the info panel to the login panel
            this.remove(this.infoPanel);
            this.add(this.loginPanel);
            this.repaint();
        });
    }
}