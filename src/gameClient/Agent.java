package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;
import org.json.JSONObject;

public class Agent {
    private static int count = 0;
    private static int seed = 3331;
    private int id;
    private geo_location position;
    private double speed;
    private edge_data currentEdge;
    private node_data currentNode;
    private directed_weighted_graph graph;
    private Pokemon currnetPokemon;
    private Pokemon prevPokemon;
    private long _sg_dt;
    private double points;
    private boolean warning;


    public Agent(directed_weighted_graph graph, int start_node) {
        this.graph = graph;
        this.points = 0;
        this.currentNode = graph.getNode(start_node);
        this.position = currentNode.getLocation();
        this.id = -1;
        this.speed = 0;
    }

    public void update(String json) {// Updates the agent according to the data he received from the server
        JSONObject line;
        try {
            line = new JSONObject(json);
            JSONObject agent = line.getJSONObject("Agent");
            int id = agent.getInt("id");
            if (id == this.getId() || this.getId() == -1) {
                if (this.getId() == -1) {
                    this.id = id;
                }
                double speed = agent.getDouble("speed");
                Point3D position = new Point3D(agent.getString("pos"));
                int src = agent.getInt("src");
                int dest = agent.getInt("dest");
                double value = agent.getDouble("value");

                this.position = position;
                this.setCurrNode(src);
                this.setSpeed(speed);
                this.setNextNode(dest);
                this.setPoints(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSrcNode() {
        return this.currentNode.getKey();
    }

    public String toJSON() {

        JsonObject agent = new JsonObject();
        agent.addProperty("id", this.id);
        agent.addProperty("value", this.points);
        agent.addProperty("src", this.currentNode.getKey());
        agent.addProperty("dest", this.getNextNode());
        agent.addProperty("speed", this.getSpeed());
        agent.addProperty("pos", this.position.toString());

        JsonObject obj = new JsonObject();
        obj.add("Agent", agent);
        return obj.toString();

    }

    private void setPoints(double v) {
        points = v;
    }

    public boolean setNextNode(int dest) {
        int src = this.currentNode.getKey();
        this.currentEdge = graph.getEdge(src, dest);// Inserts the current between src to dest into the current edge
        return currentEdge != null;// If the current side exists then it is true automatically
    }

    public void setCurrNode(int src) {
        this.currentNode = graph.getNode(src);
    }





    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", position=" + position +
                ", speed=" + speed +
                ", currentEdge=" + currentEdge +
                ", currentNode=" + currentNode +
                ", graph=" + graph +
                ", currnetPokemon=" + currnetPokemon +
                ", _sg_dt=" + _sg_dt +
                ", points=" + points +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public geo_location getPosition() {
        return position;
    }


    public double getPoints() {
        return points;
    }

    public int getNextNode() {
        if (this.currentEdge == null)
            return -1;
        return this.currentEdge.getDest();
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double v) {
        this.speed = v;
    }

    public Pokemon getCurrnetPokemon() {
        return currnetPokemon;
    }

    public void setCurrnetPokemon(Pokemon currnetPokemon) {
        this.warning = currnetPokemon.equals(this.currnetPokemon);
        this.prevPokemon = this.currnetPokemon;
        this.currnetPokemon = currnetPokemon;
        this.currnetPokemon.setCaught(true);
    }


    public edge_data getCurrentEdge() {
        return this.currentEdge;
    }

    public long get_sg_dt() {
        if (this.currentEdge != null) {
            double w = getCurrentEdge().getWeight();
            geo_location dest = graph.getNode(getCurrentEdge().getDest()).getLocation();
            geo_location src = graph.getNode(getCurrentEdge().getSrc()).getLocation();
            double distFromSrcToDest = src.distance(dest);
            double distFromPosToDest = position.distance(dest);
            if (this.getCurrnetPokemon().getEdge() == this.getCurrentEdge()) {
                distFromPosToDest = currnetPokemon.getPosition().distance(this.position);
            }
            double norm = distFromPosToDest / distFromSrcToDest;
            double dt = w * norm / this.getSpeed();
            return (long) (1000.0 * dt);
        }
        return 0;
    }

    public void set_sg_dt(long _sg_dt) {
        this._sg_dt = _sg_dt;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        return gson.toJson(this);
    }


    public boolean isWarning() {
        return warning;
    }

}
