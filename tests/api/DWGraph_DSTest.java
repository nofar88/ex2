package api;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class DWGraph_DSTest {

    @Test
    public void test1() {
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

        assertEquals(12, graph.getMC());

        assertEquals(5, graph.nodeSize());
        assertEquals(7, graph.edgeSize());

        LinkedList<node_data> nodes = new LinkedList<>();
        nodes.add(new NodeData(1));
        nodes.add(new NodeData(2));
        nodes.add(new NodeData(3));
        nodes.add(new NodeData(4));
        nodes.add(new NodeData(5));
        assertIterableEquals(nodes, graph.getV());

        LinkedList<edge_data> edges = new LinkedList<>();
        edges.add(new EdgeData(7, 3, 5));
        edges.add(new EdgeData(10, 3, 4));
        for (edge_data edge: edges) {
            assertTrue(graph.getE(3).contains(edge));
        }
        assertEquals(2, graph.getE(3).size());

        assertEquals(new NodeData(5), graph.getNode(5));
        assertNull(graph.getNode(10));

        assertEquals(new EdgeData(1, 5, 1), graph.getEdge(5, 1));
        assertNull(graph.getEdge(8, 5));

        graph.removeEdge(5, 1);
        assertEquals(6, graph.edgeSize());
        assertEquals(0, graph.getE(5).size());
        assertEquals(13, graph.getMC());

        assertEquals(new EdgeData(1, 4, 3), graph.getE(4).iterator().next());
        graph.removeNode(3);
        assertEquals(4, graph.nodeSize());
        assertEquals(2, graph.edgeSize());
        assertNull(graph.getEdge(2, 3));
        assertNull(graph.getNode(3));
        assertEquals(0, graph.getE(4).size());
        assertEquals(14, graph.getMC());

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(graph);
        algo.save("graph1.json");
        algo.load("graph1.json");
        System.out.println(algo.getGraph());
        assertEquals(graph, algo.getGraph());
    }


    @Test
    public void nodesTest1() {
        DWGraph_DS graph = new DWGraph_DS();
        for (int i = 0; i < 100; i++) {
            graph.addNode(new NodeData(i));
            graph.getNode(i).setInfo("info " + i);

        }
        assertEquals(100, graph.nodeSize());
        assertEquals(100, graph.getV().size());
        assertEquals(5, graph.getNode(5).getKey());
        assertEquals("info 5", graph.getNode(5).getInfo());
        assertEquals(6, graph.removeNode(6).getKey());
        assertEquals(99,graph.nodeSize() );
        assertEquals(null, graph.removeNode(101));
    }

    @Test
    public void nodesTest2() {
        DWGraph_DS graph = new DWGraph_DS();
        assertEquals(0, graph.nodeSize());
        assertEquals(0, graph.getV().size());
        assertThrows(NullPointerException.class, () -> graph.getNode(5).getKey());
    }

    @Test
    public void edgesTest1() {
        DWGraph_DS graph = new DWGraph_DS();
        for (int i = 1; i <= 10; i++) {
            graph.addNode(new NodeData(i));
        }
        graph.connect(1, 2, 5);
        graph.connect(1, 8, 4);
        graph.connect(9, 10, 0);
        graph.connect(5, 2, 3);

        assertEquals(4, graph.edgeSize());
        assertEquals(new EdgeData(5, 1, 2), graph.getEdge(1, 2));
        assertNull(graph.getEdge(2, 1));
        assertNotNull(graph.getEdge(1, 8));
        assertNull(graph.getEdge(8, 1));
        assertNull(graph.getEdge(4, 5));
        assertEquals(2, graph.getE(1).size());
        assertEquals(0, graph.getE(4).size());
        graph.removeEdge(1, 8);
        assertEquals(3, graph.edgeSize());
        assertEquals(1, graph.getE(1).size());
        graph.removeNode(9);
        assertEquals(2, graph.edgeSize());
        assertEquals(0, graph.getE(10).size());
        graph.connect(1, 2, -9);
        graph.connect(1, 4, -9);
        assertEquals(2, graph.edgeSize());// If the weight is negative then the number of ribs does not change.
        assertEquals(5, graph.getEdge(1, 2).getWeight());
        assertNull(graph.getEdge(11, 8));
        assertEquals(16, graph.getMC());
    }

    @Test
    public void  edgesTestEmptyGraph() { // Number of ribs in an empty hip
        DWGraph_DS graph = new DWGraph_DS();
        assertNull(graph.getEdge(1,2));
        graph.removeEdge(1,5);
        assertEquals(0, graph.nodeSize());
        assertEquals(0, graph.edgeSize());
        assertNull(graph.getEdge(1, 6));
        assertEquals(0, graph.getMC());
    }

    @Test
    public void edgesTestnumberOnFullGraph() {// A test that tests a full graph
        DWGraph_DS graph = new DWGraph_DS();
        for (int i = 1; i <= 5; i++) {
            graph.addNode(new NodeData(i));
        }

        for (int i = 1; i <=5 ; i++) {
            for (int j = 1; j <=5 ; j++) {
                graph.connect(i,j,5);
            }
        }

        assertEquals(new EdgeData(5, 1, 2), graph.getEdge(1,2));
        assertNotEquals(new EdgeData(5, 1, 2), graph.getEdge(2,1));
        assertEquals(new EdgeData(5, 2, 1), graph.getEdge(2,1));
        graph.removeEdge(1,5);
        assertEquals(19, graph.edgeSize());
        assertNull(graph.getEdge(1,6));
        assertEquals(26, graph.getMC());
    }

    @Test
    public void testTime(){
        long start = new java.util.Date().getTime();
        DWGraph_DS graph = new DWGraph_DS();
        for (int i = 0; i <=Math.pow(10,6) ; i++) {
            graph.addNode(new NodeData(i));
        }
        for (int i = 0; i <=15 ; i++) {
            for (int j = i; j <Math.pow(10,6) ; j++) {
                graph.connect(i,j,1);
            }
        }
        long end =new java.util.Date().getTime();
        double dt= (end-start)/1000.0;
        assertTrue(dt < 10);

    }

    @Test
    public void checkConnectBoaz()
    {
        DWGraph_Algo algo = new DWGraph_Algo();
        for (int i = 0; i < 6; i++) {
            assertTrue(algo.load("data/A" + i));
            assertTrue(algo.isConnected());
        }
    }






}
