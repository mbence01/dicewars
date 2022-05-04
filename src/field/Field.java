package field;

import game.*;
import gamesave.*;
import players.*;
import rollresult.*;

/**
 * A játék alapjául szolgáló térképen levő mezőket valósítja meg.<br>
 * Lehetőségünk van a mező tulajdonosát és a mezőn található dobókockák számát beállítani.<br>
 * Különböző osztálybeli metódusokkal lekérdezhető az adott mezőről támadható mezők halmaza, illetve, hogy lehet-e róla támadni.
 */
public class Field {
    private Player owner;

    private int x;
    private int y;

    private int diceCount;

    public Player getOwner() { return owner; }
    public int getDiceCount() { return diceCount; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void setOwner(Player owner) { this.owner = owner; }
    public void setDiceCount(int diceCount) { this.diceCount = diceCount; }
    public void addDiceCount(int diceCount) {
        this.diceCount += diceCount;
        if(this.diceCount > 8) this.diceCount = 8;
        if(this.diceCount < 1) this.diceCount = 1;
    }

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Megadja, hogy az adott mezőről elérhető-e olyan másik mező, ami támadható.
     * @return Igaz, ha van olyan mező, minden más esetben hamis
     */
    public boolean canAttackFromHere() {
        if(getAttackableFields() == null) return false;
        for (int i = 0; i < getAttackableFields().length; i++) {
            if(getAttackableFields()[i] != null) return true;
        }
        return false;
    }

    /**
     * Visszaadja az adott mezőről támadható mezőket egy tömb formájában.
     * @return A támadható mezők tömbje, ha nincs ilyen, akkor egy 4 méretű null tömböt
     */
    public Field[] getAttackableFields() {
        Field[][] table = owner.getCurrGame().getTable();

        if(diceCount < 2) return null;

        Field[] fields = new Field[4];
        int c = 0;

        if(x-1 >= 0 && table[x - 1][y].getOwner() != null && table[x - 1][y].getOwner() != owner) fields[c++] = table[x - 1][y];
        if(x+1 < table.length && table[x + 1][y].getOwner() != null && table[x + 1][y].getOwner() != owner) fields[c++] = table[x + 1][y];
        if(y+1 < table[0].length && table[x][y + 1].getOwner() != null && table[x][y + 1].getOwner() != owner) fields[c++] = table[x][y + 1];
        if(y-1 >= 0 && table[x][y - 1].getOwner() != null && table[x][y - 1].getOwner() != owner) fields[c++] = table[x][y - 1];

        return fields;
    }

    /**
     * Szöveggé alakítja az adott mező pozícióját.
     * @return Egy szöveg, ami tartalmazza a két koordinátát
     */
    public String toString() {
        return "[" + (x+1) + ", " + (y+1) + "]";
    }

    /**
     * Generál egy véletlenszerű térképet, majd visszaadja azt.
     * @param x A generálandó térkép sorszáma
     * @param y A generálandó térkép oszlopszáma
     * @return A generált térkép 2 dimenziós tömb formájában
     */
    public static Field[][] generate(int x, int y) {
        Field[][] ret = new Field[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                ret[i][j] = new Field(i, j);
            }
        }
        return ret;
    }
}
