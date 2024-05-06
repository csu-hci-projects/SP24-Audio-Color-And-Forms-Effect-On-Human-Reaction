import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main extends JFrame implements ActionListener {
    private static final List<String> DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String GROUP_1_FOLDER = "1";
    private static final String GROUP_2_FOLDER = "2";

    private JTextField nameField, ageField, genderField;
    private JComboBox<String> groupComboBox;
    private JButton startButton;
    private JLabel imageLabel;
    private Scanner scanner;

    public Main() {
        super("User Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        add(nameLabel);
        add(nameField);

        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        add(ageLabel);
        add(ageField);

        JLabel genderLabel = new JLabel("Gender:");
        genderField = new JTextField();
        add(genderLabel);
        add(genderField);

        JLabel groupLabel = new JLabel("Group Number:");
        groupComboBox = new JComboBox<>(new String[]{"1", "2"});
        add(groupLabel);
        add(groupComboBox);

        startButton = new JButton("Start");
        startButton.addActionListener(this);
        add(startButton);

        scanner = new Scanner(System.in);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = genderField.getText();
            int groupNumber = Integer.parseInt((String) groupComboBox.getSelectedItem());

            String outputFile = name + ".txt";

            try {
                writeToFile(name, age, gender, groupNumber, outputFile);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (groupNumber == 1) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        runGroup1Task();
                        return null;
                    }
                }.execute();
            } else if (groupNumber == 2) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        runGroup2Task();
                        return null;
                    }
                }.execute();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid group number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void runGroup1Task() {
        int[] correctEntries = {0};
        long[] totalTime = {0};
        Random random = new Random();
        Integer[] imageCount = {0};
        final int MAX_IMAGES = 20;
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String gender = genderField.getText();
        int groupNumber = Integer.parseInt((String) groupComboBox.getSelectedItem());
    
        JFrame frame = new JFrame("Group 1 Task");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
    
        imageLabel = new JLabel();
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.setVisible(true);
    
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String[] userInput = {""};
                int keyCode = e.getKeyCode();
    
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        userInput[0] = "up";
                        break;
                    case KeyEvent.VK_DOWN:
                        userInput[0] = "down";
                        break;
                    case KeyEvent.VK_LEFT:
                        userInput[0] = "left";
                        break;
                    case KeyEvent.VK_RIGHT:
                        userInput[0] = "right";
                        break;
                }
    
                if (!userInput[0].isEmpty()) {
                    long startTime = System.currentTimeMillis();
    
                    int directionIndex = random.nextInt(4) + 1;
                    String imageFileName = directionIndex + ".png";
                    String targetDirection = getDirectionFromFileName(imageFileName);
                    String imageFilePath = GROUP_1_FOLDER + "/" + imageFileName;
    
                    try {
                        BufferedImage image = ImageIO.read(new File(imageFilePath));
                        ImageIcon icon = new ImageIcon(image);
                        imageLabel.setIcon(icon);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
    
                    long endTime = System.currentTimeMillis();
                    if (userInput[0].equals(targetDirection)) {
                        correctEntries[0]++;
                    }
                    totalTime[0] += endTime - startTime;
                    imageCount[0]++;
    
                    if (imageCount[0] == MAX_IMAGES) {
                        double averageTime = (double) totalTime[0] / MAX_IMAGES;
                        String outputFile = name + ".txt";
    
                        try {
                            writeToFile(name, age, gender, groupNumber, outputFile, correctEntries[0], averageTime);
                            JOptionPane.showMessageDialog(frame, "Total correct entries: " + correctEntries[0] + "\nAverage time to enter: " + averageTime + " ms", "Results", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            System.exit(0);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(frame, "Error writing to file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        loadNextImage(random, frame);
                    }
                }
            }
        });
    
        
        loadInitialImage(random, frame);
    }
    private void loadNextImage(Random random, JFrame frame) {
        int directionIndex = random.nextInt(4) + 1;
        String imageFileName = directionIndex + ".png";
        String imageFilePath = GROUP_1_FOLDER + "/" + imageFileName;
    
        try {
            BufferedImage image = ImageIO.read(new File(imageFilePath));
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadInitialImage(Random random, JFrame frame) {
        int directionIndex = random.nextInt(4) + 1;
        String imageFileName = directionIndex + ".png";
        String imageFilePath = GROUP_1_FOLDER + "/" + imageFileName;
    
        try {
            BufferedImage image = ImageIO.read(new File(imageFilePath));
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadNextImage(Random random, JFrame frame, JTextField inputField) {
        inputField.setText("");
    }
    
    private String getDirectionFromFileName(String fileName) {
        switch (fileName) {
            case "1.png":
                return "up";
            case "2.png":
                return "down";
            case "3.png":
                return "left";
            case "4.png":
                return "right";
            default:
                return "";
        }
    }  

    private void runGroup2Task() {
        int correctEntries = 0;
        long totalTime = 0;
        Random random = new Random();

        JFrame frame = new JFrame("Group 2 Task");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        imageLabel = new JLabel();
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);
        frame.setVisible(true);

        for (int i = 0; i < 20; i++) {
            int directionIndex = random.nextInt(4) + 1; // Random number between 1 and 4
            String targetDirection = DIRECTIONS.get(directionIndex - 1);
            String imageFilePath = GROUP_2_FOLDER + "/" + directionIndex + ".png";
            String audioFilePath = GROUP_2_FOLDER + "/" + directionIndex + ".wav";

            try {
                BufferedImage image = ImageIO.read(new File(imageFilePath));
                ImageIcon icon = new ImageIcon(image);
                imageLabel.setIcon(icon);

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audioFilePath));
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                JOptionPane.showMessageDialog(frame, "Error loading image or audio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            long startTime = System.currentTimeMillis();
            String userInput = scanner.nextLine().toLowerCase();
            long endTime = System.currentTimeMillis();

            if (userInput.equals(targetDirection)) {
                correctEntries++;
            }

            totalTime += endTime - startTime;
        }

        double averageTime = (double) totalTime / 20;
        JOptionPane.showMessageDialog(frame, "Total correct entries: " + correctEntries + "\nAverage time to enter: " + averageTime + " ms", "Results", JOptionPane.INFORMATION_MESSAGE);

        frame.dispose();
    }
    private static void writeToFile(String name, int age, String gender, int groupNumber, String outputFile, int correctEntries, double averageTime) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write("Name: " + name);
        writer.newLine();
        writer.write("Age: " + age);
        writer.newLine();
        writer.write("Gender: " + gender);
        writer.newLine();
        writer.write("Group Number: " + groupNumber);
        writer.newLine();
        writer.write("Total Correct Entries: " + correctEntries);
        writer.newLine();
        writer.write("Average Time to Enter: " + averageTime + " ms");
        writer.close();
    }
    private static void writeToFile(String name, int age, String gender, int groupNumber, String outputFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write("Name: " + name);
        writer.newLine();
        writer.write("Age: " + age);
        writer.newLine();
        writer.write("Gender: " + gender);
        writer.newLine();
        writer.write("Group Number: " + groupNumber);
        writer.close();
    }
}