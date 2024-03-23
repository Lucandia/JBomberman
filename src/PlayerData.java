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

    public void setLastLevel(String lastLevel) {
        this.lastLevel = lastLevel;
    }

    public String getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(String playedGames) {
        this.playedGames = playedGames;
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

    public void setLostGames(String lostGames) {
        this.lostGames = lostGames;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDataString() {
        return nickname + "," + avatar + "," + lastLevel + "," + playedGames + "," + winGames + "," + lostGames + "," + score + "\n";
    }


}
