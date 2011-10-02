package edu.gmu.classifier.neuralnet.node;

public class Link
{
	protected Node inputNode;
	protected Node outputNode;
	protected double weight;
	
	public Link( Node inputNode, Node outputNode )
	{
		this.inputNode = inputNode;
		this.outputNode = outputNode;
	}
	
	public void setWeight( double weight )
	{
		this.weight = weight;
	}
	
	public double getWeight( )
	{
		return this.weight;
	}
	
	public Node getInputNode( )
	{
		return this.inputNode;
	}
	
	public Node getOutputNode( )
	{
		return this.outputNode;
	}
}
