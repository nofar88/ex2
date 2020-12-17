package gameClient;

import api.*;

import javax.swing.*;
import java.awt.*;

public class GraphFrame extends JFrame {
    private GraphPanel panel;

    GraphFrame(String title) {
        super(title);
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (dim.width+0.6), (int) (dim.height+0.6));

        panel = new GraphPanel();
        this.add(panel);

        this.setVisible(true);
    }

    public void update(Arena arena, game_service server) {
        this.panel.update(arena, server);
    }
}
