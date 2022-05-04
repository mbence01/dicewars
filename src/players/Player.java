package players;

import game.*;
import field.*;
import gamesave.*;
import rollresult.*;

import java.util.Random;
import java.util.Scanner;

/**
 * A játékban található játékosokat és annak funkcióit hivatott ellátni az osztály.<br>
 * Van lehetőség a támadás, kör átadás, kilépés és a kockával dobás meghívására.<br>
 * Tartalmaz különböző metódusokat, illetve függvényeket, amelyekkel a játékos területeiről szerezhetünk információkat.
 */
public class Player {
    protected int id;
    protected String name;

    protected boolean inGame;

    protected DiceWars currGame;

    public int getId() { return id; }
    public String getName() { return name; }
    public DiceWars getCurrGame() { return currGame; }
    public boolean isInGame() { return inGame; }

    public void setName(String name) { this.name = name; }

    public Player(int id, DiceWars currGame) {
        this.id = id;
        this.currGame = currGame;

        inGame = true;
        name = "Jatekos";
    }

    /**
     * Megadja, hogy a játékosnak van-e olyan területe, amiről tud támadni.
     * @return Igaz, ha van támadó mező, minden más esetben hamis
     */
    public boolean hasAttackerField() {
        for (int i = 0; i < getPlayerFields().length; i++) {
            if(getPlayerFields()[i] != null) return true;
        }
        return false;
    }

    /**
     * Megszámolja, majd visszaadja a játékos területeinek számát.
     * @return A játékos területeinek száma
     */
    public int getPlayerFieldLength() {
        int c = 0;
        Field[] fields = getPlayerFields();
        for (int i = 0; i < fields.length; i++) {
            if(fields[i] == null) continue;

            c++;
        }
        return c;
    }

