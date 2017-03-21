package net.tullco.tullutils.graphutils;

import org.apache.commons.lang3.tuple.Pair;

public class Edge {
	private final int distance;
	private final Vertex origin;
	private final Vertex destination;
	
	public Edge(Vertex origin, Vertex destination, int distance){
		this.origin=origin;
		this.destination=destination;
		this.distance=distance;
	}
	public Pair<Vertex,Vertex> getVertices(){
		return Pair.of(this.origin,this.destination);
	}
	public int getDistance(){
		return this.distance;
	}
}
