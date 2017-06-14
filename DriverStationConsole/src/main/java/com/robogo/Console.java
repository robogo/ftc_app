package com.robogo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Console implements DriverStation.EventHandler {

    private Gui gui;
    private int logLevel;

    public static void main(String[] args) {
        int level = DriverStation.INFO;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--trace") && i < args.length - 1) {
                level = getTrace(args[i+1]);
            }
        }

        Console console = new Console();
        console.logLevel = level;
        DriverStation ds = new DriverStation();
        ds.setHandler(console);
        ds.start();

        console.gui = new Gui(ds);
        console.gui.setVisible(true);
    }

    private static int getTrace(String value) {
        if (value.compareToIgnoreCase("INFO") == 0) return DriverStation.INFO;
        if (value.compareToIgnoreCase("DEBUG") == 0) return DriverStation.DEBUG;
        return DriverStation.OUTPUT;
    }

    @Override
    public void onOpModeList(String[] opModes) {
        if (gui != null) {
            gui.setOpModes(opModes);
        }
    }

    @Override
    public void OnTelemetry(Telemetry tele) {
        if (gui != null) {
            gui.setTelemetry(tele);
        }
    }

    @Override
    public void onLog(int level, String format, Object... args) {
        if (level <= this.logLevel) {
            System.out.println(String.format(format, args));
        }
    }

    private static class Gui extends JFrame implements ActionListener {
        private DriverStation station;
        private JComboBox opModes;
        private JButton cmd;
        private JTextArea telemetry;

        public Gui(DriverStation station) {
            super("Driver Station");
            this.station = station;

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setPreferredSize(new Dimension(topPanel.getWidth(), 100));
            opModes = new JComboBox();
            opModes.addItem("");
            //opModes.addItem("Item1");
            //opModes.addItem("Item2");
            topPanel.add(opModes, BorderLayout.PAGE_START);
            cmd = new JButton("INIT");
            cmd.setEnabled(false);
            topPanel.add(cmd, BorderLayout.CENTER);
            add(topPanel, BorderLayout.PAGE_START);
            telemetry = new JTextArea("telemetry");
            telemetry.setEditable(false);
            add(telemetry, BorderLayout.CENTER);
            opModes.addActionListener(this);
            cmd.addActionListener(this);

            pack();
            setLocationRelativeTo(null);
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        public void setOpModes(String[] list) {
            this.opModes.removeAllItems();
            this.opModes.addItem("");
            for (String item : list) {
                this.opModes.addItem(item);
            }
            this.opModes.setSelectedIndex(0);
        }

        public void setTelemetry(Telemetry tele) {
            telemetry.setText("");
            telemetry.append(String.format("robot: %d\n", tele.getRobotState()));
            for (Frame.StringMap.Entry entry : tele.strings().entrySet()) {
                telemetry.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
            }
            for (Frame.StringMap.Entry entry : tele.numbers().entrySet()) {
                telemetry.append(String.format("%s=%f\n", entry.getKey(), entry.getValue()));
            }
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == opModes) {
                cmd.setEnabled(opModes.getSelectedIndex() > 0);
            } else if (actionEvent.getSource() == cmd) {
                station.changeOpMode(cmd.getText(), opModes.getSelectedItem().toString());
                if (cmd.getText().equals("INIT")) {
                    cmd.setText("START");
                } else if (cmd.getText().equals("START")) {
                    cmd.setText("STOP");
                } else if (cmd.getText().equals("STOP")) {
                    cmd.setText("INIT");
                }
            }
        }
    }
}
