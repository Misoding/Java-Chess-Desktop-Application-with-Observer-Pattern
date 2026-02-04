package game_logic;


public class ChessPair<K extends Comparable<K>,V> implements Comparable<ChessPair<K,V>>{
    private K key;
    private V value;
    public ChessPair(K newKey, V newValue) {
        this.key = newKey;
        this.value = newValue;
    }
    public K getKey() {
        return this.key;
    }
    public V getValue() {
        return this.value;
    }
    public String getPair() {
        String s = this.key.toString() + " - " + this.value.toString();
        return s;
    }
    @Override
    public int compareTo(ChessPair<K,V> difPair) {
       return this.key.compareTo(difPair.key);
    }
}