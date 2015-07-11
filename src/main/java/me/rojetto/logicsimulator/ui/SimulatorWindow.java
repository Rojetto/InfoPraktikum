package me.rojetto.logicsimulator.ui;

import me.rojetto.logicsimulator.LogicSimulator;
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
import java.io.PrintStream;

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
    private JLabel imageLabel;

    public SimulatorWindow() {
        super("Logic Simulator");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        PrintStream p = new PrintStream(new TextAreaOutputStream(textArea));
        System.setOut(p); // TODO: Neeeeeee
        System.setErr(p);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        add(scrollPane, BorderLayout.CENTER);

        SpringLayout inputLayout = new SpringLayout();
        inputPanel = new JPanel(inputLayout);
        inputPanel.setPreferredSize(new Dimension(400, 100));
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

        simulateButton = new JButton("Simulation starten");
        simulateButton.addActionListener(this);
        inputPanel.add(simulateButton);

        helpButton = new JButton("Hilfe");
        helpButton.addActionListener(this);
        inputPanel.add(helpButton);

        inputLayout.putConstraint(NORTH, circuitField, 5, NORTH, inputPanel);
        inputLayout.putConstraint(WEST, circuitField, 5, WEST, inputPanel);
        inputLayout.putConstraint(NORTH, circuitButton, 5, NORTH, inputPanel);
        inputLayout.putConstraint(EAST, circuitButton, -5, EAST, inputPanel);
        inputLayout.putConstraint(EAST, circuitField, -5, WEST, circuitButton);

        inputLayout.putConstraint(NORTH, eventButton, 5, SOUTH, circuitButton);
        inputLayout.putConstraint(EAST, eventButton, -5, EAST, inputPanel);
        inputLayout.putConstraint(WEST, eventButton, 0, WEST, circuitButton);
        inputLayout.putConstraint(NORTH, eventField, 0, NORTH, eventButton);
        inputLayout.putConstraint(WEST, eventField, 5, WEST, inputPanel);
        inputLayout.putConstraint(EAST, eventField, -5, WEST, eventButton);

        inputLayout.putConstraint(NORTH, simulateButton, 5, SOUTH, eventButton);
        inputLayout.putConstraint(EAST, simulateButton, -5, EAST, inputPanel);

        inputLayout.putConstraint(NORTH, helpButton, 0, NORTH, simulateButton);
        inputLayout.putConstraint(EAST, helpButton, -5, WEST, simulateButton);

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

                if (e.getSource() == circuitButton) {
                    circuitField.setText(file.getAbsolutePath());
                }

                if (e.getSource() == eventButton) {
                    eventField.setText(file.getAbsolutePath());
                }
            }
        } else if (e.getSource() == simulateButton) {
            try {
                textArea.setText("");
                LogicSimulator simulator = new LogicSimulator(getCircuitFile(), getEventFile());
                SimulationResult result = simulator.simulate();

                System.out.println(ErgCreator.create(result));

                BufferedImage graph = DiagramCreator.create(result);
                imageLabel.setIcon(new ImageIcon(graph));

                pack();
            } catch (Exception e1) {
                System.out.println("Bei der Simulation ist ein Fehler aufgetreten."); //  TODO: Mmmh, nee
                System.out.println(e1.getClass().getName() + ": " + e1.getMessage());
            }
        } else if (e.getSource() == helpButton) {
            JOptionPane.showMessageDialog(this, "HILFE HILFE, WAS MUSS ICH TUN! WAS MACHEN DIE PARAMETER?",
                    "Hilfe", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
