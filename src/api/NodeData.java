package api;

import gameClient.util.Point3D;

public class NodeData  implements node_data, Comparable<NodeData> {
    private int key;
    private int pred;
    private String info;
    private int tag;
    private boolean visited;
    private geo_location location;
    private double weight;
    static int id_counter=0;

    public NodeData(int key) {
        this.key = key;
        this.location = new Point3D(0, 0, 0);
    }

    public int getPred() {
        return pred;
    }

    public void setPred(int pred) {
        this.pred = pred;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public NodeData() {
        this.key = id_counter;
        id_counter++;
    }

    public NodeData(int key, String info, int tag, geo_location location, double weight) {
        this.key = key;
        this.info = info;
        this.tag = tag;
        this.location = location;
        this.weight = weight;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public geo_location getLocation() {
        return location;
    }

    @Override
    public void setLocation(geo_location p) {
        this.location=p;

    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight=w;

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

        NodeData nodeData = (NodeData) o;

        if (key != nodeData.key) return false;
        if (tag != nodeData.tag) return false;
        if (info != null ? !info.equals(nodeData.info) : nodeData.info != null) return false;
        return location != null ? location.equals(nodeData.location) : nodeData.location == null;
    }

    @Override
    public int hashCode() {
        return key;
    }


    @Override
    public int compareTo(NodeData o) {//O(1)
        int ans=0;
        if( this.weight>o.weight){
            ans=1;
        }
        else{
            if ((this.weight<o.weight)){
                ans=-1;
            }
        }
        return ans;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "key=" + key +
                ", pred=" + pred +
                ", info='" + info + '\'' +
                ", tag=" + tag +
                ", visited=" + visited +
                ", location=" + location +
                ", weight=" + weight +
                '}';
    }
}
