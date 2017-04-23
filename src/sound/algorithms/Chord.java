package sound.algorithms;

/**
 * Created by Beatriz on 23/04/2017.
 */
public enum Chord {
    MAJOR(0, 4, 7), MINOR(0, 3, 7), DIMINISHED(0, 3, 6);

    private int[] progression;

    Chord(int a, int b, int c) {
        progression = new int[3];
        progression[0] = a;
        progression[0] = b;
        progression[0] = c;
    }

    public int[] getProgression() {
        return progression;
    }
}
