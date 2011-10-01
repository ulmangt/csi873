package edu.gmu.classifier.neuralnet.test;

import edu.gmu.classifier.neuralnet.factory.NetFactory;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;

public class Exercise_4_7
{
	public static void main( String[] args )
	{
		Net net = NetFactory.newNet( 2, 1, 1 );
	
		net.apply( NodeFunctions.setWeights( 0.1 ) );
	
		System.out.println( "done" );
	}
}
