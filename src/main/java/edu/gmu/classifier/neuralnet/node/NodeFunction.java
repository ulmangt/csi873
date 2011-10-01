package edu.gmu.classifier.neuralnet.node;

public interface NodeFunction
{
	public static final NodeFunction ZeroWeights = new NodeFunction( )
	{
		public void run( Node node )
		{
			for ( int i = 0 ; i < node.getInputCount( ) ; i++ )
			{
				node.setWeight( i, 0.0 );
			}
		}
	};
	
	public void run( Node node );
}
