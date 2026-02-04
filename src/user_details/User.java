package user_details;

import errors.InvalidDataInput;
import game_logic.Game;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private List<Game> userActiveGames;
    private List<Integer> userActiveGamesIds;
    private int points;

    public User() {
        this.userActiveGames = new ArrayList<>();
        this.userActiveGamesIds = new ArrayList<>();
        this.points = 0;
    }

    public User(String email, String password) {
        setEmail(email);
        setPassword(password);
        this.userActiveGames = new ArrayList<>();
        this.points = 0;
        this.userActiveGames = new ArrayList<>();
        this.userActiveGamesIds = new ArrayList<>();
    }
    public void addGame(Game game) {
        if (!this.userActiveGames.contains(game.getId())) {
            this.userActiveGames.add(game);
            this.userActiveGamesIds.add(game.getId());
        }
    }
    public void removeGame(Game game) throws InvalidDataInput {
        if (!this.userActiveGames.remove(game)){
            throw new InvalidDataInput("Invalid Game");
        };
        this.userActiveGamesIds.remove(Integer.valueOf(game.getId()));
    }
    public List<Game> getActiveGames() {
        return this.userActiveGames;
    }
    public List<Integer> getActiveGamesIds() {
        return this.userActiveGamesIds;
    }
    public int getPoints() {
        return this.points;
    }
    public void setPoints(int points) {
        if (points < 0) {
            this.points = 0;
            return;
        }
        this.points = points;
        return;
    }
    public boolean setEmail(String email) {
        if (email == null) {
            return false;
        }
        this.email = email;
        return true;
    }
    public String getEmail() {
        return this.email;
    }
    public boolean setPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        this.password = password;
        return true;
    }
    public String getPassword() {
        return this.password;
    }
    public boolean verifyPasspord(String password) {
        if (password != null && this.password.equals(password)){
            return true;
        }
        return false;
    }
    public boolean setUserActiveGamesIds(List<Integer> userActiveGamesId) {
        this.userActiveGamesIds = new ArrayList<>(userActiveGamesId);
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User secondUser = (User) o;
        return this.email.equals(secondUser.email);
    }
    public int hashCode() {
        return email.hashCode();
    }
    public String toString() {
        return "User{"+"email=\""+this.email+"\""+", points="+this.points+", activeGames="+
                userActiveGamesIds.size()+"}";
    }

}
