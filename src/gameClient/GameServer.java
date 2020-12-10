package gameClient;

import api.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;

public class GameServer implements game_service {

    LinkedList<Pokemon> pokemons;
    LinkedList<Agent> agents;
    dw_graph_algorithms graph;
    long startTime;
    long gameTime;
    boolean isRunning = false;

    public GameServer(int sene) {
        this.graph = new DWGraph_Algo();
        this.graph.load("data/A" + sene);
        this.pokemons = new LinkedList<>();
        this.pokemons.add(new Pokemon(new EdgeData(1, 2, 3), 10,
                -1, new Point3D(1, 2, 3)));
        this.agents = new LinkedList<>();
    }

    @Override
    public String getGraph() {
        try {
            JsonArray edges = new JsonArray();
            JsonArray nodes = new JsonArray();

            for (node_data node : graph.getGraph().getV()) {
                JsonObject newNode = new JsonObject();
                try {
                    geo_location location = node.getLocation();
                    newNode.addProperty("pos", location.x() + "," + location.y() + "," + location.z());
                } catch (Exception ex) {
                    newNode.addProperty("pos", "0.0,0.0,0.0");
                }
                newNode.addProperty("id", node.getKey());
                nodes.add(newNode);

                for (edge_data edge : graph.getGraph().getE(node.getKey())) {
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

            return obj.toString();// אם הצליח לעבור את הכל אז שיחזיר את זה כסטרינג של קובץ של קובץ גיסון
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getPokemons() {
        String json = "{[";
        for (Pokemon pokemon : pokemons) {
            json += pokemon.toJson() + ",";
        }
        json = json.substring(0, json.length() - 2) + "]}";
        return json;
    }

    @Override
    public String getAgents() {
        String json = "{[";
        for (Agent agent : agents) {
            json += agent.toJson() + ",";
        }
        json = json.substring(0, json.length() - 2) + "]}";
        return json;
    }

    @Override
    public boolean addAgent(int start_node) {
        try {
            agents.add(new Agent(this.graph.getGraph(), start_node));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public long startGame() {
        isRunning = true;
        startTime = new Date().getTime();
        return startTime;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public long stopGame() {
        isRunning = false;
        return (new Date().getTime());
    }

    @Override
    public long chooseNextEdge(int id, int next_node) {
        try {
            // https://www.baeldung.com/find-list-element-java
            Agent agent = agents.stream()
                    .filter(agent1 -> agent1.getId() == id)
                    .findAny()
                    .orElse(null);// מחפש לראות את הסוכן שקיבנו נמצא בכלל בגייסון
            if (agent.setNextNode(next_node))// אם הוא מצא אותו
                return new Date().getTime();// מחזיר את השעה הנוכחית
        }
        catch (Exception ignored){}
        return -1;
    }

    @Override
    public long timeToEnd() {
        long now = new Date().getTime();
        long differenceInTimes =  now - startTime;// מחזיק את ההפרש זמנים מהרגע שהתלנו עד עכשיו
        return gameTime - differenceInTimes;// מחזיר את הזמן שנותר למשחק
    }

    @Override
    public String move() {
        return null;
    }

    @Override
    public boolean login(long id) {
        return false;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }
}
