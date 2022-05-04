import game.DiceWars;

/**
 * Tartalmazza a futtatható main függvényt, amelyben meghívásra kerül a játék.
 */
public class Main {
    public static void main(String[] args) {
        DiceWars dw = new DiceWars(10);

        dw.start();
    }
}
