package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.game_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.util.*;

public class EX2 implements Runnable {
    private ArenaUs arena;
    private GraphFrame frame;
    private game_service server;
    private DWGraph_Algo algo;
    private long sleep;
    private long prevSleep;
    private static int sen;
    private static long id;

    public static void main(String[] args) {
        if(args.length > 0){
            id = Long.parseLong(args[0]);
            sen = Integer.parseInt(args[1]);
        }
        else {
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
        Thread client = new Thread(new EX2());
        client.start();
    }

    @Override
    public void run() {
        server = Game_Server_Ex2.getServer(sen);
        // ארנה זה איך שאני שומרת את הנתונים אצלי ולא רק על השרת

        arena = new ArenaUs();// לבנות ארנה חדשה
        arena.setGraph(server.getGraph());// שומרים את הגרף בארנה
        arena.setPokemons(server.getPokemons());// שומרים את הפוקמונים בארנה

        algo = new DWGraph_Algo();
        algo.init(arena.getGraph());// מאתחלים גרף אלגו ומתאחלים אותו עם הגרף של הסנריו הנתון

        locateAgents();

        frame = new GraphFrame("test Ex2");// בונה פריים חדשה
        frame.setSize(1000, 700);
        frame.update(arena, server); // תשים בתוך הפריים את הארנה שיש לנו
        frame.show();

        server.startGame();
        while (server.isRunning()) {
            try {
                moveAgants();
                frame.update(arena, server);
                frame.repaint();
                Thread.sleep(sleep);
                //Thread.sleep(150 + new Random().nextInt(100)); // שינוי כמות זמן השינה באופן רנדומלי כדי שנוכל להגיע ממש לפוקמון
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        System.out.println(server);
        System.exit(0);
    }

    private void locateAgents() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject jsonObject = gson.fromJson(server.toString(), JsonObject.class);
        int numOfAgents = jsonObject.get("GameServer").getAsJsonObject().get("agents").getAsInt();  // קבלת מספר הסוכנים שצריך מתוך הJSON של השרת

        //arena.getPokemons().sort((a, b) -> a.getValue() > b.getValue() ? -1 : a.getValue() == b.getValue() ? 0 : 1);
        LinkedList<Agent> agents = new LinkedList<>(); // יוצרים רשימת סוכנים חדשה
        for (int i = 0; i < numOfAgents; i++) {
            int src = arena.getPokemons().get(i % arena.getPokemons().size()).getEdge().getSrc();  // תביא לי את הםוקימונים, כל פוקימון I תביא לי את הקודוקד הנכנס לצלע שהוא נמצא בה
            int dest = arena.getPokemons().get(i % arena.getPokemons().size()).getEdge().getDest();  // תביא לי את הםוקימונים, כל פוקימון I תביא לי את הקודוקד יוצא לצלע שהוא נמצא בה

            Agent agent = new Agent(arena.getGraph(), src);
            agent.setNextNode(dest);
            agent.setCurrnetPokemon(arena.getPokemons().get(i));

            agents.add(agent);  // תמקם את הסוכן על הקודקוד שמצאנו
            server.addAgent(src);  // תעדכן את הסוכן בשרת
        }
        arena.setAgents(agents);// שומרים את  הסוכנים בתוך הארנה
        arena.updateAgents(server.getAgents(), arena.getGraph());// מעדכנים את הסוכנים בהתאם למה שיש בשרת
    }

    private void moveAgants() {
        prevSleep = sleep;
        sleep = Long.MAX_VALUE;
        List<Agent> log = arena.updateAgents(server.getAgents(), arena.getGraph());
        arena.setPokemons(server.getPokemons());
        for (Agent agent : log) {
            int dest = agent.getNextNode();
            if (dest == -1) {
                Pokemon pocemon = searchPokemon(agent);
                agent.setCurrnetPokemon(pocemon);
                System.out.println("Agent " + agent.getId() + " going to pokemon " + pocemon.getEdge());
                if (agent.getSrcNode() == pocemon.getEdge().getSrc()) {
                    server.chooseNextEdge(agent.getId(), pocemon.getEdge().getDest());
                    agent.setNextNode(pocemon.getEdge().getDest());
                } else {
                    dest = algo.shortestPath(agent.getSrcNode(), pocemon.getEdge().getSrc()).get(1).getKey();
                    server.chooseNextEdge(agent.getId(), dest);
                    agent.setNextNode(dest);
                }
            }
            if (agent.get_sg_dt() < sleep) {
                sleep = agent.get_sg_dt();
            }
        }
        server.move();
    }

    private Pokemon searchPokemon(Agent agent) {
//        double minDistance = Double.MAX_VALUE; // אתחול הערך המינימילי
//        Pokemon minPokemon = arena.getPokemons().iterator().next();
//        for (Pokemon pokemon:arena.getPokemons()) {// רצים על כל הפוקמונים
//            double temp = algo.shortestPathDist(src, pokemon.getEdge().getSrc()); // שולחים לדיאקסטרה ומקבלים את המסלול הכי קצר בין הקודקוד נכנס שהסוכן עליו לבין הפוקמון שהכי קרוב אליו
//            if (temp < minDistance && !pokemon.isCaught() && temp != -1) {// כל עוד הפוקמון לא תפוס, וגם המשנתה הזמני קטן יותר מהמינלי אז תחליף
//                minDistance = temp;
//                minPokemon = pokemon;
//            }
//        }
        arena.getPokemons().sort((p1, p2) -> {
            if (p1.isCaught() && p2.isCaught())
                return 0;
            if (p1.isCaught())
                return -1;
            if (p2.isCaught())
                return 1;
            double dist1 = algo.shortestPathDist(agent.getSrcNode(), p1.getEdge().getSrc());
            double dist2 = algo.shortestPathDist(agent.getSrcNode(), p2.getEdge().getSrc());
            return Double.compare(dist1, dist2);
        });
        if(agent.isWarning() && arena.getPokemons().size() > 1)
            return arena.getPokemons().get(1);
        return arena.getPokemons().get(0);
    }
}
