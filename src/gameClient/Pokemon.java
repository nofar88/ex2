package gameClient;

import api.edge_data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.Objects;

public class Pokemon {
    private edge_data edge;
    private double value;
    private int type;
    private Point3D position;
    private double min_dist;
    private int min_ro;
    private boolean caught;

    public Pokemon(edge_data edge, double value, int type, Point3D position) {
        this.edge = edge;
        this.value = value;
        this.type = type;
        this.position = position;
        min_dist = -1;
        min_ro = -1;
        this.caught = false;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "edge=" + edge +
                ", value=" + value +
                ", type=" + type +
                ", position=" + position +
                '}';
    }

    public static Pokemon initFromJson(String json) {
        Pokemon ans = null;
        try {
            JSONObject p = new JSONObject(json);
            int id = p.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    public edge_data getEdge() {
        return edge;
    }

    public void setEdge(edge_data edge) {
        this.edge = edge;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public boolean isCaught() {
        return caught;
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pokemon pokemon = (Pokemon) o;

        if (Double.compare(pokemon.value, value) != 0) return false;
        if (type != pokemon.type) return false;
        if (!Objects.equals(edge, pokemon.edge)) return false;
        return Objects.equals(position, pokemon.position);
    }
}
