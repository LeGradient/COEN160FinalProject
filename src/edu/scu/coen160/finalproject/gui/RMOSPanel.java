package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.RecyclingMonitor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * RMOS UI class
 * Contains components to select an RCM, display relevant information
 * about the selected machine, and edit the properties of the selected
 * machine.
 */
class RMOSPanel extends JPanel implements Observer {

    /**
     * Login screen UI
     */
    private class LoginPanel extends JPanel {
        /**
         * Description label.
         * Also displays error message on failed login attempt.
         */
        private JLabel descLabel = new JLabel("Enter your login credentials below:");
        /** username text field */
        private JTextField userField = new JTextField(10);
        /** password text field */
        private JPasswordField passField = new JPasswordField(10);
        /** login button */
        private JButton loginButton = new JButton("Login");

        /**
         * Constructor.
         * Initializes UI components and layout.
         */
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

        /**
         * Defines behavior on failed login attempt
         */
        private void badCredentials() {
            this.descLabel.setText("Login failed! Try again.");
            this.descLabel.setForeground(Color.RED);
        }

        /**
         * Removes user input between uses
         */
        private void reset() {
            this.descLabel.setText("Enter your login credentials below:");
            this.descLabel.setForeground(Color.BLACK);
            this.userField.setText("");
            this.passField.setText("");
        }
    }

    /**
     * Main RMOS display and control UI
     */
    private class InfoPanel extends JPanel {

        /**
         * Sub-region of info panel for adding items and changing their prices.
         */
        private class AddItemPanel extends JPanel {
            /** RMOS back-end object */
            private RecyclingMonitor RMOS = RMOSPanel.this.RMOS;
            /** item material name text field */
            private JTextField materialField = new JTextField(10);
            /** item price value text field */
            private JTextField priceField = new JTextField(10);
            /** button to add/change item/price */
            private JButton submitBtn = new JButton("Add Item");

            /**
             * Constructor.
             * Initializes UI components and defines on-click behavior for submit button
             */
            private AddItemPanel() {
                // title label wrapper panel
                JPanel titleWrapper = new JPanel();
                titleWrapper.add(new JLabel("Add an Item Type to a Recycling Machine"));

                // submit button action listener
                this.submitBtn.addActionListener(actionEvent -> {
                    // refresh the RCM
                    int index = InfoPanel.this.rcmList.getSelectedIndex();
                    RMOS.setPrice(index, this.materialField.getText(), Double.parseDouble(this.priceField.getText()));

                    // clear the text fields
                    this.materialField.setText("");
                    this.priceField.setText("");
                });
                JPanel submitPanel = new JPanel();
                submitPanel.add(this.submitBtn);

                this.setLayout(new GridLayout(5, 1));

                JPanel materialPanel = new JPanel(new FlowLayout());
                materialPanel.add(new JLabel("Material: "));
                materialPanel.add(this.materialField);

                JPanel pricePanel = new JPanel(new FlowLayout());
                pricePanel.add(new JLabel("Price: "));
                pricePanel.add(this.priceField);

                this.add(titleWrapper);
                this.add(materialPanel);
                this.add(pricePanel);
                this.add(submitPanel);
            }
        }

        /**
         * Sub-region of info panel for displaying capacity & money info,
         * as well as providing facilities for emptying the machine and
         * restocking the money supply.
         */
        private class CheckStatusPanel extends JPanel {
            /** RMOS back-end object */
            private RecyclingMonitor RMOS = RMOSPanel.this.RMOS;

            /** Label to display weight/capacity ratio */
            private JLabel capacityLabel = new JLabel();

            /** Label to display current money supply */
            private JLabel moneyLabel = new JLabel();

            /** Label to display timestamp of last empty operation */
            private JLabel lastEmpty = new JLabel();

