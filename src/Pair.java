import java.util.Comparator;
public class Pair implements Comparable<Pair> {
    private int vertice;
    private double weight;

    public Pair(int vertice, double weight) {
        this.vertice = vertice;
        this.weight = weight;
    }

    public int getVertice() {
        return vertice;
    }

    public double getWeight() {
        return weight;
    }

    public boolean equals(Object object) {
        return object instanceof Pair && vertice == ( ((Pair)object).getVertice());
    }

    @Override
    public int compareTo(Pair pair) {
        return Comparator.comparingInt(Pair::getVertice).compare(this, pair);
    }
}
