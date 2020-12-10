package api;

public class EdgeData implements edge_data{

    private int src;
    private int dest;
    private double weight;
    private String info;
    private int tag;


    public EdgeData(double weight, int source, int destination) {
        this.weight = weight;
        this.src=source;
        this.dest=destination;
    }



    @Override
    public int getSrc() {
        return src;
    }

    @Override
    public int getDest() {
        return dest;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String s) {
        this.info=s;

    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public void setTag(int t) {
        this.tag=t;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdgeData edgeData = (EdgeData) o;

        if (src != edgeData.src) return false;
        if (dest != edgeData.dest) return false;
        if (Double.compare(edgeData.weight, weight) != 0) return false;
        if (tag != edgeData.tag) return false;
        return info != null ? info.equals(edgeData.info) : edgeData.info == null;
    }

    @Override
    public int hashCode() {
        int result = src;
        result = 31 * result + dest;
        return result;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "EdgeData{" +
                "src=" + src +
                ", dest=" + dest +
                ", weight=" + weight +
                ", info='" + info + '\'' +
                ", tag=" + tag +
                '}';
    }
}
