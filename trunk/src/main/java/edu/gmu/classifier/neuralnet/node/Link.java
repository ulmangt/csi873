package edu.gmu.classifier.neuralnet.node;

public class Link
{
	protected Node inputNode;
	protected Node outputNode;
	
	protected double previous_delta_weight;
	protected double weight;
	
	public Link( Node inputNode, Node outputNode )
	{
		this.previous_delta_weight = 0.0;
		this.weight = 0.0;
		this.inputNode = inputNode;
		this.outputNode = outputNode;
	}
	
	public void deltaWeight( double delta_weight, double momentum )
	{
		delta_weight = delta_weight + momentum * this.previous_delta_weight;
		this.weight = this.weight + delta_weight;
		this.previous_delta_weight = delta_weight;
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
