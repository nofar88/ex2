package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.game_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.util.*;

public class Ex2 implements Runnable {
    private Arena arena;
    private GraphFrame frame;
    private game_service game;
    private DWGraph_Algo algo;
    private long sleep;
    private static int sen;
    private static long id;

    public static void main(String[] args) {
        if(args.length > 1){// For the Terminal
            id = Long.parseLong(args[0]); // The zero position is the id
            sen = Integer.parseInt(args[1]);// Position number two is for the scenario number
        }
        else {// Case we are not running through the terminal but through the box
            id = Integer.MAX_VALUE;
            String comment = "";
            while (id == Integer.MAX_VALUE) {
                try {
                    id = Long.parseLong(JOptionPane.showInputDialog("Enter ID " + comment + ": "));
                } catch (Exception ignored) {
                    comment = "(only numbers)";
                }
            }
            sen = Integer.MAX_VALUE;
            comment = "";
            while (sen == Integer.MAX_VALUE) {
                try {
                    sen = Integer.parseInt(JOptionPane.showInputDialog("Enter scenario " + comment + ": "));
                } catch (Exception ignored) {
                    comment = "(only numbers)";
                }
            }
        }
        Thread client = new Thread(new Ex2());// Create a new thread for the game
        client.start();// Start the thread
    }

    @Override
    public void run() {
        game = Game_Server_Ex2.getServer(sen);// Requests from the server the object of the game according to the scenario
        // Arena is how I keep the data with me and not just on the server

        game.login(id);

        arena = new Arena();// Build a new arena
        arena.setGraph(game.getGraph());// Keep the graph in the arena
        arena.setPokemons(game.getPokemons());// Save the Pokemon in the arena

        algo = new DWGraph_Algo();
        algo.init(arena.getGraph());// Initialize an algo graph with the graph of the given scenario

        locateAgents();// Place the agents

        frame = new GraphFrame("pokemon game "+ sen);// Builds a new frame
        frame.setSize(1000, 700);
        frame.update(arena, game); // Put inside the frame the arena we have
        frame.show();

        game.startGame();// Start the game
//        while (game.isRunning()) {
//            try {
//                moveAgants();
//                frame.update(arena, game);
//                frame.repaint();
//                Thread.sleep(sleep);
//                //Thread.sleep(150 + new Random().nextInt(100)); // Change the amount of sleep time randomly so we can get to Pokemon right
//            } catch (Exception ex) {
//                System.out.println(ex);
//            }
//        }
        int ind=0;
        long dt=110;

        System.out.println(game.getAgents());
        while(game.isRunning()) {
            moveAgants();
            try {
                frame.update(arena, game);
                frame.repaint();
                Thread.sleep(dt);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(game);
        System.exit(0);// Stop the thread that the game is over
    }

    private void locateAgents() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject jsonObject = gson.fromJson(game.toString(), JsonObject.class);// Conversion of String to a Json object
        int numOfAgents = jsonObject.get("GameServer").getAsJsonObject().get("agents").getAsInt();  // Getting the number of agents needed from the server's JSON

        arena.getPokemons().sort((p1, p2) -> -1 * Double.compare(p1.getValue(), p2.getValue()));// Sort the Pokemon according to their value from the highest to the lowest so that at first the agents will be placed next to the Pokemon with the highest value

        LinkedList<Agent> agents = new LinkedList<>(); // Create a new list of agents
        for (int i = 0; i < numOfAgents; i++) {// Match each agent to the Pokemon I.
            int src = arena.getPokemons().get(i % arena.getPokemons().size()).getEdge().getSrc();  // Bring me the Pokemon, for each Pokemon I will bring me the vertex that enters the edge it is on
           int dest = arena.getPokemons().get(i % arena.getPokemons().size()).getEdge().getDest();  // Bring me the Pokemon, to each Pokemon I bring me the out vertex to the edge it is on

            Agent agent = new Agent(arena.getGraph(), src);// Create a new agent in the right location
            agent.setNextNode(dest);// Tell the agent where he is going
            agent.setCurrnetPokemon(arena.getPokemons().get(i));// Keeping the Pokemon inside the agent

            agents.add(agent);  // Place the agent on the vertex we found
            game.addAgent(src);  // Update the agent on the server
        }
        arena.setAgents(agents);// Keep the agents inside the arena
        arena.updateAgents(game.getAgents());// Update the agents according to what is on the server
    }

    private void moveAgants() {
        sleep = Long.MAX_VALUE;
        List<Agent> log = arena.updateAgents(game.getAgents());// Update of the arena on the movement of the agents through the server
        arena.setPokemons(game.getPokemons());// Updates the arena on the location of the Pokemon
        for (Agent agent : log) {
            int dest = agent.getNextNode();
            if (dest == -1) {// That means he's currently on node and has nowhere to go yet
                Pokemon pocemon = searchPokemon(agent);// The function returns the Pokemon closest to the agent
                agent.setCurrnetPokemon(pocemon);// Update of the agent who is the closest Pokemon to him

                if (agent.getSrcNode() == pocemon.getEdge().getSrc()) {// A case that relates to a situation where the agent is on the edge where the Pokemon is
                    game.chooseNextEdge(agent.getId(), pocemon.getEdge().getDest());// Tells the server that in the next move he will go to the vertex after the Pokemon that is to eat the Pokemon
                    agent.setNextNode(pocemon.getEdge().getDest());// Update for the agent  where he is going and not just that the server knew
                } else {// In case the edge where the agent is located there is no Pokemon on it
                    dest = algo.shortestPath(agent.getSrcNode(), pocemon.getEdge().getSrc()).get(1).getKey();// Calculate the shortest route to the next nearest Pokemon, and tell it to go in the direction of the Pokemon (we put 1 because the function starts from zero because zero is where the agent is)
                    game.chooseNextEdge(agent.getId(), dest);// Update the server where the agent is intended to go
                    agent.setNextNode(dest);// Update the agent where it is intended to go so that the information will not only be on the server
                }
            }

//            if (agent.get_sg_dt() < sleep) {// Finds the minimum sleep time
//                sleep = agent.get_sg_dt();
//            }
        }
        game.move();// After we have told the agents where each one is meant to go then he will actually move them
    }

    private Pokemon searchPokemon(Agent agent) {

        arena.getPokemons().sort((pokemon1, pokemon2) -> {//    Sort all the Pokemon according to their distance from the agent and that they are not caught
            if (pokemon1.isCaught() && pokemon2.isCaught())// If both are caught
                return 0;// If they are equal then they have the same priority
            if (pokemon1.isCaught())// If one of them is caught then gives priority to the other
                return -1;
            if (pokemon2.isCaught())// If one of them is caught then gives priority to the other
                return 1;
            // A case where the two Pokemon available to him are not caught
            double dist1 = algo.shortestPathDist(agent.getSrcNode(), pokemon1.getEdge().getSrc());
            double dist2 = algo.shortestPathDist(agent.getSrcNode(), pokemon2.getEdge().getSrc());
            return Double.compare(dist1, dist2);
        });

        // If the agent has tried to go through Pokemon more than once then he will move on (only if there is more than one Pokemon in the game)
        if(agent.isWarning() && arena.getPokemons().size() > 1)
            return arena.getPokemons().get(1);

        return arena.getPokemons().get(0);// Aim for the Pokemon closest to it even if there is only one
    }
}
