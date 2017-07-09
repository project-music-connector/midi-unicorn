package sound.gen;

/**
 * Created by Beatriz Fusaro on 7/8/2017.
 */
public enum Key {
    C(1),
    D(2),
    E(3),
    F(4),
    G(5),
    A(6),
    B(7);
    private int key;
    Key(int key) {
        this.key = key;
    }
    public int getKey() {
        return key;
    }
}
