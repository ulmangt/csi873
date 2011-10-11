package edu.gmu.classifier.neuralnet.node;

/**
 * A weighted link between two nodes. A previous change in weight value is
 * also stored in order to implement weight updates with momentum. 
 * 
 * @author ulman
 */
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
	
	/**
	 * Update the weight of this link by delta_weight plus the previous
	 * delta_weight times the momentum parameter which should generally
	 * be on the interval [0,1).
	 * 
	 * @param delta_weight the amount to add to the link weight
	 * @param momentum the momentum parameter on [0,1)
	 */
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
	
	@Override
	public String toString( )
	{
		return inputNode + ":" + outputNode;
	}
}
