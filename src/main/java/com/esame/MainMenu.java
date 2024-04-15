package com.esame;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

/**
 * La classe MainMenu rappresenta il menu principale del gioco JBomberman.
 * Questa classe estende la classe Application di JavaFX e gestisce la visualizzazione
 * e l'interazione con gli elementi del menu principale.
 */
public class MainMenu extends Application {

    /**
     * Mappa dei dati dei giocatori.
     */
    private Map<String, PlayerData> playerDataMap = new HashMap<>();

    /**
     * L'anteprima dell'avatar selezionato.
     */
    private ImageView avatarPreview = new ImageView();

    /**
     * Etichetta per visualizzare le statistiche del giocatore.
     */
    private Label statsLabel1 = new Label(); 

    /**
     * Etichetta per visualizzare le statistiche del giocatore.
     */
    private Label statsLabel2 = new Label(); 

    /**
     * Avvia l'applicazione del menu principale.
     *
     * @param primaryStage lo stage principale dell'applicazione
     * @throws Exception se si verifica un'eccezione durante l'avvio dell'applicazione
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // fai partire la musica
        AudioUtils.preloadAll(); // Preload all sound effects
        BackgroundMusic backgroundMusic = new BackgroundMusic();
        backgroundMusic.playMusic("MainMenu.mp3");

        // Inizializza lo stage
        Font.loadFont(getClass().getResourceAsStream("/fonts/Pixelify_Sans/static/PixelifySans-Regular.ttf"), 14);
        primaryStage.setTitle("JBomberman");
        playerDataMap = PlayerData.readPlayerData(); // Read player data from file
        TextField nicknameField = new TextField();
        nicknameField.setPrefWidth(10);

        // Number of enemies
        // Spinner for selecting the number of enemies
        Spinner<Integer> enemySpinner = new Spinner<>(1, 30, 4);
        enemySpinner.setEditable(true);

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

        // Select Avatar
        ComboBox<String> avatarComboBox = new ComboBox<>();
        avatarComboBox.getItems().addAll("1", "2", "3", "4"); // Assuming these are your avatar options
        String defaultAvatar = "1"; // Default avatar
        // prova a caricare l'ultimo avatar usato
        if (playerDataMap.containsKey(nicknameField.getText())) {
            defaultAvatar = playerDataMap.get(nicknameField.getText()).getAvatar();
        }
        avatarComboBox.setValue(defaultAvatar);
        updateAvatarPreview(defaultAvatar); // Update the avatar preview based on the default value (1)
        avatarComboBox.setOnAction(e -> updateAvatarPreview(avatarComboBox.getValue()));

        // Start Game Button
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> {
            String nickname = nicknameField.getText();
            String avatar = avatarComboBox.getValue();
            PlayerData data = playerDataMap.getOrDefault(nickname, new PlayerData(nickname, avatar, "1", "0", "0", "0", "0")); 
            data.setAvatar(avatar); // Update the avatar. COMMENT THIS LINE TO KEEP THE SAME AVATAR FOR THE PLAYER
            try {
                GameApp gameApp = new GameApp();
                backgroundMusic.stopMusic();
                gameApp.initializeGame(data, enemySpinner.getValue());
                gameApp.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            primaryStage.close(); // Close the setup window
        });

        // Setup the layout
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

    /**
     * Aggiorna l'anteprima dell'avatar con l'immagine corrispondente al numero dell'avatar specificato.
     *
     * @param avatarNumber il numero dell'avatar da visualizzare
     */
    private void updateAvatarPreview(String avatarNumber) {
        Image avatarImage = new Image(getClass().getResourceAsStream("/sprites/bomberman.png"));
        int avatarIndex = Integer.parseInt(avatarNumber) - 1; // Assuming avatarNumber starts at 1
        avatarPreview.setImage(avatarImage); // avatarPreview should be an ImageView
        avatarPreview.setViewport(new Rectangle2D(0, 24 * avatarIndex, 47, 24)); // Update this to match your sprite sheet
    }

    /**
     * Questo metodo è il punto di ingresso principale dell'applicazione.
     *
     * @param args gli argomenti della riga di comando
     */
    public static void launchMainMenu(String[] args) {
        launch(args);
    }

    /**
     * Mostra la scena del menu principale.
     *
     * @param stage lo stage su cui mostrare la scena
     * @throws Exception se si verifica un'eccezione durante la visualizzazione della scena
     */
    public void show(Stage stage) throws Exception {
        // Setup your pre-game scene
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
}
