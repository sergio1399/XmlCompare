package app.components.xml;

public class MyPair<T> {
    private T first;

    private T second;

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public MyPair() {
    }

    public MyPair(T first, T second) {
        this.first = first;
        this.second = second;
    }

}
