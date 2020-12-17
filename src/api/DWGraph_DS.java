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
    }//O(1)

    @Override
    public edge_data getEdge(int src, int dest) { //O(1)
        try {// Checks if there is any side between the vertices
            return this.edges.get(getNode(src)).get(Direction.OUT).get(getNode(dest));// Checks if they are really neighbors if their sons have a edge and returns it

        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void addNode(node_data n) {//O(1)
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
    public void connect(int src, int dest, double w) { //O(1)
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
            ((EdgeData) this.edges.get(srcNode).get(Direction.OUT).get(destNode)).setWeight(w); // Update the weight (I did a casting because a weight set is exercised)
            return;

        }

        edge_data newEdge = new EdgeData(w, src, dest); // New edge

// Creates a new edge between src to dest if it does not exist, we know it is an outgoing edge and in the second row we only update the dest that has this edge as incoming
        this.edges.get(srcNode).get(Direction.OUT).put(destNode, newEdge);
        this.edges.get(destNode).get(Direction.IN).put(srcNode, newEdge);

        modeCount++;
        edgeSize++;

    }

    @Override
    public Collection<node_data> getV() {
        return nodes.values();
    } //O(1)

    @Override
    //O(1)
    public Collection<edge_data> getE(int node_id) {// There can be an error when we request a node that does not exist and therefore do
        try {
            return edges.get(getNode(node_id)).get(Direction.OUT).values();
        } catch (Exception ex) {
            return new ArrayList<edge_data>();
        }
    }

    @Override
    public node_data removeNode(int key) {// O(k), V.degree=k
        if (!nodes.containsKey(key)) { // The vertex you want to delete does not exist at all
            return null;
        }

        node_data cur = getNode(key);// Holds the vertex we want to delete
        HashMap<node_data, edge_data> in = edges.get(cur).get(Direction.IN); // HashMap that holds all the vertices and edges that go into the vertex that we want to delete
        for (node_data neiOfTheRemoveNode : in.keySet()) { // Running on all the neighbors of the vertex that we want to delete
            edges.get(neiOfTheRemoveNode).get(Direction.OUT).remove(cur); // We delete myself from the list of neighbors for which I am an outgoing edge, so all the edges that were associated with the deleted vertex are automatically deleted
            edgeSize--;
        }

        HashMap<node_data, edge_data> out = edges.get(cur).get(Direction.OUT);// HashMap that holds all the vertices and edges that go out to the vertex that we want to delete
        for (node_data neiOfTheRemoveNode : out.keySet()) { // Running on all the neighbors of the vertex that we want to delete
            edges.get(neiOfTheRemoveNode).get(Direction.IN).remove(cur); // We update all neighbors for whom the deleted vertex is an inbound edge
            edgeSize--;
        }

        modeCount++;
        nodes.remove(key);
        edges.remove(cur);
        return cur;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {//O(1)
        if (!this.nodes.containsKey(src) || !this.nodes.containsKey(dest)) {
            System.err.println(" one of the nodes not in the graph");
            return null;
        }
        if (getEdge(src, dest) == null) {
            System.err.println(" there is no edge between nodes");
            return null;
        }


        edge_data edge = getEdge(src, dest);

        // Update of out and in that we delete their edge
        edges.get(getNode(src)).get(Direction.OUT).remove(getNode(dest));
        edges.get(getNode(dest)).get(Direction.IN).remove(getNode(src));

        edgeSize--;
        modeCount++;
        return edge;
    }

    @Override
    public int nodeSize() {
        return nodes.size();
    }//O(1)

    @Override
    public int edgeSize() {
        return edgeSize;
    }//O(1)

    @Override
    public int getMC() {
        return modeCount;
    }//O(1)
    /***
     * set mc for deep copy
     * @param mc
     */
    public void setMC(int mc) {
        this.modeCount=mc;
    }//O(1)

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
    public int hashCode() { //O(1)
        int result = nodes != null ? nodes.hashCode() : 0;
        result = 31 * result + (edges != null ? edges.hashCode() : 0);
        result = 31 * result + edgeSize;
        result = 31 * result + modeCount;
        return result;
    }

    @Override
    public String toString() { //O(1)
        return "DWGraph_DS{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", edgeSize=" + edgeSize +
                ", modeCount=" + modeCount +
                '}';
    }
}
