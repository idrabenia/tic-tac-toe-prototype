package idrabenia.domain;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class Player {
    private final String name;
    private volatile Game game;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
