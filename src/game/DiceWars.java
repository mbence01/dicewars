package game;

import field.*;
import gamesave.*;
import players.*;
import rollresult.*;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Az egész játék főosztálya.<br>
 * A legtöbb kezelő műveletet ez az osztály végzi, illetve ez értelmezi a többi osztályból visszajövő adatot.
 */
public class DiceWars {
    protected Player player;

    protected int botCount;

    protected Player[] players;

    protected Field[][] table;

    protected static Scanner scanner;
    protected static Random rand;
    protected GameWriter gw;

    protected int fieldPerPlayer;

    protected Player currentPlayer;

    protected int roundCount;

    public int getFieldPerPlayer() { return fieldPerPlayer; }
    public Field[][] getTable() { return table; }
    public GameWriter getWriter() { return gw; }

    public DiceWars(int fieldPerPlayer) {
        scanner = new Scanner(System.in);
        rand = new Random();
        this.fieldPerPlayer = fieldPerPlayer;

        botCount = 0;
        roundCount = 0;

        players = new Player[100];

        gw = new GameWriter();
        gw.start();
    }

    /**
     * Elindítja az egész játékot.<br>
     * Ez a metódus hívja meg a többi metódust, amellyel a játékostól kérhetünk be és írhatunk ki adatokat.
     */
    public void start() {
        welcomePlayer();

        if(getMethod() == 1) { // jatszani akar
            getBots();
            createPlayers();

            initializeTable();

            gameLoop();
        } else { // jatekmenet betoltes
            getFilePath();
        }

        scanner.close();
    }

    /**
     * Üdvözli a játékost és kiírja a játék nevét.
     */
    protected void welcomePlayer() {
        systemOutAndRecord("<<<<<<< DICE WARS >>>>>>>");
        systemOutAndRecord("Udv a Dice Wars jatekban!");
        systemOutAndRecord("<<<<<<< DICE WARS >>>>>>>");
    }

    /**
     * Amennyiben a játékos a játékmenet betöltést választotta, ez a metódus kéri be a mentés fájl nevét.<br>
     * Ha a fájl létezik, elindul a mentés visszajátszása.
     */
    protected void getFilePath() {
        System.out.print(">> Add meg a mentes fajl eleresi utvonalat: ");
        String str = scanner.nextLine();

        GameReader gr = new GameReader(str);

        gr.start();
    }

    /**
     * Megkérdezi a játékostól, hogy új játékot akar kezdeni, vagy betöltene a játékmenetet.
     * @return 1, ha új játékot kezdene, 0, ha betöltene egy játékmenetet
     */
    protected int getMethod() {
        int chosen = 0;
        boolean succ = true;

        System.out.println("\n>> Szeretnel egy uj jatekot kezdeni vagy betoltenel egy jatekmenetet?");

        do {
            try {
                succ = true;

                System.out.print(">> Irj egy 1-est, ha jatszani szeretnel, 2-t ha betolteni egy jatekmenetet: ");
                String str = scanner.nextLine();

                chosen = Integer.parseInt(str);

                if(chosen < 1 || chosen > 2) throw new Exception();
            } catch (Exception e) {
                succ = false;
            }
        } while(!succ);

        return chosen;
    }

    /**
     * Megkérdezi a játékostól, hogy hány bottal szeretne játszani.
     */
    protected void getBots() {
        do {
            try {
                System.out.print(">> Add meg hany gepi ellenfellel szeretnel jatszani (max 10): ");
                String str = scanner.nextLine();

                botCount = Integer.parseInt(str);

                if (botCount < 1 || botCount > 10) throw new Exception();
            } catch(Exception e) {
                botCount = 0;
            }
        } while(botCount == 0);
    }

    /**
     * Amennyiben minden adat megvan a játék elkezdéséhez, ez a metódus legenerálja a játékosokat a megadott bot szám alapján.
     */
    protected void createPlayers() {
        players = new Player[botCount + 1];

        players[0] = new Player(1, this);

        for (int i = 1; i <= botCount; i++) {
            int r = rand.nextInt(3);
            switch(r) {
                case 0: players[i] = new AttackerBot(i+1, this);
                case 1: players[i] = new SmartBot(i+1, this);
                case 2: players[i] = new VerySmartBot(i+1, this);
            }
        }
    }

