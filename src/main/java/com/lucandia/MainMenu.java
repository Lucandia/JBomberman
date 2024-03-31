package com.lucandia;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMenu extends Application {
    private Map<String, PlayerData> playerDataMap = new HashMap<>();
    private ImageView avatarPreview = new ImageView();
    private Label statsLabel1 = new Label(); // Label to display player stats
    private Label statsLabel2 = new Label(); // Label to display player stats

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Pixelify_Sans/static/PixelifySans-Regular.ttf"), 14);
        readPlayerData(); // Read player data from file
        TextField nicknameField = new TextField();
        nicknameField.setPrefWidth(10);

        // Number of enemies
        // Spinner for selecting the number of enemies
        Spinner<Integer> enemySpinner = new Spinner<>(1, 30, 4);
        enemySpinner.setEditable(true);

        // Select Avatar
        ComboBox<String> avatarComboBox = new ComboBox<>();
        avatarComboBox.getItems().addAll("1", "2", "3", "4"); // Assuming these are your avatar options
        avatarComboBox.setValue("1"); // Default to the first avatar
        updateAvatarPreview("1"); // Update the avatar preview based on the default value (1)
        avatarComboBox.setOnAction(e -> updateAvatarPreview(avatarComboBox.getValue()));

        // Listen for changes in the nickname field and update stats
        nicknameField.textProperty().addListener((observable, oldValue, newValue) -> {
            PlayerData data = playerDataMap.get(newValue);
            if (data != null) {
                // Update stats label with player data
                statsLabel1.setText(String.format("Played Games: %s; Won: %s; Lost: %s;", data.getPlayedGames(), data.getWinGames(), data.getLostGames()));
                statsLabel2.setText(String.format("Last Level: %s; Highest Score: %s", data.getLastLevel(), data.getScore()));
            } else {
                statsLabel1.setText("New player!");
                statsLabel2.setText("");
            }
        });

        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> {
            String nickname = nicknameField.getText();
            String avatar = avatarComboBox.getValue();
            PlayerData data = playerDataMap.getOrDefault(nickname, new PlayerData(nickname, avatar, "1", "0", "0", "0", "0")); 
            try {
                GameApp gameApp = new GameApp();
                gameApp.initializeGame(data, enemySpinner.getValue());
                gameApp.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            primaryStage.close(); // Close the setup window
        });

        VBox layout = new VBox(10, new Label("Nickname:"), nicknameField, 
                                new Label("Number of Enemies:"), enemySpinner, 
                                new Label("Choose Avatar:"), avatarComboBox, avatarPreview, 
                                statsLabel1, statsLabel2, 
                                startGameButton);
        Scene scene = new Scene(layout, 272, 310);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void readPlayerData() {
        // List<PlayerData> playerDataList = new ArrayList<>();
        try {
            // Get the path to the JAR file
            String jarPath = new File(MainMenu.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            // Get the directory of the JAR file
            String dirPath = new File(jarPath).getParent();
            // Construct the path to the players.txt file in the same directory
            File file = new File(dirPath, "savedGames.txt");

            if (file.exists()) {
                // Read all lines from the players.txt file
                List<String> lines = Files.readAllLines(file.toPath());
                // Parse each line into PlayerData objects
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        String nickname = parts[0];
                        String avatarNumber = parts[1];
                        String lastLevel = parts[2];
                        String playedGames = parts[3];
                        String winGames = parts[4];
                        String lostGames = parts[5];
                        String score = parts[6];
                        PlayerData playerData = new PlayerData(nickname, avatarNumber, lastLevel, playedGames, winGames, lostGames, score);
                        playerDataMap.put(nickname, playerData);
                    }
                }
            }
        } catch (URISyntaxException e) {
            System.err.println("Error parsing URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void updateAvatarPreview(String avatarNumber) {
        Image avatarImage = new Image(getClass().getResourceAsStream("/sprites/bomberman.png"));
        int avatarIndex = Integer.parseInt(avatarNumber) - 1; // Assuming avatarNumber starts at 1
        avatarPreview.setImage(avatarImage); // avatarPreview should be an ImageView
        avatarPreview.setViewport(new Rectangle2D(0, 24 * avatarIndex, 47, 24)); // Update this to match your sprite sheet
    }

    public static void main(String[] args) {
        launch(args);
    }

    // This method sets up and shows the pre-game scene
    public void show(Stage stage) throws Exception {
        // Setup your pre-game scene
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
}
