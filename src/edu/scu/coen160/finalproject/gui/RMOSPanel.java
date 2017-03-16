package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import java.awt.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.general.DefaultKeyedValues2DDataset;

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
            private RecyclingMonitor RMOS = RMOSPanel.this.RMOS;
            private JTextField materialField = new JTextField(10);
            private JTextField priceField = new JTextField(10);
            private JButton submitBtn = new JButton("Add Item");

            private AddItemPanel() {
                // submit button action listener
                this.submitBtn.addActionListener(actionEvent -> {
                    // update the RCM
                    int index = rcmList.getSelectedIndex();
                    RMOS.setPrice(index, this.materialField.getText(), Double.parseDouble(this.priceField.getText()));

                    // clear the text fields
                    this.materialField.setText("");
                    this.priceField.setText("");
                });

                this.setLayout(new GridLayout(5, 1));

                JPanel materialPanel = new JPanel(new FlowLayout());
                materialPanel.add(new JLabel("Material: "));
                materialPanel.add(this.materialField);

                JPanel pricePanel = new JPanel(new FlowLayout());
                pricePanel.add(new JLabel("Price: "));
                pricePanel.add(this.priceField);

                this.add(new JLabel("Add an Item Type to a Recycling Machine"));
                this.add(materialPanel);
                this.add(pricePanel);
                this.add(this.submitBtn);
            }
        }

        private class CheckStatusPanel extends JPanel {
            private RecyclingMonitor RMOS = RMOSPanel.this.RMOS;
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
                    this.RMOS.empty(InfoPanel.this.rcmList.getSelectedIndex());
                    this.update();
                });

                JPanel moneyPanel = new JPanel(new FlowLayout());

                JTextField moneyField = new JTextField(5);

                JButton moneyBtn = new JButton("Add Money");
                moneyBtn.addActionListener(actionEvent -> {
                    double money = Double.parseDouble(moneyField.getText());
                    this.RMOS.addMoney(InfoPanel.this.rcmList.getSelectedIndex(), money);
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
                int i = InfoPanel.this.rcmList.getSelectedIndex();
                this.capacityLabel.setText("Capacity: " + this.RMOS.getWeight(i) + " / " + this.RMOS.getCapacity(i));
                this.moneyLabel.setText("Money: " + this.RMOS.getMoney(i));
            }
        }

        private class StatisticsPanel extends JPanel {
            JFreeChart itemsChart;
            JFreeChart weightChart;
            JFreeChart valueChart;

            ChartPanel itemsChartPanel;
            ChartPanel weightChartPanel;
            ChartPanel valueChartPanel;

            private StatisticsPanel() {
                DefaultKeyedValues2DDataset itemsDataset = createDataset(0);
                itemsChart = createChart(itemsDataset);
                itemsChartPanel = new ChartPanel(itemsChart, false);


                DefaultKeyedValues2DDataset weightDataset = createDataset(1);
                weightChart = createChart(weightDataset);
                weightChartPanel = new ChartPanel(weightChart, false);

                DefaultKeyedValues2DDataset valueDataset = createDataset(2);
                valueChart = createChart(valueDataset);
                valueChartPanel = new ChartPanel(valueChart, false);

                add(itemsChartPanel);
                add(weightChartPanel);
                add(valueChartPanel);

                InfoPanel.this.rcmList.addActionListener(actionEvent -> {
                    this.update();
                });
            }

            private void update() {
                itemsChart = createChart(createDataset(0));
                itemsChartPanel = new ChartPanel(itemsChart);

                weightChart = createChart(createDataset(1));
                weightChartPanel = new ChartPanel(weightChart);

                valueChart = createChart(createDataset(2));
                valueChartPanel = new ChartPanel(valueChart);

                this.removeAll();
                add(itemsChartPanel);
                add(weightChartPanel);
                add(valueChartPanel);
            }

            private JFreeChart createChart(DefaultKeyedValues2DDataset dataset) {
                JFreeChart chart = ChartFactory.createBarChart(
                        "STATISTICS", null /* x-axis label*/,
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

            private DefaultKeyedValues2DDataset createDataset(int typeOfChart) {
                if (typeOfChart == 0) {
                    DefaultKeyedValues2DDataset dataset = new DefaultKeyedValues2DDataset();
                    dataset.addValue(RMOS.totalItemsCollectedBoundless(0), "RCM1", "Total Items Collected");
                    dataset.addValue(RMOS.totalItemsCollectedBoundless(1), "RCM2", "Total Items Collected");
                    return dataset;
                } else if (typeOfChart == 1) {
                    DefaultKeyedValues2DDataset dataset = new DefaultKeyedValues2DDataset();
                    dataset.addValue(RMOS.totalWeightCollectedBoundless(0), "RCM1", "Total Weight Collected");
                    dataset.addValue(RMOS.totalWeightCollectedBoundless(1), "RCM2", "Total Weight Collected");
                    return dataset;
                } else {
                    DefaultKeyedValues2DDataset dataset = new DefaultKeyedValues2DDataset();
                    dataset.addValue(RMOS.totalValueIssuedBoundless(0), "RCM1", "Total Value Issued");
                    dataset.addValue(RMOS.totalValueIssuedBoundless(1), "RCM2", "Total Value Issued");
                    return dataset;
                }
            }
        }

        private RecyclingMonitor RMOS = RMOSPanel.this.RMOS;
        private JComboBox<String> rcmList = new JComboBox<>(this.RMOS.getMachineNames());
        private AddItemPanel addItemPanel = new AddItemPanel();
        private CheckStatusPanel checkStatusPanel = new CheckStatusPanel();
        private JButton logoutBtn = new JButton("Logout");

        private InfoPanel() {
            this.setLayout(new BorderLayout());

            JPanel gridPanel = new JPanel(new GridLayout(1, 3));
            gridPanel.add(this.addItemPanel);
            gridPanel.add(this.checkStatusPanel);
            gridPanel.add(this.new StatisticsPanel());
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