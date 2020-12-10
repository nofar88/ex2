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
    }

    @Override
    public directed_weighted_graph getGraph() {

        return graph;
    }

    @Override
    public directed_weighted_graph copy() {
        DWGraph_DS graphcopy = new DWGraph_DS();
        for (node_data nodes : graph.getV()) {// רציל על כל הקודקודים שבגרף המקורי מעתיקים אותם לגרף החדש יחד עם הטאג והאינפו
            int key = nodes.getKey();
            graphcopy.addNode(new NodeData(key));
            graphcopy.getNode(key).setInfo(graph.getNode(key).getInfo());
            graphcopy.getNode(key).setTag(graph.getNode(key).getTag());
            graphcopy.getNode(key).setLocation(graph.getNode(key).getLocation());
        }
        for (node_data nodes : graph.getV()) { // רצים על כל הקודקודים
            for (edge_data edges : graph.getE(nodes.getKey())) { // עוברים על כל הצלעות היוצאות של קודקוד מסוים ומחברים ( בגלל שעוברים על כל הקודקודים זה יעשה גם את הצלעות הנכנסות
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

        viseted1 = new HashMap<>();// יצירת אש מאפ חדש ריצה על כל הקודקודים של הגרף להכניס אותם לתוך האש מאפ ולהתחל אותם לפולס כי לא בקרו בהם
        for (node_data node : graph.getV()) {
            viseted1.put(node.getKey(), false);
        }
        DFS(graph.getV().iterator().next().getKey()); //שולחים לפונקציה של DFS את הקודקוד הראשון

        // Call for reverse direction
        viseted2 = new HashMap<>(); // אתחול אש מאפ נוסף שרץ על כל הקודקודים מאתחל אותם להיות פולס
        for (node_data node : graph.getV()) {
            viseted2.put(node.getKey(), false);
        }
        reverseDFS(graph.getV().iterator().next().getKey()); // שולח לפונקציה של DFS ההפוכה כלומר הצלעות בה הפוכות אבל מתחילים מאותו קודקוד

        for (node_data nodes : graph.getV()) {
            if (!viseted1.get(nodes.getKey()) || !viseted2.get(nodes.getKey()))// מספיק שאחד מאשאפים החזיר פולס כלומר לא ביקרו בו משני הכיוונים
                return false;
        }

        return true;


    }

    private void DFS(int key) {

        Stack<Integer> stack = new Stack<>(); // Create a stack for DFS
        stack.push(key);

        while (!stack.empty()) {
            key = stack.pop();// להוציא את הראשון מהמחסנית

            if (!viseted1.get(key)) {// אם לא בקרו בו
                viseted1.put(key, true);// מסמנים את הקודקוד שאיתו מתחילים בטרו כלומר בקרנו בו
            }

            for (edge_data edge_data : graph.getE(key)) {// עוברים על כל הצלעות היוצאות של הקודקוד הנוכחי
                int v = edge_data.getDest();// תפיסת השכן עצמו
                if (!viseted1.get(v)) // אם השכן שלו הקודקוד הנוכחי לא בקרו בו, אז להכניס אותו למחסנית
                    stack.push(v);
            }
        }
    }

    private void reverseDFS(int key) {

        Stack<Integer> stack = new Stack<>();  // Create a stack for DFS
        stack.push(key);

        while (!stack.empty()) {

            key = stack.pop();

            if (!viseted2.get(key)) {
                viseted2.put(key, true);// מסמנים את הקודקוד שאיתו מתחילים בטרו כלומר בקרנו בו
            }


            for (node_data i : ((DWGraph_DS) graph).edges.get(graph.getNode(key)).get(DWGraph_DS.Direction.IN).keySet()) { // עוברים על כל השכנים הנכנסים של הקודקוד הנוכחי
                int v = i.getKey(); // תופסים את הערך שלהם
                if (!viseted2.get(v))
                    stack.push(v);
            }
        }
    }

    @Override
    public double shortestPathDist(int src, int dest) {
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
    public List<node_data> shortestPath(int src, int dest) {
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
    public boolean save(String file) {
        try {
//            Gson gson = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .enableComplexMapKeySerialization()// אם יש אובייקט יותר מורכב אז שישמור אותו ולא את הסטרינג שלו
//                    .serializeNulls()
//                    .create();

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
    public boolean load(String file) {
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
//                    .enableComplexMapKeySerialization()// אם יש אובייקט יותר מורכב אז שישמור אותו ולא את הסטרינג שלו
//                    .registerTypeAdapter(node_data.class, new NodeDataDeserialize())
//                    .registerTypeAdapter(edge_data.class, new EdgeDataDeserialize())
//                    .registerTypeAdapter(geo_location.class, new GeoLocationDeserialize())
                    .create();

            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            String str = "";
            while (myReader.hasNext())
                str += myReader.nextLine() + "\n";
//
//            this.graph = gson.fromJson(str, DWGraph_DS.class);
//            this.graph =  copy();// בחזרה שהוא מעתיק אז שיעתיק את אותו כתובת בזיכרון
//
//            myReader.close();
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
