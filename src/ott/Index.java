package ott;

public class Index {

    private int wrapped;

    public Index() {
        wrapped = 0;
    }

    public Index(int value) {
        wrapped = value;
    }

    /**
     * increment value and return the value after its incremented
     * @return
     */
    public int inc() {
        return ++wrapped;
    }

    /**
     * decrement the value and return the value after its decremented
     * @return
     */
    public int dec() {
        return --wrapped;
    }

    public int add(int value) {
        wrapped += value;
        return wrapped;
    }

    public int sub(int value) {
        wrapped -= value;
        return wrapped;
    }

    public void value(int value) {
        wrapped = value;
    }

    public int value() {
        return wrapped;
    }
}
