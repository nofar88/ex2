package api;

import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DWGraph_AlgoTest {
    @Test
    public void connectedTest()
    {
        DWGraph_DS graph = new DWGraph_DS();
        graph.addNode(new NodeData(1));
        graph.addNode(new NodeData(2));
        graph.addNode(new NodeData(3));
        graph.addNode(new NodeData(4));
        graph.addNode(new NodeData(5));
        graph.connect(1, 2, 3);
        graph.connect(2, 3, 4);
        graph.connect(3, 4, 10);
        graph.connect(4, 3, 1);
        graph.connect(3, 5, 7);
        graph.connect(5, 1, 1);
        graph.connect(1, 5, 2);

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);
        assertTrue(algo.isConnected());

        graph.removeEdge(1, 5);
        assertTrue(algo.isConnected());

        graph.removeEdge(5, 1);
        assertFalse(algo.isConnected());

        graph.connect(5, 1, 1);
        graph.removeNode(3);
        assertFalse(algo.isConnected());

        graph.connect(5, 2, 1);
        assertFalse(algo.isConnected());

        graph.removeNode(4);
        graph.connect(2, 5, 1);
        assertTrue(algo.isConnected());
    }



    @Test
    public void copyTest() {
        DWGraph_DS graph = new DWGraph_DS();
        for (int i = 0; i < 50; i++) {
            graph.addNode(new NodeData(i));
            graph.getNode(i).setInfo("info " + i);
            graph.getNode(i).setTag(i);

        }
        graph.connect(1, 2, 5);
        graph.connect(2, 3, 5);
        graph.connect(3, 4, 5);
        assertEquals(50, graph.nodeSize());
        assertEquals(3, graph.edgeSize());

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);

        assertEquals(graph, algo.getGraph());

        DWGraph_DS graphcopy = (DWGraph_DS) algo.copy();
        assertEquals(graph, graphcopy);
        graphcopy.removeNode(4);
        assertEquals(2, graphcopy.edgeSize());
        assertEquals(49, graphcopy.nodeSize());
        assertEquals(50, graph.nodeSize());

        graphcopy.getNode(30).setTag(100);
        assertEquals(30, graph.getNode(30).getTag());
        assertEquals(100, graphcopy.getNode(30).getTag());

        graphcopy.getNode(30).setInfo("100");
        assertEquals("info 30", graph.getNode(30).getInfo());
        assertEquals("100", graphcopy.getNode(30).getInfo());

        graph.removeNode(15);
        assertNotEquals(graph, graphcopy);

        assertEquals(15, algo.shortestPathDist(1, 4));
        algo.init(graphcopy);
        assertEquals(-1, algo.shortestPathDist(1, 4));
        assertEquals(10, algo.shortestPathDist(1, 3));
        assertEquals(-1, algo.shortestPathDist(1, 5));
    }

    @Test
    public void emptyGraph() {
        DWGraph_DS graph = new DWGraph_DS();
        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);
        assertTrue(algo.isConnected());
        assertEquals(-1, algo.shortestPathDist(1, 4));
        assertNull(algo.shortestPath(4, 6));
    }

    @Test
    public void weightedGraphNotlinked() {// test that tests a graph that is not a link
        DWGraph_DS graph = new DWGraph_DS();
        graph.addNode(new NodeData(1));
        graph.addNode(new NodeData(2));
        graph.addNode(new NodeData(3));
        graph.addNode(new NodeData(4));
        graph.addNode(new NodeData(5));
        graph.addNode(new NodeData(6));
        graph.connect(1, 2, 5);
        graph.connect(2, 3, 5);
        graph.connect(4, 5, 5);
        graph.connect(2, 6, 10);
        graph.connect(6, 3, 30);
        graph.connect(3, 2, 0);
        graph.connect(3, 2, -1);
        graph.connect(1, 3, 1);

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);
        assertFalse(algo.isConnected());
        assertEquals(1, algo.shortestPathDist(1, 3));
        graph.removeEdge(1, 3);
        assertEquals(10, algo.shortestPathDist(1, 3));
        assertEquals(5, algo.shortestPathDist(1, 2));
        assertEquals(-1, algo.shortestPathDist(3, 5));
        assertEquals(14, graph.getMC());
        assertEquals(15, algo.shortestPathDist(1, 6));
        graph.connect(2, 1, 10);
        assertEquals(30, algo.shortestPathDist(6, 3));
        assertEquals(0, algo.shortestPathDist(3, 2));
        assertEquals(10, algo.shortestPathDist(2, 1));

        assertEquals(40, algo.shortestPathDist(6, 1));
    }


    @Test
    public void shorterRouteLinkGraph() {// Checking a short route in a link graph
        DWGraph_DS graph = new DWGraph_DS();
        graph.addNode(new NodeData(1));
        graph.addNode(new NodeData(2));
        graph.addNode(new NodeData(3));
        graph.addNode(new NodeData(4));
        graph.addNode(new NodeData(5));
        graph.addNode(new NodeData(6));
        graph.connect(1, 2, 1);
        graph.connect(1, 3, 2);
        graph.connect(3, 6, 1);
        graph.connect(2, 6, 5);
        graph.connect(2, 4, 1);
        graph.connect(4, 5, 1);
        graph.connect(3, 5, 3);
        graph.connect(6, 2, 3);
        graph.connect(3, 1, 9);
        graph.connect(5, 3, 1);

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);

        assertEquals(3, algo.shortestPathDist(1, 6));
        assertEquals(3, algo.shortestPathDist(1, 5));
        assertEquals(2, algo.shortestPathDist(2, 5));
        assertEquals(12, algo.shortestPathDist(2, 1));
        assertEquals(3, algo.shortestPathDist(4, 6));
        assertTrue(algo.isConnected());

        List<node_data> temp = algo.shortestPath(1, 6);
        String path = "";
        for (node_data node : temp) {
            path += node.getKey() + "";
        }
        assertEquals("136", path);

        graph.removeEdge(1, 3);
        assertEquals(5, algo.shortestPathDist(1, 6));
        assertTrue(algo.isConnected());
        temp = algo.shortestPath(1, 6);
        path = "";
        for (node_data node : temp) {
            path += node.getKey() + "";
        }
        assertEquals("124536", path);
    }


    @Test
    public void saveAndLoad() {
        DWGraph_DS graph = new DWGraph_DS();
        graph.addNode(new NodeData(1));
        graph.addNode(new NodeData(2));
        graph.addNode(new NodeData(3));
        graph.addNode(new NodeData(4));
        graph.connect(1, 3, 6);
        graph.connect(3, 4, 10);
        graph.connect(4, 2, 5);
        graph.connect(2, 1, 10);

        graph.getNode(1).setLocation(new Point3D(1, 2, 3));

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);
        assertEquals(new EdgeData(10, 3, 4), graph.getEdge(3, 4));
