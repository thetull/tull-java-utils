package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.tullco.tullutils.exceptions.GraphException;
import net.tullco.tullutils.graphutils.Edge;
import net.tullco.tullutils.graphutils.Graph;
import net.tullco.tullutils.graphutils.UndirectedGraph;
import net.tullco.tullutils.graphutils.Vertex;

public class GraphTest {

	Graph graph;
	
	@Before
	public void setUp() throws Exception {
		this.graph=new UndirectedGraph();
	}
	
	@Test
	public void testAddEdge() {
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Edge e = new Edge(v1,v2,1);
		this.graph.addEdge(e);
		assertTrue(this.graph.hasVertex("v1"));
		assertTrue(this.graph.hasVertex(v1));
		assertTrue(this.graph.hasVertex("v2"));
		assertTrue(this.graph.hasVertex(v2));
		Set<Edge> edges = this.graph.getEdges();
		assertTrue(edges.contains(new Edge(v1,v2,1)));
		assertTrue(edges.contains(new Edge(v2,v1,1)));
		assertEquals(2,edges.size());
		
	}

	@Test
	public void testAddVertex() {
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		assertFalse(this.graph.hasVertex(v1));
		assertFalse(this.graph.hasVertex(v2));
		this.graph.addVertex(v1);
		assertTrue(this.graph.hasVertex(v1));
		assertFalse(this.graph.hasVertex(v2));
		this.graph.addVertex(v2);
		assertTrue(this.graph.hasVertex(v1));
		assertTrue(this.graph.hasVertex(v2));
		Set<Edge> edges = this.graph.getEdges();
		assertEquals(0,edges.size());
	}

	@Test
	public void testCreateEdgeBetween() {
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		this.graph.createEdgeBetween(v1, v2, 1);
		assertTrue(this.graph.hasVertex("v1"));
		assertTrue(this.graph.hasVertex(v1));
		assertTrue(this.graph.hasVertex("v2"));
		assertTrue(this.graph.hasVertex(v2));
		Set<Edge> edges = this.graph.getEdges();
		assertTrue(edges.contains(new Edge(v1,v2,1)));
		assertTrue(edges.contains(new Edge(v2,v1,1)));
		assertEquals(2,edges.size());
	}

	@Test
	public void testDistanceBetween() throws GraphException {
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Vertex v3 = new Vertex("v3");
		Vertex v4 = new Vertex("v4");
		this.graph.createEdgeBetween(v1, v2, 5);
		this.graph.createEdgeBetween(v1, v3, 2);
		try{
			this.graph.distanceBetween(v1, v4);
			fail("Didn't throw a graph exeption on a missing second vertex.");
		}catch(GraphException e){}
		try{
			this.graph.distanceBetween(v4, v1);
			fail("Didn't throw a graph exeption on a missing first vertex.");
		}catch(GraphException e){}
		this.graph.addVertex(v4);
		try{
			this.graph.distanceBetween(v1, v4);
			fail("This didn't throw a graph exception when there wasn't a path between the vertices.");
		}catch(GraphException e){}
		this.graph.createEdgeBetween(v2,v4, 2);
		this.graph.createEdgeBetween(v3,v4, 10);
		assertEquals(7,this.graph.distanceBetween(v1, v4));
		this.graph.createEdgeBetween(v2, v3, 1);
		assertEquals(5,this.graph.distanceBetween(v1, v4));
	}

}
