package players;

import game.*;
import field.*;
import gamesave.*;
import rollresult.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Egy speciális játékos, ami a gépi ellenfelet valósítja meg a gyerekosztályaival együtt.
 */
public class Bot extends Player {
    public static List<String> usedUsernames = new ArrayList<String>();

    public void setName(String name) { this.name = name; }

    public Bot(int id, DiceWars currGame) {
        super(id, currGame);
        setName(generateRandomBotname());
    }

    /**
     * Generál egy véletlenszerű nevet egy botnak. Figyelembe veszi a már generált neveket és azokat nem adja vissza újra.
     * @return Egy véletlenszerű név
     */
    private String generateRandomBotname() {
        String[] botNames = { "Albert", "Allen", "Bert", "Bob", "Cecil", "Clarence",
                "Elliot", "Elmer", "Ernie", "Eugene", "Fergus", "Ferris", "Frank",
                "George", "Stan", "Stoki", "Matyi", "Szifon", "Baki", "Gyuri",
                "Attila", "Márk", "Mira", "Janka", "Endre" };

        Random r = new Random();
        int c = r.nextInt(botNames.length);

        while(usedUsernames != null && usedUsernames.contains(botNames[c])) { c = r.nextInt(botNames.length); }
        usedUsernames.add(botNames[c]);
        return "BOT " + botNames[c];
    }
}
