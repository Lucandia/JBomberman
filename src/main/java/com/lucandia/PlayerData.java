package com.lucandia;
public class PlayerData {
    private String nickname;
    private String avatar;
    private String lastLevel;
    private String playedGames;
    private String winGames;
    private String lostGames;
    private String score;

    public PlayerData(String nickname, String avatar, String lastLevel, String playedGames, String winGames, String lostGames, String score) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.lastLevel = lastLevel;
        this.playedGames = playedGames;
        this.winGames = winGames;
        this.lostGames = lostGames;
        this.score = score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastLevel() {
        return lastLevel;
    }

    public int getLastLevelInt() {
        return Integer.parseInt(lastLevel);
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = Integer.toString(lastLevel);
    }

    public String getPlayedGames() {
        return playedGames;
    }

    public int getPlayedGamesInt() {
        return Integer.parseInt(playedGames);
    }

    public void setPlayedGames(int playedGames) {
        this.playedGames = Integer.toString(playedGames);
    }

    public void setWinGames(int winGames) {
        this.winGames = Integer.toString(winGames);
    }

    public int getWinGamesInt() {
        return Integer.parseInt(winGames);
    }

    public String getWinGames() {
        return winGames;
    }

    public void setWinGames(String winGames) {
        this.winGames = winGames;
    }

    public String getLostGames() {
        return lostGames;
    } 

    public int getLostGamesInt() {
        return Integer.parseInt(lostGames);
    }

    public void setLostGames(int lostGames) {
        this.lostGames = Integer.toString(lostGames);
    }

    public String getScore() {
        return score;
    }

    public void setScore(int newScore) {
        int currentScore = Integer.parseInt(getScore());
        if (currentScore < newScore) {
            this.score = Integer.toString(newScore);
        }
    }

    public String getDataString() {
        return nickname + "," + avatar + "," + lastLevel + "," + playedGames + "," + winGames + "," + lostGames + "," + score;
    }


}
