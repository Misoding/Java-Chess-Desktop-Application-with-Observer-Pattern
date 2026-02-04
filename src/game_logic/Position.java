package game_logic;

import misc.Pair;

public class Position implements Comparable<Position>{
    private char x;
    private int y;
    public Position(char x, int y) {
        this.x = x;
        this.y = y;
    }
    public char getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Position)) return false;
        if (this == o) return true;
        Position secondObj = (Position)o;
        return (this.x == secondObj.x && this.y == secondObj.y);
    }
    @Override
    public String toString() {
        String s;
        s  = "" + this.x + this.y;
        return s;
    }
    public int compareTo(Position secondObj) throws ClassCastException {
        int res = (this.y - secondObj.y);
        if(res == 0) {
            return (int) this.x - secondObj.x;
        }
        return res;
    }
    public int hashCode() {
        return (this.x - 'A') + (this.y - 1) * 8;
    }
}
