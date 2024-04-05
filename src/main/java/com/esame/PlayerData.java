package com.esame;

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
}
