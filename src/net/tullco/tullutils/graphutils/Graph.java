package net.tullco.tullutils.graphutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.tullco.tullutils.exceptions.GraphException;

public abstract class Graph {
	
	private HashMap<String,Vertex> vertices;
	private HashMap<Vertex,Set<Edge>> connections;
	private Set<Edge> edges;
	
	/**
	 * Create an empty graph with no vertices or edges.
	 */
	public Graph(){
		this.vertices = new HashMap<String,Vertex>();
		this.connections= new HashMap<Vertex,Set<Edge>>();
		this.edges = new HashSet<Edge>();
	}

	/**
	 * Add the specified edge to the graph. Will also add the vertices if they aren't already part of the graph.
	 * @param e The edge to add to the graph.
	 */
	public abstract void addEdge(Edge e);
	
	/**
	 * Add the specified vertex to the graph. Will not create any edges. Does nothing if the vertex is already a part of the graph.
	 * @param v The vertex to add to the graph.
	 */
	public void addVertex(Vertex v){
		if(v==null)
			return;
		this.vertices.putIfAbsent(v.getIdentifier(), v);
		this.connections.putIfAbsent(v, new HashSet<Edge>());
	}
	/**
	 * Creates a new edge between the two specified verticies with the given distance between them.
	 * The vertices will be added to the graph if they are not already contained. Order matters if you're in a directed environment.
	 * @param origin The origin of the new edge.
	 * @param destination The endpoint of the new edge.
	 * @param distance The distance between the two points along this edge.
	 */
	public void createEdgeBetween(Vertex origin, Vertex destination, int distance){
		Edge e = new Edge(origin,destination,distance);
		this.addEdge(e);
	}
	/**
	 * Returns a hashmap of the verticies in the graph keyed to their identifiers.
	 * The resulting hashmap will be completely independent from the graph.
	 * @return A hashmap of the verticies in the graph keyed to their identifiers.
	 */
	public HashMap<String,Vertex> getVertices(){
		HashMap<String,Vertex> copy = new HashMap<String,Vertex>(this.vertices);
		return copy;
	}
	protected HashMap<Vertex,Set<Edge>> getConnections(){
		return this.connections;
	}
	public Set<Edge> getEdges(){
		return this.edges;
	}
	public Vertex getVertex(String identifier){
		return this.vertices.get(identifier);
	}
	public boolean hasVertex(String identifier){
		return this.vertices.containsKey(identifier);
	}
	public boolean hasVertex(Vertex v){
		return hasVertex(v.getIdentifier());
	}
	public Set<Edge> getVertexEdges(String identifier){
		Vertex v = this.vertices.get(identifier);
		return this.getVertexEdges(v);
	}
	public Set<Edge> getVertexEdges(Vertex v){
		HashSet<Edge> copy = new HashSet<Edge>(this.connections.get(v));
		return copy;
	}
	
	/**
	 * Uses Djikstra's method to calculate a path between the two vertices.
	 * @param origin The origin vertex.
	 * @param destination The destination vertex.
	 * @return The distance between the two vertices.
	 * @throws GraphException If the graph does not contain both the specified vertices or there is no path between the two vertices.
	 */
	public int distanceBetween(Vertex origin, Vertex destination) throws GraphException{
		if(!this.hasVertex(origin)){
			throw new GraphException("The origin vertex "+origin.getIdentifier()+" is not in the graph.");
		}
		if(!this.hasVertex(destination)){
			throw new GraphException("The destination vertex "+destination.getIdentifier()+" is not in the graph.");
		}
		HashMap<Vertex,Integer> costs = new HashMap<Vertex,Integer>();
		HashSet<Vertex> completedVertices = new HashSet<Vertex>();
		costs.put(origin, 0);
		Vertex current=origin;
		int currentDistance = 0;
		while(!current.equals(destination)){
			//System.out.println("Pre Distance: "+currentDistance);
			//System.out.println(current.getIdentifier());
			for(Edge e: this.getVertexEdges(current)){
				Vertex newVertex = e.getVertices().getRight();
				if(completedVertices.contains(newVertex))
					continue;
				if(!costs.containsKey(newVertex)){
					costs.put(newVertex,currentDistance+e.getDistance());
				}else{
					int currentCost = costs.get(newVertex);
					if(currentDistance + e.getDistance() < currentCost){
						costs.put(newVertex, currentDistance+e.getDistance());
					}
				}
			}
			completedVertices.add(current);
			Pair<Vertex,Integer> best = null;
			for(Vertex candidate : costs.keySet()){
				if(completedVertices.contains(candidate)){
					continue;
				}
				int vertexCost = costs.get(candidate);
				if(best==null){
					best = Pair.of(candidate, vertexCost);
					continue;
				}if(vertexCost < best.getValue()){
					best = Pair.of(candidate, vertexCost);
					continue;
				}
			}
			if(best==null)
				throw new GraphException("There is no graph between vertex "+origin.getIdentifier()+" and vertex "+destination.getIdentifier()+".");
			current=best.getKey();
			currentDistance = best.getValue();
			//System.out.println("Current Distance: "+currentDistance);
		}
		return currentDistance;
	}
}
