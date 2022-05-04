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
public class AttackerBot extends Bot {

    public AttackerBot(int id, DiceWars game) {
        super(id, game);
    }

    /**
     * A bot mindig támad, ha talál olyan mezőt, amiről tud.<br>
     * Sikeres támadást követően 20% eséllyel átadja a kört, egyébként folytatja a támadást.
     * @param scanner Egy Scanner objektum, amelyen keresztül történik a kommunikáció a program és a játékos között
     * @return 0, ha átadja a kört, minden más esetben 1
     */
    @Override
    public int getInput(Scanner scanner) {
        Random r = new Random();

        systemOutAndRecord("");
        systemOutAndRecord("");
        systemOutAndRecord(">> " + getName() + " kovetkezik...");

        DiceWars.wait(r.nextInt(8) + 1);
        currGame.getWriter().add(GameWriter.wait, Integer.toString(r.nextInt(8) + 1));

        Field field = null;
        for (int i = 0; i < getPlayerFields().length; i++) {
            if(getPlayerFields()[i] == null) continue;

            if(getPlayerFields()[i].canAttackFromHere()) {
                field = getPlayerFields()[i];
                break;
            }
        }

        if(field != null) {
            Field defender = null;

            while(defender == null) {
                defender = field.getAttackableFields()[r.nextInt(4)];
            }

            RollResult result = attack(field, defender);
            currGame.handleAttack(result);
        }
        if(r.nextInt(5) == 0) {
            skip();
            return 0;
        } else return 1;
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
