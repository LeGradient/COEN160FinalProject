package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

class RCMPanel extends JPanel {
    private JButton toggleSessionButton;
    private JButton endSessionButton;
    private JButton weighButton;
    private JButton submitButton;
    private JTextArea receipt;
    private JTextArea info;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private RecyclingMachine myRCM;

    private RecyclableItem myItem;

    private void toggleButtons() {
        if (myRCM.isSession()) {
            toggleSessionButton.setText("Cancel Session");
            endSessionButton.setEnabled(true);
        } else {
            endSessionButton.setEnabled(false);
            toggleSessionButton.setText("Start Session");
        }
    }

    public RCMPanel(RecyclingMachine RCM) {
        myRCM = RCM;

        this.setLayout(new BorderLayout());
        receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.append("Receipt\n");

        info = new JTextArea();
        info.setEditable(false);

        info.append("ID: " + String.valueOf(myRCM.getId()) + "\n");
        info.append("Location: " + myRCM.getLocation() + "\n");

        for(int i = 0; i < 3; i++)
            info.append("\n");

        info.append("Prices\n");

        info.append(myRCM.printPrices());

        toggleSessionButton = new JButton("Start Session");
        toggleSessionButton.addActionListener(actionEvent -> {
            if (!myRCM.isSession()) {
                myRCM.startSession();
                toggleButtons();
            } else {
              myRCM.cancelSession();
              toggleButtons();
            }
        });

        submitButton = new JButton("Submit Item");
        submitButton.addActionListener(actionEvent -> {
            // TODO pay out for 1 item

            myRCM.recycleItem(myItem);
            myRCM.payOut(myRCM.calculatePrice(myItem));
            receipt.setText("Receipt\n");
        });

        endSessionButton = new JButton("End Session & Submit Items");
        endSessionButton.setEnabled(false);
        endSessionButton.addActionListener(actionEvent -> {
            if (myRCM.isSession()) {
                myRCM.submitSession();
                toggleButtons();
                myRCM.payOut();
                receipt.setText("Receipt\n");
            }
        });

        weighButton = new JButton("Weigh Item");
        weighButton.addActionListener(actionEvent -> {
            String[] items = myRCM.getAcceptableItems().toArray(new String[0]);

            int size = myRCM.getAcceptableItems().size();

            Random r = new Random();
            int materialIndex = r.nextInt(size);

            myItem = new RecyclableItem(items[materialIndex]);
            receipt.append(myItem.toString() + " ");
            receipt.append("$" + String.valueOf(myRCM.calculatePrice(myItem)) + "\n");
        });

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        centerPanel.add(weighButton);
        centerPanel.add(submitButton);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        bottomPanel.add(toggleSessionButton);
        bottomPanel.add(endSessionButton);

        this.add(centerPanel, BorderLayout.PAGE_START);
        this.add(bottomPanel, BorderLayout.PAGE_END);
        this.add(info, BorderLayout.LINE_START);
        this.add(receipt, BorderLayout.LINE_END);
    }
}

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

    public RMOSPanel(RecyclingMonitor RMOS) {
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

public class RecyclingUI extends JFrame {
    private RecyclingMachine myRCM1;
    private RecyclingMachine myRCM2;
    private RecyclingMonitor myRMOS;

    private JTabbedPane tabs = new JTabbedPane();
    public RecyclingUI(RecyclingMachine RCM1, RecyclingMachine RCM2, RecyclingMonitor RMOS) {
        super("Recycling Simulation");

        myRCM1 = RCM1;
        myRCM2 = RCM2;
        myRMOS = RMOS;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;

        setSize(width/4 , height/4);
        setLocationRelativeTo(null);

        Container container = getContentPane();
        container.add(tabs);

        tabs.addTab("RCM 1", new RCMPanel(myRCM1));
        tabs.addTab("RCM 2", new RCMPanel(myRCM2));
        tabs.addTab("RMOS", new RMOSPanel(myRMOS));

        setVisible(true);
    }
}
