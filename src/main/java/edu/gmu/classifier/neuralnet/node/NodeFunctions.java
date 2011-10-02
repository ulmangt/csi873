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
	
}
