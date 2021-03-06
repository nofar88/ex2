package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class GraphPanel extends JPanel {

    private Arena arena;
    private gameClient.util.Range2Range _w2f;
    private game_service server;



    public void update(Arena arena, game_service server) {
        this.arena = arena;
        this.server = server;
        updateFrame();
    }

    private void updateFrame() {
        Range rx = new Range(70, this.getWidth() - 70);
        Range ry = new Range(this.getHeight() - 100, 100);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = arena.getGraph();
        _w2f = Arena.w2f(g, frame);
    }

    public void paint(Graphics g) {
        try {
            int w = this.getWidth();
            int h = this.getHeight();
            g.clearRect(0, 0, w, h);


            drawGraph(g);
            drawPokemons(g);
            drawAgants(g);
            drawData(g);
        }
        catch (Exception ex){

        }
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        LinkedList<edge_data> edges = new LinkedList<>();
        for (node_data n : gg.getV()) {
            edges.addAll(gg.getE(n.getKey()));
        }
        for (edge_data e : edges) {
            g.setColor(Color.gray);
            drawEdge(e, g);
        }
        for (node_data n : gg.getV()) {
            g.setColor(Color.blue);
            drawNode(n, 5, g);
        }
    }
    private void drawPokemons(Graphics g) {
        List<Pokemon> pokemons = arena.getPokemons();
        if(pokemons!=null) {
            for (Pokemon pokemon : pokemons) {
                Point3D location = pokemon.getPosition();
                int radius = 20;
                g.setColor(Color.green);
                if (pokemon.getType() < 0) {
                    g.setColor(Color.orange);
                }
                if (location != null) {

                    geo_location fp = this._w2f.world2frame(location);

                    try {
                        BufferedImage img = ImageIO.read(getClass().getResource("/gameClient/poc.png"));
                        g.drawImage(img, (int) fp.x() - radius, (int) fp.y() - radius, null);
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }

                }
            }
        }
    }
    private void drawAgants(Graphics g) {
        List<Agent> agents = arena.getAgents();
        int i = 0;
        while (agents != null && i < agents.size()) {
            geo_location c = agents.get(i).getPosition();
            int radius = 8;
            i++;
            if (c != null) {
                geo_location fp = this._w2f.world2frame(c);
                try {
                    BufferedImage img = ImageIO.read(getClass().getResource("/gameClient/agent.png"));
                    g.drawImage(img, (int) fp.x() - radius, (int) fp.y() - radius, null);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private void drawNode(node_data n, int radius, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int) fp.x() - radius, (int) fp.y() - radius, 2 * radius, 2 * radius);
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * radius);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        geo_location src = gg.getNode(e.getSrc()).getLocation();
        geo_location dest = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(src);
        geo_location d0 = this._w2f.world2frame(dest);
        drawThickLine(g, (int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y(), 2, Color.GRAY);
    }


    /**
    A function that draws a thick line
    https://www.rgagnon.com/javadetails/java-0260.html
     */
    public void drawThickLine(
            Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
        // The thick line is in fact a filled polygon
        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;
        // line length
        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double)(thickness) / (2 * lineLength);

        // The x,y increments from an endpoint needed to create a rectangle...
        double ddx = -scale * (double)dY;
        double ddy = scale * (double)dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int)ddx;
        int dy = (int)ddy;

        // Now we can compute the corner points...
        int xPoints[] = new int[4];
        int yPoints[] = new int[4];

        xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;

        g.fillPolygon(xPoints, yPoints, 4);
    }

    /**
     A function that writes the time left for the game and the agents' score
     */

    private void drawData(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.drawString("Countdowen: " + ((int)server.timeToEnd()/1000) + " sec", 15, 50);
        g.setColor(Color.GRAY);
        int y = 65;
        for(Agent agent :  arena.getAgents()) {
            g.drawString("Agent " + agent.getId() + ": " + ((int) agent.getPoints()) + " points", 15, y);
            y += 15;
        }
    }
}
