package net.tullco.tullutils.graphutils;

import java.util.HashMap;
import java.util.Set;

public class UndirectedGraph extends Graph {

	public UndirectedGraph(){
		super();
	}

	@Override
	public void addEdge(Edge e) {
		if(e==null){
			return;
		}
		Vertex v1 = e.getVertices().getLeft();
		Vertex v2 = e.getVertices().getRight();
		
		Edge forward = e;
		Edge backward = new Edge(e.getVertices().getRight(),e.getVertices().getLeft(),e.getDistance());
		
		// Add the vertices to the graph
		this.addVertex(v1);
		this.addVertex(v2);
		
		HashMap<Vertex,Set<Edge>> connections = this.getConnections();
		connections.get(v1).add(forward);
		connections.get(v2).add(backward);
		
		this.getEdges().add(forward);
		this.getEdges().add(backward);
	}

}
