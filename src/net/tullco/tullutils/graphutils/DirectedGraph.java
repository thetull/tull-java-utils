package net.tullco.tullutils.graphutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DirectedGraph extends Graph {

	@Override
	public void addEdge(Edge e) {
		if(e==null){
			return;
		}
		Vertex v1 = e.getVertices().getLeft();
		Vertex v2 = e.getVertices().getRight();
		
		// Add the vertices to the graph
		this.addVertex(v1);
		this.addVertex(v2);
		
		HashMap<Vertex,Set<Edge>> connections = this.getConnections();
		Set<Edge> tempEdgeSet;
		Set<Edge> newEdges = new HashSet<Edge>();
		newEdges.add(e);
		tempEdgeSet = connections.putIfAbsent(v1, newEdges);
		if(tempEdgeSet != null)
			tempEdgeSet.add(e);
		this.getEdges().add(e);
	}

}
