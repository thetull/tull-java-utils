package net.tullco.tullutils.graphutils;

public class Vertex {
	private final String identifier;
	public Vertex(String identifier){
		this.identifier=identifier;
	}
	
	public String getIdentifier(){
		return this.identifier;
	}
	
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
	public String toString(){
		return this.identifier;
	}
}