            /**
             * Constructor.
             * Initializes UI components and defines behavior for
             * empty and add money buttons.
             */
            private CheckStatusPanel() {
                this.setLayout(new GridLayout(3, 1));

                // refresh fields when the RCM list changes
                InfoPanel.this.rcmList.addActionListener(actionEvent -> this.refresh());

                // title label wrapper panel
                JPanel titleWrapper = new JPanel();
                titleWrapper.add(new JLabel("Check Recycling Machine Status"));

                // empty button & wrapper panel
                JPanel emptyWrapper = new JPanel();
                JButton emptyBtn = new JButton("Empty");
                lastEmpty = new JLabel("Last emptied: ");
                emptyBtn.addActionListener(actionEvent -> {
                    this.RMOS.empty(InfoPanel.this.rcmList.getSelectedIndex());
                    this.refresh();
                });
                emptyWrapper.add(emptyBtn);
                emptyWrapper.add(lastEmpty);

                // money text field, add button, & wrapper panel
                JPanel moneyPanel = new JPanel(new FlowLayout());
                JTextField moneyField = new JTextField(5);
                JButton moneyBtn = new JButton("Add Money");
                moneyBtn.addActionListener(actionEvent -> {
                    double money = Double.parseDouble(moneyField.getText());
                    this.RMOS.addMoney(InfoPanel.this.rcmList.getSelectedIndex(), money);
                    this.refresh();
                });
                moneyPanel.add(moneyField);
                moneyPanel.add(moneyBtn);

                // grid for aligning labels & corresponding controls
                JPanel gridWrapper = new JPanel();
                JPanel gridPanel = new JPanel(new GridLayout(2, 2));
                gridPanel.add(this.capacityLabel);
                gridPanel.add(emptyWrapper);
                gridPanel.add(this.moneyLabel);
                gridPanel.add(moneyPanel);
                gridWrapper.add(gridPanel);

                this.add(titleWrapper);
                this.add(gridWrapper);
                this.refresh();
            }

            /**
             * Called by top-level RMOS UI on observer update.
             * Fetches latest information to be displayed from the RCMs.
             */
            private void refresh() {
                int i = InfoPanel.this.rcmList.getSelectedIndex();
                this.capacityLabel.setText("Capacity: " + this.RMOS.getWeight(i) + " / " + this.RMOS.getCapacity(i));
                this.moneyLabel.setText("Money: " + this.RMOS.getMoney(i));
                this.lastEmpty.setText("Last emptied: " + this.RMOS.lastEmptied(i));
            }
        }

        /**
         * Sub-region of the info panel for displaying statistical
         * information in bar chart form.
         */
        private class StatisticsPanel extends JPanel {

            /**
             * Chart to show the total items collected.
             */
            JFreeChart itemsChart;

            /**
             * Chart to show the total weight collected.
             */
            JFreeChart weightChart;

            /**
             * Chart to show the total value issued.
             */
            JFreeChart valueChart;

            /**
             * Panel to hold items chart.
             */
            ChartPanel itemsChartPanel;

            /**
             * Panel to hold weight chart.
             */
            ChartPanel weightChartPanel;

            /**
             * Panel to hold value chart.
             */
            ChartPanel valueChartPanel;

            private StatisticsPanel() {
                this.setLayout(new GridLayout(0, 3));

                createChartPanels();

                add(itemsChartPanel);
                add(weightChartPanel);
                add(valueChartPanel);
            }

            /**
             * Creates all three chart panels and sets their dimensions.
             */
            private void createChartPanels() {
                itemsChart = createChart(createDataset(0), "Total Items Collected");
                itemsChartPanel = new ChartPanel(itemsChart);
                itemsChartPanel.setPreferredSize(new Dimension(300,300));

                weightChart = createChart(createDataset(1), "Total Weight Collected");
                weightChartPanel = new ChartPanel(weightChart);
                weightChartPanel.setPreferredSize(new Dimension(300,300));

                valueChart = createChart(createDataset(2), "Total Value Issued");
                valueChartPanel = new ChartPanel(valueChart);
                valueChartPanel.setPreferredSize(new Dimension(300,300));
            }

            /**
             * Called by top-level RMOS UI on observer update.
             * Updates graphs with latest data from RCMs.
             */
            private void refresh() {
                createChartPanels();

                this.removeAll();
                add(itemsChartPanel);
                add(weightChartPanel);
                add(valueChartPanel);
            }

