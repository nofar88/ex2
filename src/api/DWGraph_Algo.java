package api;

import com.google.gson.*;
import gameClient.util.Point3D;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {

    private directed_weighted_graph graph;
    private HashMap<Integer, Boolean> viseted1, viseted2;

    public DWGraph_Algo() {
        this.graph = new DWGraph_DS();
    }


    @Override
    public void init(directed_weighted_graph g) {
        this.graph = g;
    }//O(1)

    @Override
    public directed_weighted_graph getGraph() {//O(1)

        return graph;
    }

    @Override
    public directed_weighted_graph copy() {//O(v+e), e-edges v-nodes
        DWGraph_DS graphcopy = new DWGraph_DS();
        for (node_data nodes : graph.getV()) {// Run on all the vertices in the original graph, copy them to the new graph along with the tag and info
            int key = nodes.getKey();
            graphcopy.addNode(new NodeData(key));
            graphcopy.getNode(key).setInfo(graph.getNode(key).getInfo());
            graphcopy.getNode(key).setTag(graph.getNode(key).getTag());
            graphcopy.getNode(key).setLocation(graph.getNode(key).getLocation());
        }
        for (node_data nodes : graph.getV()) { // Run on all the vertices
            for (edge_data edges : graph.getE(nodes.getKey())) { // Go through all the outgoing edges of a particular vertex and connect (because going over all the vertices it will do the incoming edges as well)
                graphcopy.connect(edges.getSrc(), edges.getDest(), edges.getWeight());
            }
        }
        graphcopy.setMC(graph.getMC());
        return graphcopy;
    }

    @Override
    public boolean isConnected() {// O(V+E)
        if (this.graph.getV().size() < 2) {
            return true;
        }

        viseted1 = new HashMap<>();// Create a new HashMap, run on all the vertices of the graph and insert them into the HashMap and start them to false because they were not visited
        for (node_data node : graph.getV()) {
            viseted1.put(node.getKey(), false);
        }
        DFS(graph.getV().iterator().next().getKey()); //Send the first vertex to the DFS function

        // Call for reverse direction
        viseted2 = new HashMap<>(); // initialization another HashMap , running on all vertices initializes them to be false
        for (node_data node : graph.getV()) {
            viseted2.put(node.getKey(), false);
        }
        reverseDFS(graph.getV().iterator().next().getKey()); // Sends to the function of the inverted DFS, its edges are inverted but starting from the same vertex

        for (node_data nodes : graph.getV()) {
            if (!viseted1.get(nodes.getKey()) || !viseted2.get(nodes.getKey()))// It is enough that one of the HashMap returned false meaning that it was not visited from both directions
                return false;
        }

        return true;


    }

    private void DFS(int key) { // O(V+E)

        Stack<Integer> stack = new Stack<>(); // Create a stack for DFS
        stack.push(key);

        while (!stack.empty()) {
            key = stack.pop();// Remove the first one from the stack

            if (!viseted1.get(key)) {// If not visit it
                viseted1.put(key, true);// Mark the vertex we start with true, we visited it
            }

            for (edge_data edge_data : graph.getE(key)) {// Go through all the outgoing edges of the current vertex
                int v = edge_data.getDest();// Perception of the neighbor himself
                if (!viseted1.get(v)) // If the neighbor of the current vertex has not visited it, then put it in the stack
                    stack.push(v);
            }
        }
    }

    private void reverseDFS(int key) { // O(V+E)

        Stack<Integer> stack = new Stack<>();  // Create a stack for DFS
        stack.push(key);

        while (!stack.empty()) {

            key = stack.pop();

            if (!viseted2.get(key)) {
                viseted2.put(key, true);// Mark the vertex we start with true, we visited it
            }


            for (node_data i : ((DWGraph_DS) graph).edges.get(graph.getNode(key)).get(DWGraph_DS.Direction.IN).keySet()) { // Go through all the incoming neighbors of the current vertex
                int v = i.getKey(); // Perceive their value
                if (!viseted2.get(v))
                    stack.push(v);
            }
        }
    }

    @Override
    public double shortestPathDist(int src, int dest) {//O(E*LOG2(V))
        if (graph.getV().size() == 0) {
            return -1;
        }
        if (graph.getNode(src) == null || graph.getNode(dest) == null) {
            return -1;
        }

        for (node_data nodes : graph.getV()) {// A loop that starts up
            nodes.setWeight(Double.MAX_VALUE);
            ((NodeData) nodes).setPred(Integer.MIN_VALUE);
            ((NodeData) nodes).setVisited(false);
        }
        NodeData s = (NodeData) graph.getNode(src);//Holding the start node
        s.setWeight(0);// The node from which we begin
        PriorityQueue<NodeData> queue = new PriorityQueue<NodeData>();
        for (node_data nodes : graph.getV()) {// Run on the nodes of the graph
            queue.add((NodeData) nodes); // Add to the priority queue all the vertices of the graph, when the distance of all of them is equal to infinity except the first vertex is zero
        }
        while (!queue.isEmpty()) {
            NodeData u = queue.poll(); // Pull out the node with the minimum value
            for (edge_data outEdge : graph.getE(u.getKey())) {// Go through all the neighbors of u which is the node that removed
                node_data nei = graph.getNode(outEdge.getDest());
                if (!((NodeData) nei).isVisited()) { // As long as we have not visited this node already
                    double t = u.getWeight() + outEdge.getWeight();//t = holds the distance -Calculate the distance to the node where you are

                    if (nei.getWeight() > t) { // Checks whether to update the distance of the neighbors of the current vertex that is smaller
                        nei.setWeight(t);
                        ((NodeData) nei).setPred(u.getKey()); // Update where I got to the node

                        //Do some action that will update the queue
                        queue.remove(nei);
                        queue.add((NodeData) nei);

                    }
                }
            }
            u.setVisited(true);
        }

        if (graph.getNode(dest).getWeight() == Double.MAX_VALUE)// When there is no path between two nodes return -1
            return -1;
        return graph.getNode(dest).getWeight(); // Returns the length of the distance

    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {//O(E*LOG2(V))
        if (graph.getNode(src) == null || graph.getNode(dest) == null) return null;
        List<node_data> path = new ArrayList<>();// Start a new list
        List<node_data> pathRe = new ArrayList<>(); // Initialize another list that we will enter in the correct order
        double dist = shortestPathDist(src, dest);
        if (dist == -1)// f there is no track at all then the graph is not a link and I want to access something I can not
            return null;

        path.add(graph.getNode(dest));

        while (src != dest) {
            dest = ((NodeData) graph.getNode(dest)).getPred();// Update of the distance to be the previous one
            path.add(graph.getNode(dest));
        }
        if (path.size() == 0) return null;

        for (int i = path.size() - 1; i >= 0; i--) {
            pathRe.add(path.get(i));
        }
        return pathRe;
    }

    @Override
    public boolean save(String file) {//O(v+e)
        try {

            JsonArray edges = new JsonArray();
            JsonArray nodes = new JsonArray();
            for (node_data node: graph.getV()) {
                JsonObject newNode = new JsonObject();
                try {
                    geo_location location = node.getLocation();
                    newNode.addProperty("pos", location.x() + "," + location.y() + "," + location.z());
                } catch (Exception ex){
                    newNode.addProperty("pos", "0.0,0.0,0.0");
                }
                newNode.addProperty("id", node.getKey());
                nodes.add(newNode);

                for (edge_data edge: graph.getE(node.getKey())) {
                    JsonObject newEdge = new JsonObject();
                    newEdge.addProperty("src", edge.getSrc());
                    newEdge.addProperty("w", edge.getWeight());
                    newEdge.addProperty("dest", edge.getDest());
                    edges.add(newEdge);
                }
            }

            JsonObject obj = new JsonObject();
            obj.add("Edges", edges);
            obj.add("Nodes", nodes);

            FileWriter myWriter = new FileWriter(file);
            myWriter.write(obj.toString());
            myWriter.close();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean load(String file) {//O(v+e)
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            String str = "";
            while (myReader.hasNext())
                str += myReader.nextLine() + "\n";

            JsonObject jsonObject = gson.fromJson(str, JsonObject.class);

            DWGraph_DS graphDs = new DWGraph_DS();
            for (JsonElement nodeElement: jsonObject.get("Nodes").getAsJsonArray()) {
                JsonObject node = nodeElement.getAsJsonObject();
                NodeData newNode = new NodeData(node.get("id").getAsInt());
                newNode.setLocation(new Point3D(node.get("pos").getAsString()));
                graphDs.addNode(newNode);
            }

            for (JsonElement edgeElement: jsonObject.get("Edges").getAsJsonArray()) {
                JsonObject edge = edgeElement.getAsJsonObject();
                graphDs.connect(edge.get("src").getAsInt(), edge.get("dest").getAsInt(),
                        edge.get("w").getAsDouble());
            }

            this.graph = graphDs;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
