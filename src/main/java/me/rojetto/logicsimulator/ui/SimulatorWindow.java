package me.rojetto.logicsimulator.ui;

import me.rojetto.logicsimulator.LogicSimulator;
import me.rojetto.logicsimulator.LogicSimulatorException;
import me.rojetto.logicsimulator.core.SimulationResult;
import me.rojetto.logicsimulator.files.DiagramCreator;
import me.rojetto.logicsimulator.files.ErgCreator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.swing.SpringLayout.*;

public class SimulatorWindow extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JPanel inputPanel;
    private JTextField circuitField;
    private JButton circuitButton;
    private JTextField eventField;
    private JButton eventButton;
    private JButton simulateButton;
    private JButton helpButton;
    private JLabel maxUpdatesLabel;
    private JTextField maxUpdatesField;
    private JLabel imageLabel;

    private final PrintWriter guiOut;

    public SimulatorWindow() {
        super("Logic Simulator");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        guiOut = new PrintWriter(new TextAreaOutputStream(textArea), true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        add(scrollPane, BorderLayout.CENTER);

        SpringLayout inputLayout = new SpringLayout();
        inputPanel = new JPanel(inputLayout);
        add(inputPanel, BorderLayout.NORTH);

        circuitField = new JTextField();
        inputPanel.add(circuitField);

        circuitButton = new JButton("Schaltung suchen");
        circuitButton.addActionListener(this);
        inputPanel.add(circuitButton);

        eventField = new JTextField();
        inputPanel.add(eventField);

        eventButton = new JButton("Events suchen");
        eventButton.addActionListener(this);
        inputPanel.add(eventButton);

        simulateButton = new JButton("Simulieren");
        simulateButton.addActionListener(this);
        inputPanel.add(simulateButton);

        helpButton = new JButton("Hilfe");
        helpButton.addActionListener(this);
        inputPanel.add(helpButton);

        maxUpdatesLabel = new JLabel("Maximale Updates");
        inputPanel.add(maxUpdatesLabel);

        maxUpdatesField = new JTextField();
        maxUpdatesField.setText(LogicSimulator.DEFAULT_MAX_UPDATES + "");
        inputPanel.add(maxUpdatesField);

        inputLayout.putConstraint(NORTH, circuitButton, 5, NORTH, inputPanel);
        inputLayout.putConstraint(EAST, circuitButton, -5, EAST, inputPanel);
        inputLayout.putConstraint(WEST, circuitField, 5, WEST, inputPanel);
        inputLayout.putConstraint(EAST, circuitField, -5, WEST, circuitButton);
        inputLayout.putConstraint(VERTICAL_CENTER, circuitField, 0, VERTICAL_CENTER, circuitButton);

        inputLayout.putConstraint(NORTH, eventButton, 5, SOUTH, circuitButton);
        inputLayout.putConstraint(EAST, eventButton, -5, EAST, inputPanel);
        inputLayout.putConstraint(WEST, eventButton, 0, WEST, circuitButton);
        inputLayout.putConstraint(WEST, eventField, 5, WEST, inputPanel);
        inputLayout.putConstraint(EAST, eventField, -5, WEST, eventButton);
        inputLayout.putConstraint(VERTICAL_CENTER, eventField, 0, VERTICAL_CENTER, eventButton);

        inputLayout.putConstraint(NORTH, simulateButton, 5, SOUTH, eventButton);
        inputLayout.putConstraint(EAST, simulateButton, -5, EAST, inputPanel);
        inputLayout.putConstraint(WEST, simulateButton, 0, WEST, circuitButton);

        inputLayout.putConstraint(NORTH, helpButton, 0, NORTH, simulateButton);
        inputLayout.putConstraint(EAST, helpButton, -5, WEST, simulateButton);

        inputLayout.putConstraint(WEST, maxUpdatesLabel, 5, WEST, inputPanel);
        inputLayout.putConstraint(VERTICAL_CENTER, maxUpdatesLabel, 0, VERTICAL_CENTER, simulateButton);

        inputLayout.putConstraint(WEST, maxUpdatesField, 5, EAST, maxUpdatesLabel);
        inputLayout.putConstraint(EAST, maxUpdatesField, -5, WEST, helpButton);
        inputLayout.putConstraint(VERTICAL_CENTER, maxUpdatesField, 0, VERTICAL_CENTER, simulateButton);

        inputLayout.putConstraint(SOUTH, inputPanel, 5, SOUTH, simulateButton);

        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.SOUTH);

        pack();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
        setVisible(true);
    }

    private File getCircuitFile() {
        return new File(circuitField.getText());
    }

    private File getEventFile() {
        return new File(eventField.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == circuitButton || e.getSource() == eventButton) {
            JFileChooser fileChooser = new JFileChooser();

            if (e.getSource() == circuitButton) {
                if (getCircuitFile().exists()) {
                    fileChooser.setCurrentDirectory(getCircuitFile());
                }

                fileChooser.setFileFilter(new FileNameExtensionFilter("Circuit Datei", "cir"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("LogiFlash XML Datei", "xml"));
            }

            if (e.getSource() == eventButton) {
                if (getEventFile().exists()) {
                    fileChooser.setCurrentDirectory(getEventFile());
                }

                fileChooser.setFileFilter(new FileNameExtensionFilter("Events Datei", "events"));
            }

            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String fileNameWithoutExtension = file.getName();
                if (fileNameWithoutExtension.contains(".")) {
                    fileNameWithoutExtension = fileNameWithoutExtension.substring(0,
                            fileNameWithoutExtension.lastIndexOf("."));
                }

                if (e.getSource() == circuitButton) {
                    circuitField.setText(file.getAbsolutePath());
                    if (eventField.getText().length() == 0) {
                        eventField.setText(file.getParent() + File.separator + fileNameWithoutExtension + ".events");
                    }
                }

                if (e.getSource() == eventButton) {
                    eventField.setText(file.getAbsolutePath());
                    if (circuitField.getText().length() == 0) {
                        circuitField.setText(file.getParent() + File.separator + fileNameWithoutExtension + ".cir");
                    }
                }
            }
        } else if (e.getSource() == simulateButton) {
            try {
                textArea.setText("");
                int maxUpdates = Integer.parseInt(maxUpdatesField.getText());
                LogicSimulator simulator = new LogicSimulator(getCircuitFile(), getEventFile(), maxUpdates);
                SimulationResult result = simulator.simulate();

                guiOut.println(ErgCreator.create(result));

                BufferedImage graph = DiagramCreator.create(result);
                imageLabel.setIcon(new ImageIcon(graph));

                pack();
            } catch (LogicSimulatorException | NumberFormatException | IOException e1) {
                guiOut.println(e1);
            }
        } else if (e.getSource() == helpButton) {
            JOptionPane.showMessageDialog(this, "HILFE HILFE, WAS MUSS ICH TUN! WAS MACHEN DIE PARAMETER?",
                    "Hilfe", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
