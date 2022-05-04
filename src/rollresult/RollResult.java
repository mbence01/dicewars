package rollresult;

import game.*;
import field.*;
import gamesave.*;
import players.*;

/**
 * Az osztály felelős egy támadás minden információjának rögzítésére.<br>
 * Tárolja a nyertes-, vesztes- mezőt, játékost, a támadó-, védő- mezőt, játékost.<br>
 * Külön tárolja az összes dobást, valamint összesítve is el van tárolva.<br>
 * Az osztály tartalmaz egy olyan metódust, amely kiszámolja a végeredményt.
 */
public class RollResult {
    private Player attackerPlayer;
    private Player defenderPlayer;

    private Field attackerField;
    private Field defenderField;

    private int[] rolls_attacker;
    private int[] rolls_defender;

    private Field winner_field;
    private Field loser_field;

    private Player winner_player;
    private Player loser_player;

    private int final_attacker;
    private int final_defender;

    private int result;

    private int[] rolls_counter = new int[2];

    public static final int attacker = 0;
    public static final int defender = 1;

    public static final int attacker_win = 0;
    public static final int defender_win = 1;
    public static final int draw = 2;

    public void setAttackerField(Field field) {
        attackerField = field;
        attackerPlayer = field.getOwner();
    }

    public void setDefenderField(Field field) {
        defenderField = field;
        defenderPlayer = field.getOwner();
    }

    public int getResult() { return result; }

    public Player getWinnerPlayer() { return winner_player; }
    public Player getLoserPlayer() { return loser_player; }

    public Field getWinnerField() { return winner_field; }
    public Field getLoserField() { return loser_field; }

    public Player getAttackerPlayer() { return attackerPlayer; }
    public Player getDefenderPlayer() { return defenderPlayer; }

    public Field getAttackerField() { return attackerField; }
    public Field getDefenderField() { return defenderField; }

    public int getAttackerResult() { return final_attacker; }
    public int getDefenderResult() { return final_defender; }

    public int[] getRollsAttacker() { return rolls_attacker; }
    public int[] getRollsDefender() { return rolls_defender; }

    public int getWinnerResult() { return (final_attacker > final_defender) ? final_attacker : final_defender; }
    public int getLoserResult() { return (final_attacker > final_defender) ? final_defender : final_attacker; }

    /**
     * Rögzít egy új dobást a megfelelő változóban attól függően, hogy a támadó vagy a védekező dobott.
     * @param which 0, ha támadó, 1, ha védekező (használhatóak a RollResult.attacker és a RollResult.defender konstansok)
     * @param result A dobott érték
     */
    public void addRolls(int which, int result) {
        if(which == attacker) rolls_attacker[rolls_counter[attacker]] = result;
        else rolls_defender[rolls_counter[defender]] = result;

        rolls_counter[which]++;
    }

    public RollResult(int max_roll) {
        rolls_attacker = new int[max_roll];
        rolls_defender = new int[max_roll];
    }

    /**
     * Összesíti az eredményeket. Kiszámolja a dobott értékek alapján az összesített dobásokat.<br>
     * Meghatározza a nyertes és vesztes mezőket, valamint játékosokat.
     */
    public void calculateResult() {
        for (int i = 0; i < rolls_attacker.length; i++) {
            final_attacker += rolls_attacker[i];
            final_defender += rolls_defender[i];
        }

        if(final_attacker > final_defender) {
            result = attacker_win;

            winner_field = attackerField;
            winner_player = attackerPlayer;

            loser_field = defenderField;
            loser_player = defenderPlayer;
        } else if(final_defender > final_attacker) {
            result = defender_win;

            winner_field = defenderField;
            winner_player = defenderPlayer;

            loser_field = attackerField;
            loser_player = attackerPlayer;
        } else {
            result = draw;

            winner_field = defenderField;
            winner_player = defenderPlayer;

            loser_field = attackerField;
            loser_player = attackerPlayer;
        }
    }
}