    /**
     * Végigiterál a térképen, majd megadja az adott játékos területeit.
     * @return Egy 1 dimenziós tömb, amely tartalmazza a játékos területeit
     */
    public Field[] getPlayerFields() {
        Field[][] table = currGame.getTable();
        Field[] fields = new Field[currGame.getTable().length * currGame.getTable()[0].length];

        int c = 0;
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                if(table[i][j].getOwner() == this) fields[c++] = table[i][j];
            }
        }
        return fields;
    }

    /**
     * Végigiterál a játékos területein, majd megszámolja a dobokockái számát.
     * @return Egy szám, amely a játékos dobokockáinak száma
     */
    public int getDiceCount() {
        Field[] fields = getPlayerFields();

        int c = 0;
        for (Field f : fields) {
            if(f == null) continue;
            c += f.getDiceCount();
        }
        return c;
    }

    /**
     * Kiírja a játékosnak a támadó mezőit és megkérdezi, hogy melyikről szeretne támadni.
     * @return Egy 1 dimenziós tömb, amely tartalmazza az elfogadható mező sorszámokat vagy ha nem tud támadni, akkor null
     */
    public int[] showAttackerFields() {
        if(!hasAttackerField()) {
            System.out.println(">> Nem tudsz tamadni, mert nincs 2-nel tobb dobokockaval rendelkezo mezod!");
            return null;
        }

        boolean canattack = false;
        for (int i = 0; i < getPlayerFields().length; i++) {
            if(getPlayerFields()[i] == null) continue;
            if(getPlayerFields()[i].canAttackFromHere()) canattack = true;
        }

        if(canattack) {
            System.out.println(">> Melyik mezorol szeretnel tamadni?");

            Field[] fields = getPlayerFields();

            int[] correct_indexes = new int[fields.length];
            int c = 0;

            for (int i = 0; i < fields.length; i++) {
                if(fields[i] == null || !fields[i].canAttackFromHere()) continue;

                correct_indexes[c++] = i + 1;
                System.out.print((i+1) + ": " + fields[i].toString());
                if (i + 1 != fields.length) System.out.print(" ");
            }
            System.out.println("");
            return correct_indexes;
        } else System.out.println(">> Nem tudsz tamadni, mert nincs 2-nel tobb dobokockaval rendelkezo mezod!");
        return null;
    }

    /**
     * Kiírja a játékosnak az adott mezőről támadható mezőket és megkérdezi, hogy melyikre szeretne támadni.
     * @param field A támadó mező, ahonnan indítja a támadást
     * @return Egy 1 dimenziós tömb, amely tartalmazza az elfogadható mezősorszámokat vagy ha nem tud támadni a mezőről, akkor null
     */
    public int[] showDefenderFields(Field field) {
        if(field.getAttackableFields() == null) {
            System.out.println("Errol a mezorol nem tudsz tamadni! Kerlek valassz masikat!");
            currGame.getAttackerField();
        } else {
            System.out.println(">> Melyik mezore szeretnel tamadni?");

            Field[] fields = field.getAttackableFields();
            int[] correct_indexes = new int[fields.length];
            int c = 0;

            for (int i = 0; i < fields.length; i++) {
                if(fields[i] == null) continue;

                correct_indexes[c++] = i + 1;
                System.out.print((i + 1) + ": " + fields[i].toString());
                if (i + 1 != fields.length) System.out.print(" ");
            }
            return correct_indexes;
        }
        return null;
    }

     /**
     * Megkérdezi a játékost, hogy támadni akar vagy átadja a kört.<br>Hibás input esetén addig kérdezi, amíg nem helyes az input.
     * @param scanner Egy Scanner objektum, amelyen keresztül történik a kommunikáció a program és a játékos között
     * @return 0, ha átadta a kört, egyébként 1
     */
     public int getInput(Scanner scanner) {
        System.out.println("\n\n>> Megtamadhatsz egy mezot vagy atadhatod a kort...");

        String str = "";
        boolean skip = false;
        do {
            System.out.print(">> Valassz a lehetosegek kozul: 'tamadas', 'atadas' > ");
            str = scanner.nextLine();
        } while(!str.equals("tamadas") && !str.equals("atadas"));

        if(str.equals("tamadas")) {
            Field attacker_field = currGame.getAttackerField();

            if(attacker_field != null) {
                Field defender_field = currGame.getDefenderField(attacker_field);

                RollResult result = attack(attacker_field, defender_field);
                currGame.handleAttack(result);
            } else {
                System.out.println("Nem tudsz tamadni, igy a kor atadasra kerul!");
                skip = true;
                skip();
            }
        }
        else {
            skip();
        }
        return (str.equals("atadas") || skip) ? 0 : 1;
    }

    /**
     * Megvalósítja a játékos támadását.<br>
     * Kiírja az üzeneteket, megvalósítja a dobásokat, valamint ebben a metódusban történik a kör kiértékelése.
     * @param attacker Mező, ahonnan a támadást indították
     * @param defender Mező, amit megtámadtak
     * @return Egy RollResult objektum, amely tartalmaz minden információt a dobások részleteiről
     */
    public RollResult attack(Field attacker, Field defender) {
        systemOutAndRecord("");
        systemOutAndRecord("");
        systemPrintAndRecord(">> Tamadas a " + attacker.toString() + " mezorol a " + defender.toString() + " mezore");

        for (int i = 0; i < 3; i++) {
            systemPrintAndRecord(".");
            DiceWars.wait(1);
            currGame.getWriter().add(GameWriter.wait, Integer.toString(1));
        }
        systemOutAndRecord("");

        int n = (attacker.getDiceCount() > defender.getDiceCount()) ? attacker.getDiceCount() : defender.getDiceCount();

        RollResult rr = new RollResult(n);
        rr.setAttackerField(attacker);
        rr.setDefenderField(defender);

        for (int i = 0; i < n; i++) {
            int roll1 = attacker.getOwner().roll(), roll2 = defender.getOwner().roll();

            if(attacker.getDiceCount() <= i) roll1 = 0;
            if(defender.getDiceCount() <= i) roll2 = 0;

            rr.addRolls(RollResult.attacker, roll1);
            rr.addRolls(RollResult.defender, roll2);

            systemOutAndRecord( attacker.getOwner().getName() +
                                " dobasa: " +
                                ((roll1 == 0) ? "-" : roll1) +
                                "\t>> | <<\t" +
                                defender.getOwner().getName() +
                                " dobasa: " +
                                ((roll2 == 0) ? "-" : roll2));
            DiceWars.wait(1);
            currGame.getWriter().add(GameWriter.wait, Integer.toString(1));
        }
        return rr;
    }

    /**
     * Megvalósítja az körátadás opciót.
     */
    public void skip() {
        Random r = new Random();
        Field[] fields = getPlayerFields();
        int dices = getPlayerFieldLength() / 2;

        systemOutAndRecord(">> Atadtad a kort a kovetkezo jatekosnak...");
        systemOutAndRecord(">> " + dices + " db dobokocka szetosztva a teruleteid kozott.");

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
        DiceWars.wait(2);
        currGame.getWriter().add(GameWriter.wait, Integer.toString(2));
    }

    /**
     * Ez a metódus hívódik meg, amikor a játékos kiesik a játékból.<br>
     * @param scanner Egy Scanner objektum, amin keresztül történik a kommunikáció a program és a játékos között
     */
    public void quit(Scanner scanner) {
        inGame = false;

        systemOutAndRecord(">> Sajnos elfogytak a teruleteid, igy kiestel a jatekbol!");

		DiceWars.wait(2);
		currGame.getWriter().add(GameWriter.wait, Integer.toString(2));

		currGame.gameOver();
		systemOutAndRecord("<<<<< SZIA! >>>>>");
		System.exit(0);
    }

    /**
     * Generál egy véletlenszerű számot 1 és 6 között, amely egy dobást szimulál.
     * @return A dobott véletlenszerű érték
     */
    protected int roll() {
        Random r = new Random();
        return r.nextInt(6) + 1;
    }

    /**
     * Lényegében egy System.out.println metódus, viszont amennyiben ez a metódus kerül meghívásra,<br>
     * a kapott üzenet rögzítésre kerül egy GameWriter objektumba, amely a játékmentésért felelős.
     * @param text A kiírandó, valamint a rögzítendő üzenet
     */
    protected void systemOutAndRecord(String text) {
        currGame.getWriter().add(GameWriter.out, text);
        System.out.println(text);
    }

    /**
     * Lényegében egy System.out.print metódus, viszont amennyiben ez a metódus kerül meghívásra,<br>
     * a kapott üzenet rögzítésre kerül egy GameWriter objektumba, amely a játékmentésért felelős.<br>
     * A metódus nem tesz automatikusan sorvége jelet a szöveg végére.
     * @param text A kiírandó, valamint a rögzítendő üzenet
     */
    protected void systemPrintAndRecord(String text) {
        currGame.getWriter().add(GameWriter.print, text);
        System.out.print(text);
    }
}
