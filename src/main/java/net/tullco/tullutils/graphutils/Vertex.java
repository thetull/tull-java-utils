package net.tullco.tullutils.graphutils;

/**
 * This class represents a Vertex of on a Graph. It is primarily for with the graph class.
 * @author Tull Gearreald
 */
public class Vertex {
	private final String identifier;
	
	/**
	 * Construct an immutable vertex with the given string as an identifier. Please don't use Null as the identifier. I have no idea what that will do.
	 * @param identifier The Identifier for the vertex. Vertices with identical identifiers are considered equal. Null is not advised, but will not throw an error.
	 */
	public Vertex(String identifier){
		this.identifier=identifier;
	}
	/**
	 * Gets the vertex's identifier.
	 * @return The identifier string for the vertex.
	 */
	public String getIdentifier(){
		return this.identifier;
	}
	/**
	 * Yes if the object is a Vertex and has the same identifer and is not null.
	 * @param o The object to check for equality.
	 * @return True if the objects are equal by the above definition. False otherwise.
	 */
	@Override
	public boolean equals(Object o){
		if(o==null)
			return false;
		if(o==this)
			return true;
		if(!(o instanceof Vertex))
			return false;
		Vertex v = (Vertex) o;
		return this.identifier==v.identifier;
	}
	@Override
	public int hashCode(){
		return this.identifier.hashCode();
	}
	/**
	 * Returns the identifier.
	 * @return The vertex's identifier.
	 */
	@Override
	public String toString(){
		return this.identifier;
	}
}
