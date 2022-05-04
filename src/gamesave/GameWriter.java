package gamesave;

import game.*;
import field.*;
import players.*;
import rollresult.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A játék mentését megvalósító osztály.<br>
 * Segítségével lehetőségünk van szöveget (sortöréssel vagy nélküle), illetve várakozást menteni.
 */
public class GameWriter {
    private List<String> data = new ArrayList<String>();

    private boolean started;

    public static final int out = 0;
    public static final int wait = 1;
    public static final int print = 2;

    public boolean isStarted() { return started; }

    public GameWriter() {
        started = false;
    }

    /**
     * Segítségével elindítható a mentés. Ezután mindent menteni fog az osztály.
     */
    public void start() {
        started = true;
    }

    /**
     * Segítségével leállítható a mentés. Ezután nem fogja menteni az adatokat.<br>
     * (Ez a metódus önmagában még nem menti el a fájlt!)
     */
    public void stop() {
        started = false;
    }

    /**
     * Segítségével hozzáfűzhető egy sor a menteni kívánt játékmenethez.
     * @param type Kiiratás sortöréssel: 0, sortörés nélkül: 2, várakozás: 1 (használhatóak az osztálybeli konstansok: GameWriter.out, GameWriter.print, GameWriter.wait)
     * @param text Kiiratásnál a menteni kívánt szöveg, várakozásnál a várakozás időtartama másodpercben
     */
    public void add(int type, String text) {
        if(!started) return;

        if(type == out) data.add("out");
        else if(type == print) data.add("print");
        else data.add("wait");
        data.add(text);
    }

    /**
     * A metódus elmenti az eddig rögzített sorokat és várakozásokat a megadott fájlba.
     * @return Igaz, ha sikeres volt a mentés, minden más esetben hamis
     */
    public boolean save() {
        String filename = "gamesave/saves/game_save_";
        int c = 1;

        while(new File(filename + c + ".txt").exists()) c++;

        try {
            FileWriter fw = new FileWriter(filename + c + ".txt");

            for (String str : data) {
                fw.write(str + "\n");
            }

            fw.close();

            return true;
        } catch(IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
