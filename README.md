# EX2
## directed  weighted graph:
### The project was about a weighted directed graph,
 ### A graph consisting of nodes and edges where each edge has a weight
### We got 4 interfaces that we had to implement: node_data, edge_data, dw_graph_algorithms, directed_weighted_graph.

## Implementation classes:

 1. `NodeData` - A class that implements node_data and represents a single vertex.
 2. `EdgeData` - A class that implements edge_data and represents a single edge.
 3. `DWGraph_DS` - A class that implements weighted_graph and represents a weighted directional graph that can contain multiple nodes and edges. we used HashMap data structure because the methods of this data structure have really good complexity that helped us be efficient, for example add, delete, and other functions all on average of O(1).
 
 4. `DWGraph_algo` - A class that impements dw_graph_algorithms and executes and computes algorithms on graphs.
    

## Remarks & notes:

 - we implemented each of the classes according to the interface we got for it , and several additional hepler functions.
 - When we needed to implement directed_weighted_graph we chose to do so using three HashMap data structures, when the first HashMap is for the nodes, and the second HashMap is for the directions(in and out) and the third HashMap for the for couples of neighbors and edge. we did it because its give us access to both outbound and inbound vertex. 
 - in the classes `NodeData, EdgeData, DWGraph_DS`we added an **Equality function** used for comparing the actual contents of two objects of the same type (rather than their addresses in the memory).
 - `NodeData` has two constructors, one which does not receive any arguments and creates a default node with increasing ID (to ensure there aren't two nodes with the same key), and another constructor which gets all the required parameters as arguments.
 - Our project includes test files to all of the mentioned classes and functionality.
## Function explanations:

**getEdge**- Returns the  out edge if exists .

**connect**- creates a edge between src to dest, if the edge we want to create exists then just update the weight of the edge, Update both vertices (src and dest) on the change made.

**removeNode**- Deleting a node in a graph, when we delete a node in a graph we have to take care of deleting the edges that are connected to the deleted node, i.e. I have to delete myself from my list of neighbors, The function had to have a defined complexity so we chose the data structure that we did.

**removeEdge**- Deleting a edge in a graph, when we delete a edge we have to make sure that the deletion of the edge is to the two nodes to which it is attached. I.e. two-way erasure.

**init**- A function that takes care of initializing the graph.

**isConnected**-Checks whether the graph is a strongy connected graph or not. We implemented the function using a DFS algorithm (the algorithm learned in the lecture). We know that if we managed to go through the whole graph it means that it was possible to reach from any other vertex to any other vertex and therefore the graph is  strongy connected. For the function, we did two external functions of DFS, one of the algorithm in its normal form and one that makes the algorithm in the opposite direction. The purpose of both functions is to ensure trongy connected (which can be reached from any vertex to any vertex).
We did the implementation of DFS using a stack rather than a recursive form so we wont reach maximum recursion depth on the runtime stack.

**shortestPathDist**- we implemented the function by the **Dijkstra** algorithm that I learned in the lecture. The algorithm goes through all the nodes in the graph, and solves the problem of finding the "cheapest" route from a source node to a target node in a weighted graph.
The algorithm works like this: at first all the nodes are marked as not visited, and their distance is defined as infinity, except for the first node whose distance will be set to 0. Then we go over all the out neighbors of that node and update their weight, and insert them to a queue.
Then, one by one, I take out a node from the queue which has the minimum weight, and check for the node's neighbors. **we always update the weight to be the cheapest weight.**
The algorithm is over when we finished going through all the nodes in the queue.

**shortestPath**- Returns a list of vertices with the shortest path, the implemention of the function is done using a Dijkstra algorithm.

**save**- saves to the file the graph we am working on according to the format we received.

**load**- Restores the graph from the file.
