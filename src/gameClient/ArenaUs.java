package gameClient;

import api.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph - grabs Pokemons and avoid the Zombies.
 * @author boaz.benmoshe
 *
 */
public class ArenaUs {
	public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
	private directed_weighted_graph graph;
	private List<Agent> _agents;
	private List<Pokemon> _pokemons;
	private List<String> _info;
	private static Point3D MIN = new Point3D(0, 100,0);
	private static Point3D MAX = new Point3D(0, 100,0);

	public ArenaUs() {;
		_info = new ArrayList<String>();
	}
//	private ArenaUs(directed_weighted_graph g, List<CL_Agent> r, List<CL_Pokemon> p) {
//		graph = g;
//		this.setAgents(r);
//		this.setPokemons(p);
//	}
	public void setPokemons(String json) {
		ArrayList<Pokemon> ans = new  ArrayList<Pokemon>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray ags = jsonObject.getJSONArray("Pokemons");
			for (int i = 0; i < ags.length(); i++) {
				JSONObject pp = ags.getJSONObject(i);
				JSONObject pokemon = pp.getJSONObject("Pokemon");
				int type = pokemon.getInt("type");
				double value = pokemon.getDouble("value");
				String pos = pokemon.getString("pos");
				Pokemon f = new Pokemon(null, value, type,  new Point3D(pos));
				ans.add(f);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		for (Pokemon pokemon: ans) {
			updateEdge(pokemon, graph);
		}
		this._pokemons = ans;
	}
	public void setAgents(List<Agent> f) {
		this._agents = f;
	}

	public void setGraph(String graph) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
		JsonObject jsonObject = gson.fromJson(graph, JsonObject.class);

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
	}

	private void init( ) {
		MIN=null; MAX=null;
		double x0=0,x1=0,y0=0,y1=0;
		Iterator<node_data> iter = graph.getV().iterator();
		while(iter.hasNext()) {
			geo_location c = iter.next().getLocation();
			if(MIN==null) {x0 = c.x(); y0=c.y(); x1=x0;y1=y0;MIN = new Point3D(x0,y0);}
			if(c.x() < x0) {x0=c.x();}
			if(c.y() < y0) {y0=c.y();}
			if(c.x() > x1) {x1=c.x();}
			if(c.y() > y1) {y1=c.y();}
		}
		double dx = x1-x0, dy = y1-y0;
		MIN = new Point3D(x0-dx/10,y0-dy/10);
		MAX = new Point3D(x1+dx/10,y1+dy/10);
		
	}
	public List<Agent> getAgents() {return _agents;}
	public List<Pokemon> getPokemons() {return _pokemons;}

	
	public directed_weighted_graph getGraph() {
		return graph;
	}
	public List<String> get_info() {
		return _info;
	}
	public void set_info(List<String> _info) {
		this._info = _info;
	}

	////////////////////////////////////////////////////
	public List<Agent> updateAgents(String agentsJson, directed_weighted_graph graph) {
		try {
			JSONArray array = new JSONObject(agentsJson).getJSONArray("Agents");
			for (int i = 0; i < array.length(); i++) {
				_agents.get(i).update(array.get(i).toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return _agents;
	}
//	public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
//
//		return ans;
//	}
	public static void updateEdge(Pokemon pokemon, directed_weighted_graph graph) {
		for (node_data node : graph.getV()) {
			for (edge_data edge : graph.getE(node.getKey())) {
				boolean f = checkTypeOfSuspectEdge(pokemon.getPosition(), edge, pokemon.getType(), graph);
				if (f) {
					pokemon.setEdge(edge);
				}
			}
		}
	}

	private static boolean isOnEdge(geo_location pokemon, int s, int d, directed_weighted_graph g) {
		geo_location src = g.getNode(s).getLocation();  // מקבל את המיקום של הקודקודים שמחוברים לצלע
		geo_location dest = g.getNode(d).getLocation();
		double dist = src.distance(dest); // מחשב את האורך של הצלע
		double distance = src.distance(pokemon) + pokemon.distance(dest);  // מחשב את החיבור של החלקים שמוםרדים ע"י הפוקימון
		return dist > distance - EPS2; // יחזיר כן אם זה עומד בסטיית תקן של המרחק
	}

	private static boolean checkTypeOfSuspectEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
		int src = g.getNode(e.getSrc()).getKey();
		int dest = g.getNode(e.getDest()).getKey();
		if (type < 0 && dest > src) {
			return false;
		}
		if (type > 0 && src > dest) {
			return false;
		}
		return isOnEdge(p, src, dest, g);
	}

	private static Range2D GraphRange(directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
		double x0=0,x1=0,y0=0,y1=0;
		boolean first = true;
		while(itr.hasNext()) {
			geo_location p = itr.next().getLocation();
			if(first) {
				x0=p.x(); x1=x0;
				y0=p.y(); y1=y0;
				first = false;
			}
			else {
				if(p.x()<x0) {x0=p.x();}
				if(p.x()>x1) {x1=p.x();}
				if(p.y()<y0) {y0=p.y();}
				if(p.y()>y1) {y1=p.y();}
			}
		}
		Range xr = new Range(x0,x1);
		Range yr = new Range(y0,y1);
		return new Range2D(xr,yr);
	}
	public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
		Range2D world = GraphRange(g);
		Range2Range ans = new Range2Range(world, frame);
		return ans;
	}

}