    /**
     * A területszám és a botok száma alapján legenerál egy véletlenszerű térképet egy másik metódus segítségével.<br>
     * Meghív további két metódust, amik szükségesek a pálya megfelelő létrehozásához.
     */
    protected void initializeTable() {
        int x = (int)(Math.sqrt(fieldPerPlayer) + (botCount));
        int y = (int)((Math.sqrt(fieldPerPlayer) + (botCount))) + 1;

        table = Field.generate(x, y);

        addOwnerToFields();
        addDicesToFields();
    }

    /**
     * Miután létrejött az alaptérkép, ez a metódus rendeli hozzá a mezőkhöz véletlenszerűen a játékosokat.
     */
    protected void addOwnerToFields() {
        for (int i = 0; i <= botCount; i++) {
            int fieldAddedToOwner = 0;
            while(fieldAddedToOwner != fieldPerPlayer) {
                int x = rand.nextInt(table.length), y = rand.nextInt(table[0].length);

                if(table[x][y] == null) continue;

                if(table[x][y].getOwner() == null) {
                    table[x][y].setOwner(players[i]);
                    fieldAddedToOwner++;
                }
            }
        }
    }

    /**
     * A térkép létrejötte és a játékosok mezőkhöz rendelése után ez a metódus végzi el a dobokockák<br>
     * megfelelő számú szétosztását a mezők között.
     */
    protected void addDicesToFields() {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                if(table[i][j].getOwner() != null) table[i][j].addDiceCount(1);
            }
        }

        for (int i = 0; i <= botCount; i++) {
            int dices = fieldPerPlayer * 3 - fieldPerPlayer;
            while (dices != 0) {
                int x = rand.nextInt(table.length), y = rand.nextInt(table[0].length);
                if(table[x][y].getOwner() == players[i] && table[x][y].getDiceCount() < 8) {
                    int dice;
                    while ((dice = rand.nextInt(8 - table[x][y].getDiceCount()) + 1) > dices) { }

                    table[x][y].addDiceCount(dice);
                    dices -= dice;
                }
            }
        }
    }

    /**
     * A játék magja. Itt történik a lépések bekérése, a kör eleji üzenetek kiiratása, a térkép kiiratása,<br>
     * a mezők szétoszlásának kiiratása, valamint a kiesett játékosok törlése a játékból.
     */
    protected void gameLoop() {
        while(checkIfSomeoneIsStillInGame()) {
            roundCount++;
            currentPlayer = getNextPlayer();
            while(currentPlayer == null) { currentPlayer = getNextPlayer(); }

            systemOutAndRecord("");
            systemOutAndRecord("");
            systemOutAndRecord(">>>>> " + roundCount + ". KOR <<<<<");
            systemOutAndRecord(">> A kovetkezo jatekos: " + currentPlayer.getName());

            int ret = 0;
            do {
                ret = currentPlayer.getInput(scanner);
                showTable();
                showStatus();
                if(checkFields() == 1) return;
                wait(2);
                gw.add(GameWriter.wait, Integer.toString(2));
            } while (ret != 0);

            wait(2);
            gw.add(GameWriter.wait, Integer.toString(2));
        }
        gameOver();
    }

    /**
     * Bekéri a játékostól a választott támadó mező sorszámát, majd amennyiben ez helytelen lekezeli azt.<br>
     * Helyes bemenet esetén visszaadja a választott mezőt.
     * @return A választott támadó mező
     */
    public Field getAttackerField() {
        int[] correct_fields = currentPlayer.showAttackerFields();
        Field[] fields = currentPlayer.getPlayerFields();

        if(correct_fields != null) {
            int val = 0;
            do {
                try {
                    System.out.print(">> Ird be a valasztott mezo sorszamat: ");
                    String str = scanner.nextLine();
                    val = Integer.parseInt(str);
                } catch (Exception e) {
                    val = 0;
                }
            } while (val < 1 || !contains(correct_fields, val) || fields[val - 1] == null);

            return fields[val - 1];
        }
        return null;
    }

    /**
     * Megadja, hogy egy szám szerepel-e egy számokat tartalmazó tömbben.
     * @param array Számokat tartalmazó tömb
     * @param value Szám
     * @return Igaz, ha tartalmazza, egyébként hamis
     */
    protected boolean contains(int[] array, int value) {
        for(int i : array) {
            if(i == value) return true;
        }
        return false;
    }

    /**
     * Bekéri a játékostól a választott mező sorszámát, amire támadni akar, majd amennyiben ez helytelen lekezeli azt.<br>
     * Helyes bemenet esetén visszaadja a választott mezőt.
     * @param attacker A támadó mező
     * @return A választott mező, amire támadni akar
     */
    public Field getDefenderField(Field attacker) {
        int[] correct_fields = currentPlayer.showDefenderFields(attacker);
        Field[] fields = attacker.getAttackableFields();

        int val = 0;
        do {
            try {
                System.out.print("\n>> Ird be a valasztott mezo sorszamat: ");
                String str = scanner.nextLine();
                val = Integer.parseInt(str);
            } catch(Exception e) { val = 0; }
        } while(val < 1 || !contains(correct_fields, val) || fields[val - 1] == null);

        return fields[val - 1];
    }

    /**
     * Kiírja a paraméterül kapott eredmény alapján az eredményeket, illetve a nyertest.<br>
     * Elvégzi a szükséges műveleteket a mezőkön mindhárom eset után.
     * @param result Egy RollResult objektum, ami tartalmazza a dobások adatait
     */
    public void handleAttack(RollResult result) {
        result.calculateResult();

        systemOutAndRecord("EREDMENY: " + result.getAttackerPlayer().getName() +
                " osszesen " +
                result.getAttackerResult() +
                "-t dobott.");

        systemOutAndRecord("EREDMENY: " + result.getDefenderPlayer().getName() +
                " osszesen " +
                result.getDefenderResult() +
                "-t dobott.");


        systemOutAndRecord(">> Nyertes: " +
                result.getWinnerPlayer().getName() +
                " (" + result.getWinnerResult() + "|" + result.getLoserResult() + ")");

        switch(result.getResult()) {
            case RollResult.attacker_win:
                result.getLoserField().setOwner(result.getWinnerPlayer());

                int dices = result.getWinnerField().getDiceCount();

                result.getWinnerField().setDiceCount(1);
                result.getLoserField().setDiceCount(dices - 1);
                break;
            case RollResult.defender_win, RollResult.draw:
                result.getLoserField().setDiceCount(1);
                break;
        }
        wait(3);
        gw.add(GameWriter.wait, Integer.toString(3));
    }

    /**
     * Kiiratja a táblát.
     */
    protected void showTable() {
        systemOutAndRecord("\n");
        systemPrintAndRecord("       ");
        for (int i = 1; i <= table[0].length; i++) systemPrintAndRecord("<|" + (i < 10 ? "0"+i : i) + "|> ");

        systemOutAndRecord("");

        for (int i = 0; i < table.length; i++) {
            systemPrintAndRecord("<|" + (i+1 < 10 ? "0"+(i+1) : (i+1)) + "|> ");
            for (int j = 0; j < table[0].length; j++) {
                if(table[i][j].getOwner() != null) {
                    int id = table[i][j].getOwner().getId();
                    systemPrintAndRecord("[" + (id < 10 ? "0"+id : id) + "/" + table[i][j].getDiceCount() + "] ");
                } else {
                    systemPrintAndRecord("[XX/X] ");
                }
            }
            systemOutAndRecord("");
        }
    }

    /**
     * Kiiratja a játékosok területeinek számát, valamint a dobokockák számát.
     */
    protected void showStatus() {
        systemOutAndRecord("");
        
        for (int i = 0; i < players.length; i++) {
            if(players[i] == null || !players[i].isInGame()) continue;
            systemOutAndRecord(players[i].getName() + " nevu jatekosnak eddig " +
                                players[i].getPlayerFieldLength() +
                                " terulete van " + players[i].getDiceCount() + " dobokockaval.");
        }
    }

    /**
     * Leellenőrzi, hogy minden játékos jogosult-e a játékban maradáshoz.<br>
     * Amennyiben valakinek elfogytak a területei meghívja a .quit() metódusát.
     */
    protected int checkFields() {
        for(int i = 0; i < players.length; i++) {
            if(players[i] == null) continue;

            if(players[i].isInGame() && players[i].getPlayerFieldLength() == 0) {
                players[i].quit(scanner);
            }
        }
		
		int c = 0;
		Player temp = null;
		for(int i = 0; i < players.length; i++) {
			if(players[i] == null) continue;
			
			if(players[i].isInGame()) {
				temp = players[i];
				c++;
			}
		}
		
		if(temp != null && c == 1) {
			wait(2);
			gw.add(GameWriter.wait, Integer.toString(2));
			systemOutAndRecord(">>>>> NYERTES: " + temp.getName() + " <<<<<");
			gameOver();
		}
		return c;
    }

    /**
     * Megvizsgálja, hogy van-e még olyan játékos, aki játékban van és folytatódhat a játék.
     * @return Igaz, ha van még játékban játékos, egyébként hamis
     */
    protected boolean checkIfSomeoneIsStillInGame() {
        for (int i = 0; i < players.length; i++) {
            if(players[i] == null) continue;
            if(players[i].isInGame()) return true;
        }
        return false;
    }

    /**
     * A játék végi kiiratásokért felelős metódus.
     */
    public void gameOver() {
        systemOutAndRecord(">>>>> A JATEKNAK VEGE!!! <<<<<");
        gw.save();
    }

    /**
     * A currentPlayer változó alapján egy iteráció során megadja, hogy ki következik.
     * @return A következő játékos, vagy null, ha egyedül van játékban valaki
     */
    protected Player getNextPlayer() {
        if(currentPlayer == null) return players[0];

        for (int i = currentPlayer.getId(); i < players.length; i++) {
            if(players[i] == null) return players[0];

            if(players[i].isInGame()) return players[i];

            if(i == currentPlayer.getId()) return null;
        }
        return null;
    }

    /**
     * Lényegében egy System.out.println metódus, viszont amennyiben ez a metódus kerül meghívásra,<br>
     * a kapott üzenet rögzítésre kerül egy GameWriter objektumba, amely a játékmentésért felelős.
     * @param text A kiírandó, valamint a rögzítendő üzenet
     */
    protected void systemOutAndRecord(String text) {
        gw.add(GameWriter.out, text);
        System.out.println(text);
    }

    /**
     * Lényegében egy System.out.print metódus, viszont amennyiben ez a metódus kerül meghívásra,<br>
     * a kapott üzenet rögzítésre kerül egy GameWriter objektumba, amely a játékmentésért felelős.<br>
     * A metódus nem tesz automatikusan sorvége jelet a szöveg végére.
     * @param text A kiírandó, valamint a rögzítendő üzenet
     */
    protected void systemPrintAndRecord(String text) {
        gw.add(GameWriter.print, text);
        System.out.print(text);
    }

    /**
     * Visszaad egy olyan Player objektumot, aminek az ID-je a paraméterül kapott id.
     * @param id A megadott ID
     * @return A megadott ID-hez tartozó játékos, vagy ha nincs ilyen, akkor null
     */
    protected Player getPlayerById(int id) {
        for (Player p : players) {
            if(p == null) continue;

            if(p.getId() == id) return p;
        }
        return null;
    }

    /**
     * Egy metódusba ágyazott várakozó metódus, amely egy try{} catch{} ágban van benne a hibák elkerülése végett.
     * @param seconds Másodperc, amennyit várakozzon a program
     */
    public static void wait(int seconds) {
        try { TimeUnit.SECONDS.sleep(seconds); } catch(Exception e) {}
    }
}
