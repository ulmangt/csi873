package edu.gmu.classifier.neuralnet.node;

import java.util.List;

/**
 * A single Node in a Net consisting of any number of weighted inputs and a single output.
 * @author ulman
 */
public interface Node
{
	/**
	 * Sets the output based on the weights and child outputs.
	 */
	public void calculateOutput( );
	
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
	 * Calculate error for interior node.
	 */
	public void calculateError( );
	
	/**
	 * Calculate error for output node using the truth value provided
	 * @param truth
	 */
	public void calculateError( double truth );
	
	public double getError( );
	
	public void setError( double error );
	
	/**
	 * @return the weighted linear combination of the input nodes
	 */
	public double getNet( );
	
	/**
	 * Update the weights of the input links
	 */
	public void updateInputWeights( double learningRate );
	
	public List<Link> getInputLinks( );
	
	public List<Link> getOutputLinks( );
	
	public void addInputLink( Link link );
	
	public void addOutputLink( Link link );
}