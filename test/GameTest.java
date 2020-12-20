import Server.Game_Server_Ex2;
import api.*;
import gameClient.Agent;
import gameClient.Arena;
import gameClient.Pokemon;
import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void arenatest(){
        game_service game = Game_Server_Ex2.getServer(0);
        Arena arena = new Arena();
        DWGraph_Algo algo = new DWGraph_Algo();

        arena.setGraph(game.getGraph());
        arena.setPokemons(game.getPokemons());

        assertEquals(1, arena.getPokemons().size());
        assertTrue(algo.load("data/A0"));
        assertEquals(algo.getGraph(), arena.getGraph());

        game.addAgent(0);
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(algo.getGraph(), 0));
        arena.setAgents(agents);

        game.startGame();
        arena.updateAgents(game.getAgents());
        assertEquals(agents.get(0), arena.getAgents().get(0));
        game.stopGame();
    }

    @Test
    public void pokemonTest(){
        game_service game = Game_Server_Ex2.getServer(1);
        Arena arena = new Arena();
        arena.setGraph(game.getGraph());
        arena.setPokemons(game.getPokemons());

        Pokemon pokemon = arena.getPokemons().get(0);
        pokemon.setCaught(true);

        assertEquals(new EdgeData(1.4575484853801393, 9, 8), pokemon.getEdge());
        assertTrue(pokemon.isCaught());
        assertEquals(5, pokemon.getValue());
        assertEquals(new Point3D(35.197656770719604,32.10191878639921,0.0), pokemon.getPosition());
        assertEquals(-1, pokemon.getType());
    }

    @Test
    public void agentTest() {
        game_service game = Game_Server_Ex2.getServer(1);
        Arena arena = new Arena();
        arena.setGraph(game.getGraph());
        arena.setPokemons(game.getPokemons());

        game.addAgent(0);
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(arena.getGraph(), 0));
        arena.setAgents(agents);
        game.startGame();
        arena.updateAgents(game.getAgents());

        Agent agent = arena.getAgents().get(0);

        assertEquals(0, agent.getId());
        assertEquals(0, agent.getPoints());
        assertEquals(0, agent.getSrcNode());
        assertEquals(-1, agent.getNextNode());
        assertEquals(1, agent.getSpeed());
        assertEquals(new Point3D(35.18753053591606,32.10378225882353,0.0), agent.getPosition());

        Pokemon pokemon = new Pokemon(new EdgeData(1, 2, 1), 1, 1, new Point3D(0, 0, 0));
        agent.setCurrnetPokemon(pokemon);
        agent.setNextNode(pokemon.getEdge().getDest());

        assertEquals(pokemon, agent.getCurrnetPokemon());
        assertEquals(1, agent.getNextNode());
        game.stopGame();

    }
}