//        assertEquals(16, algo.shortestPathDist(1, 4));
        assertTrue(algo.save("graph2.json"));
        assertTrue(algo.load("graph2.json"));
        assertEquals(graph, algo.getGraph());
        assertTrue(algo.isConnected());
        assertEquals(16, algo.shortestPathDist(1, 4));
        assertEquals(graph, algo.getGraph());
        assertFalse(algo.load("abc"));
    }

    @Test
    public void extremeCases() {
        DWGraph_DS graph = new DWGraph_DS();
        graph.addNode(new NodeData(1));
        graph.addNode(new NodeData(2));
        graph.addNode(new NodeData(3));
        graph.addNode(new NodeData(4));
        graph.addNode(new NodeData(4));
        graph.addNode(new NodeData(6));

        graph.connect(1, 3, 6);
        graph.connect(3, 4, 10);
        graph.connect(4, 2, 5);
        graph.connect(2, 1, 10);
        graph.connect(2, 1, 10);
        graph.connect(2, 1, 10.9);


        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);


        assertEquals(new EdgeData(10, 3, 4), graph.getEdge(3, 4));
        assertNull(graph.getEdge(4, 3));
        assertNull(graph.getEdge(6, 4));
        assertNull(algo.shortestPath(1, 6));
        assertEquals(10, graph.getMC());
        graph.removeNode(2);
        assertEquals(4,graph.nodeSize());
        assertEquals(2, graph.edgeSize());
        assertNull(graph.removeNode(2));
        assertFalse(algo.isConnected());
        assertEquals(16, algo.shortestPathDist(1, 4));
        assertEquals(-1, algo.shortestPathDist(1, 2));
        assertNull(algo.shortestPath(1, 2));
        assertNull(algo.shortestPath(1, 6));
        assertEquals(11, graph.getMC());
    }


    @Test
    public void alotOfNodes() {
        DWGraph_DS graph = new DWGraph_DS();
        for (int i = 0; i < 1000000; i++)
            graph.addNode(new NodeData(i));

        for (int i = 0; i < 1000000; i++)
            graph.connect(i, (i+1)%1000000, 1);

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);
        assertTrue(algo.isConnected());
        graph.removeNode(3);
        assertFalse(algo.isConnected());
    }
}
