import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class UserInputGUI {
    private static final String MEDIA_PLAYER_ID = "mediaplayer";

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("User Information");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel to hold the components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 5, 10)); // Adjust layout to grid for better organization

        // Create labels and text fields for name, age, gender, and choice
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameTextField = new JTextField(20);

        JLabel ageLabel = new JLabel("Age:");
        JTextField ageTextField = new JTextField(10);

        JLabel genderLabel = new JLabel("Gender:");
        JTextField genderTextField = new JTextField(10);

        JLabel choiceLabel = new JLabel("Enter 1 or 2:");
        JTextField choiceTextField = new JTextField(5);

        // Create button
        JButton enterButton = new JButton("Enter");

        // Add components to the panel
        panel.add(nameLabel);
        panel.add(nameTextField);

        panel.add(ageLabel);
        panel.add(ageTextField);

        panel.add(genderLabel);
        panel.add(genderTextField);

        panel.add(choiceLabel);
        panel.add(choiceTextField);

        panel.add(enterButton); // Add the button to the panel

        // Add the panel to the frame
        frame.add(panel);

        // Set up action listener for the button
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get user inputs
                String name = nameTextField.getText();
                String age = ageTextField.getText();
                String gender = genderTextField.getText();
                String choice = choiceTextField.getText();

                // Validate name and choice are not empty
                if (name.isEmpty() || choice.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter Name and Choice (1 or 2).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate choice is either "1" or "2"
                if (!choice.equals("1") && !choice.equals("2")) {
                    JOptionPane.showMessageDialog(frame, "Please enter either 1 or 2 for Choice.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generate filename based on name and choice
                String filename = name + "_group" + choice + ".txt";

                // Write user information to the file
                try {
                    Path filePath = Paths.get(filename);
                    Files.writeString(filePath, "Name: " + name + "\n");
                    Files.writeString(filePath, "Age: " + age + "\n");
                    Files.writeString(filePath, "Gender: " + gender + "\n");
                    Files.writeString(filePath, "Choice: " + choice + "\n");

                    // Display success message
                    JOptionPane.showMessageDialog(frame, "Data saved to " + filename + " successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Play MP4 file based on the user's choice
                    String folderPath = (choice.equals("1")) ? "1" : "2";
                    playRandomMP4FromFolder(folderPath);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error occurred while writing to file.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    // Method to play a random MP4 file from the specified folder
    private static void playRandomMP4FromFolder(String folderName) {
        JFileChooser fileChooser = new JFileChooser(folderName);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            // Initialize JavaFX platform for MediaPlayer
            JFXPanel fxPanel = new JFXPanel();
            javafx.application.Platform.runLater(() -> {
                Media media = new Media(new File(filePath).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.play();
                });
            });
        }
    }
}