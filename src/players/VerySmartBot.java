package players;

import game.*;
import field.*;
import gamesave.*;
import rollresult.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Egy speciális bot, amely egyedi algoritmussal dönt arról, hogy támad vagy átadja a kört.
 */
public class VerySmartBot extends Bot {

    public VerySmartBot(int id, DiceWars game) {
        super(id, game);
    }

    /**
     * Az algoritmus először egy külön leképezést hoz létre a támadható mezők és a védő mezők között.<br>
     * Ezután a támadható mezők közül kiválasztja azt, amelyiken a legkevesebb dobókocka van.<br>
     * Amennyiben ez a kiválasztás nem volt sikeres, az algoritmus megáll, minden más esetben folytatódik.<br>
     * Ezután elvégez egy tesztet, amelyben megnézi, hogy 5 támadás esetén hányszor győzne Ő.<br>
     * Amennyiben ez a szám nagyobb vagy egyenlő, mint 3, a bot megtámadja a mezőt, egyébként átadja a kört.
     * @param scanner Egy Scanner objektum, amelyen keresztül történik a kommunikáció a program és a játékos között
     * @return 0, ha átadta a kört, minden más esetben 1
     */
    @Override
    public int getInput(Scanner scanner) {
        systemOutAndRecord("");
        systemOutAndRecord("");
        systemOutAndRecord(">> " + getName() + " kovetkezik...");
        Random r = new Random();
        Map<Field, Field> map = new HashMap<>();

        DiceWars.wait(r.nextInt(8) + 1);
        currGame.getWriter().add(GameWriter.wait, Integer.toString(r.nextInt(8) + 1));
        for (Field attackField : getPlayerFields()) {
            if(attackField == null || attackField.getAttackableFields() == null) continue;
            for (Field defField : attackField.getAttackableFields()) {
                if(defField == null) continue;
                map.put(attackField, defField);
            }
        }

        int min_val = Integer.MAX_VALUE, skipAfterAttack = 0;
        Field minField = null, minAttackerField = null;

        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry elem = (Entry)iterator.next();

            Field temp_field = (Field)elem.getValue();
            if(min_val > temp_field.getDiceCount()) {
                min_val = temp_field.getDiceCount();
                minField = temp_field;
                minAttackerField = (Field)elem.getKey();
            }
        }

        if(minField != null && minAttackerField != null) {
            int[][] result = new int[2][5];
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < minField.getDiceCount(); k++) result[0][j] += roll();
                for (int k = 0; k < minAttackerField.getDiceCount(); k++) result[1][j] += roll();
            }
            int c = 0;
            for (int i = 0; i < 5; i++) if(result[1][i] > result[0][i]) c++;

            if(c >= 3) {
                RollResult res = attack(minAttackerField, minField);
                if(res.getWinnerPlayer() == this) skipAfterAttack = 1;
                currGame.handleAttack(res);
            } else {
                skip();
                return 0;
            }
        } else {
            skip();
            return 0;
        }
        return skipAfterAttack;
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
