package net.tullco.tullutils.graphutils;

import org.apache.commons.lang3.tuple.Pair;

/**
 * An Edge for use with the graph class. Edges are immutable.
 * @author Tull Gearreald
 *
 */
public class Edge {
	private final int distance;
	private final Vertex origin;
	private final Vertex destination;
	/**
	 * Creates an edge with the specified distance that connects the origin and destination.
	 * @param origin The starting point of the edge.
	 * @param destination The destination of the edge.
	 * @param distance The distance or cost of traversing the edge.
	 */
	public Edge(Vertex origin, Vertex destination, int distance){
		this.origin=origin;
		this.destination=destination;
		this.distance=distance;
	}
	/**
	 * Returns a pair containing the two vertices connected by the edge, with the origin being on the left, and the destination being on the right.
	 * @return A pair containing the two vertices.
	 */
	public Pair<Vertex,Vertex> getVertices(){
		return Pair.of(this.origin,this.destination);
	}
	/**
	 * Returns the distance factor of the edge.
	 * @return The edge distance
	 */
	public int getDistance(){
		return this.distance;
	}
}
