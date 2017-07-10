package sound.gen;

/**
 * Created by Beatriz Fusaro on 7/8/2017.
 */
public enum Key {
    C(0),
    D(1),
    E(2),
    F(3),
    G(4),
    A(5),
    B(6);
    private int key;
    Key(int key) {
        this.key = key;
    }
    public int getKey() {
        return key;
    }
}
