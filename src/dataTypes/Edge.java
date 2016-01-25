package dataTypes;

import linkcuttrees.Vertex;

public class Edge {
	
	private Vertex vertex1;
	private Vertex vertex2;
	private int weight;
	
	public Edge(Vertex vertex1, Vertex vertex2, int weight) {
		super();
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.weight = weight;
	}
	
	public Vertex getVertex1() {
		return vertex1;
	}

	public Vertex getVertex2() {
		return vertex2;
	}

	public int getWeight() {
		return weight;
	}
	
}