            /**
             * Creates charts with the provided data and title.
             *
             * @param dataset   Data to use for the chart.
             * @param title     Title of the chart.
             * @return          The newly created chart.
             */
            private JFreeChart createChart(DefaultCategoryDataset dataset, String title) {
                JFreeChart chart = ChartFactory.createBarChart(
                        title, null /* x-axis label*/,
                        null /* y-axis label */, dataset);
                chart.setBackgroundPaint(Color.white);
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                BarRenderer renderer = (BarRenderer) plot.getRenderer();
                renderer.setDrawBarOutline(false);
                chart.getLegend().setFrame(BlockBorder.NONE);
                return chart;
            }

            /**
             * Creates datasets using the data from the RCMs.
             * @param typeOfChart   Specifies which data to look at (items/weight/value).
             * @return              The dataset.
             */
            private DefaultCategoryDataset createDataset(int typeOfChart) {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                /*
                    0 = items chart
                    1 = weight chart
                    2 = value chart
                */

                if (typeOfChart == 0) {
                    dataset.addValue(RMOS.totalItemsCollected(0), "RCM1", "");
                    dataset.addValue(RMOS.totalItemsCollected(1), "RCM2", "");
                    return dataset;
                } else if (typeOfChart == 1) {
                    dataset.addValue(RMOS.totalWeightCollected(0), "RCM1", "");
                    dataset.addValue(RMOS.totalWeightCollected(1), "RCM2", "");
                    return dataset;
                } else {
                    dataset.addValue(RMOS.totalValueIssued(0), "RCM1", "");
                    dataset.addValue(RMOS.totalValueIssued(1), "RCM2", "");
                    return dataset;
                }
            }
        }

        /** RMOS back-end object */
        private RecyclingMonitor RMOS = RMOSPanel.this.RMOS;
        /** Dropdown list for selecting an RCM */
        private JComboBox<String> rcmList = new JComboBox<>(this.RMOS.getMachineNames());
        /** switches active panel back to the login panel */
        private JButton logoutBtn = new JButton("Logout");
        private AddItemPanel addItemPanel = new AddItemPanel();
        private CheckStatusPanel checkStatusPanel = new CheckStatusPanel();
        private StatisticsPanel statisticsPanel = new StatisticsPanel();

        /**
         * Constructor.
         * Initializes UI components.
         */
        private InfoPanel() {
            this.setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
            topPanel.add(this.rcmList);
            topPanel.add(this.logoutBtn);

            JPanel gridPanel = new JPanel(new GridLayout(1, 2));
            gridPanel.add(this.addItemPanel);
            gridPanel.add(this.checkStatusPanel);

            this.add(topPanel, BorderLayout.PAGE_START);
            this.add(gridPanel, BorderLayout.CENTER);
            this.add(this.statisticsPanel, BorderLayout.PAGE_END);
        }

        /**
         * Called on top-level RMOS UI observer update.
         * Calls the refresh methods for the status and statistics sub-panels.
         */
        private void refresh() {
            this.checkStatusPanel.refresh();
            this.statisticsPanel.refresh();
        }
    }

    /** RMOS back-end object */
    private RecyclingMonitor RMOS;
    /** JPanel to display the login screen */
    private LoginPanel loginPanel = new LoginPanel();
    /** JPanel to display RMOS controls after a successful login */
    private InfoPanel infoPanel;

    /**
     * Constructor.
     * Binds the UI to an RMOS back-end object.
     * Initializes login and info panels.
     * Defines behavior for logging in and out of the RMOS.
     * @param RMOS  RecyclingMonitor back-end object
     */
    RMOSPanel(RecyclingMonitor RMOS) {
        this.RMOS = RMOS;
        this.infoPanel = new InfoPanel();

        this.add(loginPanel);

        // login button action listener
        this.loginPanel.loginButton.addActionListener(actionEvent -> {
            // attempt to log in with the currently entered credentials
            if (this.RMOS.login(this.loginPanel.userField.getText(), new String (this.loginPanel.passField.getPassword()))) {
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

    /**
     * Observer update method.
     * Called on a state change in one of the two RCMs the RMOS monitors.
     * @param observable    unused
     * @param arg           unused
     */
    public void update(Observable observable, Object arg) {
        infoPanel.refresh();
    }
}