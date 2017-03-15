package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import java.awt.*;

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
    }

    private class InfoPanel extends JPanel {

        private class AddItemPanel extends JPanel {
            private JComboBox<String> rcmList = new JComboBox<>();
            private JTextField materialField = new JTextField(10);
            private JTextField priceField = new JTextField(10);
            private JButton submitBtn = new JButton("Add Item");

            private AddItemPanel() {
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

        private AddItemPanel addItemPanel = new AddItemPanel();

        private InfoPanel() {
            this.setLayout(new GridLayout(2, 3));
            this.add(this.addItemPanel);
        }

    }

    private RecyclingMonitor RMOS;
    private LoginPanel loginPanel = new LoginPanel();
    private InfoPanel infoPanel = new InfoPanel();

    RMOSPanel(RecyclingMonitor RMOS) {
        this.RMOS = RMOS;
        this.add(loginPanel);

        // initialize the login screen
        this.loginPanel.loginButton.addActionListener(actionEvent -> {
            // attempt to log in with the currently entered credentials
            if (this.RMOS.login(this.loginPanel.userField.getText(), this.loginPanel.passField.getText())) {
                // login succeeded
                // switch from the login panel to the info panel
                this.remove(this.loginPanel);
                this.add(this.infoPanel);
            } else {
                this.loginPanel.descLabel.setText("Login failed! Try again.");
                this.loginPanel.descLabel.setForeground(Color.RED);
                // login failed
                // stay on the login panel and display a message
            }
        });
    }
}