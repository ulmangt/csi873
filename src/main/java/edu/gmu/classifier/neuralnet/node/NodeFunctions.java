package edu.gmu.classifier.neuralnet.node;

public class NodeFunctions
{
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
	
	public static NodeFunction updateWeights( final double learningRate )
	{
		return new NodeFunction( )
		{
			public void run( Node node )
			{
				node.updateInputWeights( learningRate );
			}
		};
	};
}
