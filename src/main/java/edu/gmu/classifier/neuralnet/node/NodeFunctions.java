package edu.gmu.classifier.neuralnet.node;

/**
 * A collection of commonly used NodeFunction implementations.
 * 
 * @author ulman
 *
 */
public class NodeFunctions
{
	/**
	 * @param weight
	 * @return a NodeFunction which sets the weight of all a node's input links to the specified weight
	 */
	public static NodeFunction setWeights( final double weight )
	{
		return new NodeFunction( )
		{
			public void run( Node node )
			{
				for ( Link link : node.getInputLinks( ) )
				{
					link.setWeight( weight );
				}
			}
		};
	};
	
	/**
	 * @param minWeight
	 * @param maxWeight
	 * @return a NodeFunction which sets the weight of all a node's input links to
	 *         a random weight between minWeight and maxWeight
	 */
	public static NodeFunction setRandomWeights( final double minWeight, final double maxWeight )
	{
		return new NodeFunction( )
		{
			public void run( Node node )
			{
				for ( Link link : node.getInputLinks( ) )
				{
					link.setWeight( Math.random( ) * ( maxWeight - minWeight ) + minWeight );
				}
			}
		};
	};
	
	/**
	 * @param learningRate
	 * @param momentum
	 * @return a NodeFunction which updates the weight of all a node's input links to
	 *         based on the previously calculated error and output values
	 */
	public static NodeFunction updateWeights( final double learningRate, final double momentum )
	{
		return new NodeFunction( )
		{
			public void run( Node node )
			{
				node.updateInputWeights( learningRate, momentum );
			}
		};
	};
}
