package edu.gmu.classifier.neuralnet.node;

/**
 * A single Node in a Net consisting of any number of weighted inputs and a single output.
 * @author ulman
 */
public interface Node
{
	/**
	 * Sets the output based on the weights and child outputs.
	 */
	public void setOutput( );
	
	/**
	 * Sets the output to a fixed value (used for input nodes).
	 * @param value
	 */
	public void setOutput( double value );
	
	/**
	 * @return the output value of this node
	 *         (after passing throught the squashing function)
	 */
	public double getOutput( );
	
	/**
	 * @return the weighted linear combination of the input nodes
	 */
	public double getNet( );
	
	/**
	 * @return the number of input nodes.
	 */
	public int getInputCount( );
	
	/**
	 * @param i the input index
	 * @return the weight associated with the ith input
	 */
	public double getWeight( int i );
	
	/**
	 * @param i the input index
	 * @param weight the new weight associated with the ith input
	 */
	public void setWeight( int i, double weight );
	
	/**
	 * @param i the input index
	 * @return the ith input node
	 */
	public Node getInput( int i );
}
