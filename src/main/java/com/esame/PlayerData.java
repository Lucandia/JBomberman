package com.esame;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Questa classe rappresenta i dati di un giocatore.
 */
public class PlayerData {

    /**
     * Il nickname del giocatore.
     */
    private String nickname;

    /**
     * L'avatar del bomberman.
     */
    private String avatar;

    /**
     * L'ultimo livello completato dal giocatore.
     */
    private String lastLevel;

    /**
     * Il numero di partite giocate dal giocatore.
     */
    private String playedGames;

    /**
     * Il numero di partite vinte dal giocatore.
     */
    private String winGames;

    /**
     * Il numero di partite perse dal giocatore.
     */
    private String lostGames;

    /**
     * Il punteggio del giocatore.
     */
    private String score;

    /**
     * Costruttore della classe PlayerData.
     *
     * @param nickname     il nickname del giocatore
     * @param avatar       l'avatar del giocatore
     * @param lastLevel    l'ultimo livello completato dal giocatore
     * @param playedGames  il numero di partite giocate dal giocatore
     * @param winGames     il numero di partite vinte dal giocatore
     * @param lostGames    il numero di partite perse dal giocatore
     * @param score        il punteggio del giocatore
     */
    public PlayerData(String nickname, String avatar, String lastLevel, String playedGames, String winGames, String lostGames, String score) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.lastLevel = lastLevel;
        this.playedGames = playedGames;
        this.winGames = winGames;
        this.lostGames = lostGames;
        this.score = score;
    }

    /**
     * Restituisce il nickname del giocatore.
     *
     * @return il nickname del giocatore
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Imposta il nickname del giocatore.
     *
     * @param nickname il nuovo nickname del giocatore
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Restituisce l'avatar del giocatore.
     *
     * @return l'avatar del giocatore
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Imposta l'avatar del giocatore.
     *
     * @param avatar il nuovo avatar del giocatore
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Restituisce l'ultimo livello completato dal giocatore come stringa.
     *
     * @return l'ultimo livello completato dal giocatore come stringa
     */
    public String getLastLevel() {
        return lastLevel;
    }

    /**
     * Restituisce l'ultimo livello completato dal giocatore come intero.
     *
     * @return l'ultimo livello completato dal giocatore come intero
     */
    public int getLastLevelInt() {
        return Integer.parseInt(lastLevel);
    }

    /**
     * Imposta l'ultimo livello completato dal giocatore come intero.
     *
     * @param lastLevel il nuovo ultimo livello completato dal giocatore come intero
     */
    public void setLastLevel(int lastLevel) {
        this.lastLevel = Integer.toString(lastLevel);
    }

    /**
     * Restituisce il numero di partite giocate dal giocatore come stringa.
     *
     * @return il numero di partite giocate dal giocatore come stringa
     */
    public String getPlayedGames() {
        return playedGames;
    }

    /**
     * Restituisce il numero di partite giocate dal giocatore come intero.
     *
     * @return il numero di partite giocate dal giocatore come intero
     */
    public int getPlayedGamesInt() {
        return Integer.parseInt(playedGames);
    }

    /**
     * Imposta il numero di partite giocate dal giocatore come intero.
     *
     * @param playedGames il nuovo numero di partite giocate dal giocatore come intero
     */
    public void setPlayedGames(int playedGames) {
        this.playedGames = Integer.toString(playedGames);
    }

    /**
     * Imposta il numero di partite vinte dal giocatore come intero.
     *
     * @param winGames il nuovo numero di partite vinte dal giocatore come intero
     */
    public void setWinGames(int winGames) {
        this.winGames = Integer.toString(winGames);
    }

    /**
     * Restituisce il numero di partite vinte dal giocatore come intero.
     *
     * @return il numero di partite vinte dal giocatore come intero
     */
    public int getWinGamesInt() {
        return Integer.parseInt(winGames);
    }

    /**
     * Restituisce il numero di partite vinte dal giocatore come stringa.
     *
     * @return il numero di partite vinte dal giocatore come stringa
     */
    public String getWinGames() {
        return winGames;
    }

    /**
     * Imposta il numero di partite vinte dal giocatore come stringa.
     *
     * @param winGames il nuovo numero di partite vinte dal giocatore come stringa
     */
    public void setWinGames(String winGames) {
        this.winGames = winGames;
    }

    /**
     * Restituisce il numero di partite perse dal giocatore come stringa.
     *
     * @return il numero di partite perse dal giocatore come stringa
     */
    public String getLostGames() {
        return lostGames;
    }

    /**
     * Restituisce il numero di partite perse dal giocatore come intero.
     *
     * @return il numero di partite perse dal giocatore come intero
     */
    public int getLostGamesInt() {
        return Integer.parseInt(lostGames);
    }

    /**
     * Imposta il numero di partite perse dal giocatore come intero.
     *
     * @param lostGames il nuovo numero di partite perse dal giocatore come intero
     */
    public void setLostGames(int lostGames) {
        this.lostGames = Integer.toString(lostGames);
    }

    /**
     * Restituisce il punteggio del giocatore come stringa.
     *
     * @return il punteggio del giocatore come stringa
     */
    public String getScore() {
        return score;
    }

    /**
     * Imposta il punteggio del giocatore come intero.
     * Se il nuovo punteggio è maggiore del punteggio attuale, il punteggio viene aggiornato.
     *
     * @param newScore il nuovo punteggio del giocatore come intero
     */
    public void setScore(int newScore) {
        int currentScore = Integer.parseInt(getScore());
        if (currentScore < newScore) {
            this.score = Integer.toString(newScore);
        }
    }

    /**
     * Restituisce una stringa che rappresenta i dati del giocatore.
     *
     * @return una stringa che rappresenta i dati del giocatore
     */
    public String getDataString() {
        return nickname + "," + avatar + "," + lastLevel + "," + playedGames + "," + winGames + "," + lostGames + "," + score;
    }

   /**
     * Legge i dati dei giocatori dal file savedGames.txt e li memorizza in una mappa.
     * 
     * @return una mappa contenente i dati dei giocatori
     */
    public static Map<String, PlayerData> readPlayerData() {
        Map<String, PlayerData> playerDataMap = new HashMap<>();
        try {
            // Get the path to the JAR file
            String jarPath = new File(PlayerData.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
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
        return playerDataMap;
        } catch (URISyntaxException e) {
            System.err.println("Error parsing URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
        return playerDataMap;
    }

    /**
        * Salva i dati del giocatore nel file savedGames.txt.
        */
        public void savePlayerData() {
            try {
                // Get the path to the JAR file
                String jarPath = new File(PlayerData.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                // Get the directory of the JAR file
                String dirPath = new File(jarPath).getParent();
                // Construct the path to the players.txt file in the same directory
                File file = new File(dirPath, "savedGames.txt");
                // Prepare data to save
                String dataToSave = getDataString();
                // Check if data for the player already exists and needs to be updated, or append new data
                List<String> lines = file.exists() ? Files.readAllLines(file.toPath()) : new ArrayList<>();
                lines.removeIf(String::isEmpty); // Remove any empty lines
                boolean dataExists = false;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    String nickname = line.split(",")[0];
                    if (nickname.equals(getNickname())) {
                        lines.set(i, dataToSave); // Update existing data
                        dataExists = true;
                        break;
                    }
                }
                if (!dataExists && !dataToSave.isEmpty()) {
                    lines.add(dataToSave); // Append new player data
                }
                // Write data to the file
                Files.write(file.toPath(), lines);
            } catch (URISyntaxException e) {
                System.err.println("Error parsing URI: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("IO error occurred: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
}
