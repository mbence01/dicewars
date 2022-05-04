package gamesave;

import game.*;
import field.*;
import players.*;
import rollresult.*;

import java.io.File;
import java.util.*;

/**
 * Egy már létező játékmenetet tud beolvasni és kezelni egy megadott fájlból.<br>
 * Az osztály egy megadott kódolás szerint irat ki szöveget.<br>
 */
public class GameReader {
    private List<String> data;

    public GameReader(String filename) {
        data = new ArrayList<String>();
        readFile(filename);
    }

    /**
     * Beolvassa a fájl tartalmát egy listába, amiből később dolgozni fog a program.
     * @param filename A fájl elérési útvonala
     */
    private void readFile(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));

            while(sc.hasNextLine()) {
                data.add(sc.nextLine());
            }

            sc.close();
        } catch(Exception e) {
            System.err.println("Hiba tortent a fajl megnyitasakor! Ellenorizd az utvonalat!");
        }
    }

    /**
     * Elindítja a játékmenet visszajátszást.<br>
     * Értelmezi a beolvasott parancsot, majd ez alapján elvégzi a következő sor helyes kiiratását.
     */
    public void start() {
        Iterator<String> iterator = data.iterator();

        while(iterator.hasNext()) {
            String str = iterator.next();

            switch(str) {
                case "out":
                    System.out.println(iterator.next());
                    break;
                case "print":
                    System.out.print(iterator.next());
                    break;
                case "wait":
                    DiceWars.wait(Integer.parseInt(iterator.next()));
                    break;
            }
        }
    }
}
