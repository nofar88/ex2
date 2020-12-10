package api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class DWGraph_DS implements directed_weighted_graph {

    enum Direction {IN, OUT}

    HashMap<Integer, node_data> nodes;
    HashMap<node_data, HashMap<Direction, HashMap<node_data, edge_data>>> edges;

    private int edgeSize;
    private int modeCount;

    public DWGraph_DS() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
        edgeSize = 0;
        modeCount = 0;
    }


    @Override
    public node_data getNode(int key) {
        return nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        try {// Checks if there is any side between the vertices
            return this.edges.get(getNode(src)).get(Direction.OUT).get(getNode(dest));// Checks if they are really neighbors if their sons have a rib and returns it

        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void addNode(node_data n) {
        if (!nodes.containsKey(n.getKey())) { //Checks whether the node you want to add whether it exists or not, if it does not exist then
            nodes.put(n.getKey(), n);

            HashMap<Direction, HashMap<node_data, edge_data>> initHashMap = new HashMap<>();
            initHashMap.put(Direction.OUT, new HashMap<>());
            initHashMap.put(Direction.IN, new HashMap<>());
            edges.put(n, initHashMap);// Adding to the hashmap in out

            modeCount++;
        }

    }

    @Override
    public void connect(int src, int dest, double w) {
        if (w < 0) {
            System.err.println("the Weight of edge must be positive");
            return;
        }
        if (src == dest) {
            System.err.println(" There is no edge between a node and itself ");
            return;
        }
        if (getNode(src) == null || getNode(dest) == null) {
            System.err.println("one of the nodes are not exist");
            return;
        }

        node_data srcNode = nodes.get(src);
        node_data destNode = nodes.get(dest);


        if (getEdge(src, dest) != null) { // Checking for a edge
            if (getEdge(src, dest).getWeight() != w)
                modeCount++;// If the weight is really different, only then is a change made added
            ((EdgeData) this.edges.get(srcNode).get(Direction.OUT).get(destNode)).setWeight(w); // Update the weight עשיתי קסטינג כי סט משקל הוא במימוש
            return;

        }

        edge_data newEdge = new EdgeData(w, src, dest); // New edge

// יוצר צלע חדשה בין הססורס לדסט אם היא לא קיים אנחנו יודעים שזו צלע יוצאת ובשורה השניה רק מעדנים את הדסט שיש לו את הזאת כנכנסת
        this.edges.get(srcNode).get(Direction.OUT).put(destNode, newEdge);
        this.edges.get(destNode).get(Direction.IN).put(srcNode, newEdge);

        modeCount++;
        edgeSize++;

    }

    @Override
    public Collection<node_data> getV() {
        return nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {// There can be an error when we request a node that does not exist and therefore do
        try {
            return edges.get(getNode(node_id)).get(Direction.OUT).values();
        } catch (Exception ex) {
            return new ArrayList<edge_data>();
        }
    }

    @Override
    public node_data removeNode(int key) {
        if (!nodes.containsKey(key)) { // הקודקוד שאותו רוצים למחוק בכלל לא קיים
            return null;
        }

        node_data cur = getNode(key);// מחזיק את הקודקוד שאותו אנחנו רוצים למחוק
        HashMap<node_data, edge_data> in = edges.get(cur).get(Direction.IN); // אשמאפ שמחזיק לי את כל הקודקודים והצלעות שנכסנות אל הקודקוד שאותו אנחנו רוצים למחוק
        for (node_data neiOfTheRemoveNode : in.keySet()) { // רצה על כל השכנים של הקודקוד שאותו אנחנו רוצים למחוק
            edges.get(neiOfTheRemoveNode).get(Direction.OUT).remove(cur); // אנחנו מוחקים את עצמי מהרשימת שכנים שעבורם אני צלע יוצאת ולכן אוטמטית נמחק ככל הצלעות שהיו קשורות לקודקוד הנמחק
            edgeSize--;
        }

        HashMap<node_data, edge_data> out = edges.get(cur).get(Direction.OUT);// אשמאפ שמחזיק לי את כל הקודקודים והצלעות שיוצאות אל הקודקוד שאותו אנחנו רוצים למחוק
        for (node_data neiOfTheRemoveNode : out.keySet()) { // רצה על כל השכנים של הקודקוד שאותו אנחנו רוצים למחוק
            edges.get(neiOfTheRemoveNode).get(Direction.IN).remove(cur); // אנחנו מעדכנים את כל השכנים שעבורם הקודקוד הנמחק הוא צלע נכנסת שאני מחוק
            edgeSize--;
        }

        modeCount++;
        nodes.remove(key);
        edges.remove(cur);
        return cur;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if (!this.nodes.containsKey(src) || !this.nodes.containsKey(dest)) {
            System.err.println(" one of the nodes not in the graph");
            return null;
        }
        if (getEdge(src, dest) == null) {
            System.err.println(" there is no edge between nodes");
            return null;
        }


        edge_data edge = getEdge(src, dest);

        // עדכון של האווט והאין שאנחנו מוחקים את הצלע בניהם
        edges.get(getNode(src)).get(Direction.OUT).remove(getNode(dest));
        edges.get(getNode(dest)).get(Direction.IN).remove(getNode(src));

        edgeSize--;
        modeCount++;
        return edge;
    }

    @Override
    public int nodeSize() {
        return nodes.size();
    }

    @Override
    public int edgeSize() {
        return edgeSize;
    }

    @Override
    public int getMC() {
        return modeCount;
    }
    /***
     * set mc for deep copy
     * @param mc
     */
    public void setMC(int mc) {
        this.modeCount=mc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DWGraph_DS graph_ds = (DWGraph_DS) o;

        if (edgeSize != graph_ds.edgeSize) return false;
        if (nodes != null ? !nodes.equals(graph_ds.nodes) : graph_ds.nodes != null) return false;
        return edges != null ? edges.equals(graph_ds.edges) : graph_ds.edges == null;
    }

    @Override
    public int hashCode() {
        int result = nodes != null ? nodes.hashCode() : 0;
        result = 31 * result + (edges != null ? edges.hashCode() : 0);
        result = 31 * result + edgeSize;
        result = 31 * result + modeCount;
        return result;
    }

    @Override
    public String toString() {
        return "DWGraph_DS{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", edgeSize=" + edgeSize +
                ", modeCount=" + modeCount +
                '}';
    }
}
