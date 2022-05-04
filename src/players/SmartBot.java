package players;

import game.*;
import field.*;
import gamesave.*;
import rollresult.*;

import java.util.Random;
import java.util.Scanner;

/**
 * Egy speciális bot, amely egyedi algoritmussal dönt arról, hogy támad vagy átadja a kört.
 */
public class SmartBot extends Bot {

    public SmartBot(int id, DiceWars game) {
        super(id, game);
    }

    /**
     * A bot bármilyen lépés előtt 50%-os eséllyel azonnal átadja a kört.<br>
     * Amennyiben nem így döntött, az algoritmus kiválaszt egy véletlenszerű mezőt, amiről lehet támadni.<br>
     * Ezután kiválasztja a legkevesebb dobókockával rendelkező szomszédos támadható mezőt.<br>
     * Amennyiben ez sikeres volt megtámadja az adott mezőt, minden más esetben átadja a kört.
     * @param scanner Egy Scanner objektum, amelyen keresztül történik a kommunikáció a program és a játékos között
     * @return 0, ha átadta a kört, minden más esetben 1
     */
    @Override
    public int getInput(Scanner scanner) {
        Random r = new Random();

        systemOutAndRecord("");
        systemOutAndRecord("");
        systemOutAndRecord(">> " + getName() + " kovetkezik...");

        DiceWars.wait(r.nextInt(8) + 1);
        currGame.getWriter().add(GameWriter.wait, Integer.toString(r.nextInt(8) + 1));

        if(r.nextInt(2) == 0) {
            skip();
            return 0;
        }

        Field field = null;
        for (int i = r.nextInt(getPlayerFields().length); i < getPlayerFields().length; i++) {
            if(getPlayerFields()[i] == null) continue;

            if(getPlayerFields()[i].canAttackFromHere()) {
                field = getPlayerFields()[i];
                break;
            }
            if(i+1 == getPlayerFields().length) i = r.nextInt(getPlayerFields().length);
        }

        if(field != null) {
            Field[] defender = new Field[4];

            for (int i = 0; i < 4; i++) {
                if(field.getAttackableFields()[i] == null) continue;

                defender[i] = field.getAttackableFields()[i];
            }

            int min = Integer.MAX_VALUE;
            Field minField = null;
            for (int i = 0; i < 4; i++) {
                if(defender[i] == null) continue;

                if(defender[i].getDiceCount() < min) {
                    min = defender[i].getDiceCount();
                    minField = defender[i];
                }
            }

            if(minField != null) {
                RollResult result = attack(field, minField);
                currGame.handleAttack(result);
            }
        }
        if(r.nextInt(5) == 0) {
            return 1;
        }
        else {
            skip();
            return 0;
        }
    }

    /**
     * Ugyanaz, mint az ősosztálybeli skip() metódus, viszont itt a kiiratás más, ugyanis a botnál máshogy kell látszódnia a szövegnek.
     */
    @Override
    public void skip() {
        Random r = new Random();
        Field[] fields = getPlayerFields();
        int dices = getPlayerFieldLength() / 2;

        while (dices != 0) {
            int rand = r.nextInt(fields.length);

            if(fields[rand] == null) continue;

            if(fields[rand].getDiceCount() < 8) {
                int dice;
                while ((dice = r.nextInt(8 - fields[rand].getDiceCount()) + 1) > dices) { }

                fields[rand].addDiceCount(dice);
                dices -= dice;
            }
        }
        systemOutAndRecord(">> " + getName() + " atadta a kort a kovetkezo jatekosnak...");
    }

    /**
     * Megvalósítja a bot kilépését a játékból.
     * @param scanner Egy Scanner objektum, amin keresztül történik a kommunikáció a program és a játékos között
     */
    @Override
    public void quit(Scanner scanner) {
        systemOutAndRecord(">> " + getName() + " kiesett a jatekbol.");
        inGame = false;
    }
}
