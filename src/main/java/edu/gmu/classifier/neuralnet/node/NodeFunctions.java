package edu.gmu.classifier.neuralnet.node;

public class NodeFunctions
{
	public static NodeFunction setWeights( final double weight )
	{
		return new NodeFunction( )
		{
			public void run( Node node )
			{
				for ( int i = 0 ; i < node.getInputCount( ) ; i++ )
				{
					node.setWeight( i, weight );
				}
			}
		};
	};
	
}
