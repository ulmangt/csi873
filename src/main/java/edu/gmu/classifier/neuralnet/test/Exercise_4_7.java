package edu.gmu.classifier.neuralnet.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gmu.classifier.neuralnet.factory.NetFactory;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;
import edu.gmu.classifier.neuralnet.train.Backpropagation;
import edu.gmu.classifier.neuralnet.train.Backpropagation.TrainingExample;

public class Exercise_4_7
{
	public static void main( String[] args )
	{
		Net net = NetFactory.newNet( 2, 1, 1 );
	
		net.apply( NodeFunctions.setWeights( 0.1 ) );
	
		System.out.println( Arrays.toString( net.calculateOutput( 1.0, 0.0 ) ) );
		
		Backpropagation b = new Backpropagation( );
		
		List<TrainingExample> training = new ArrayList<TrainingExample>( );
		training.add( new TrainingExample( new double[] { 1, 0 }, new double[] { 1 } ) );
		training.add( new TrainingExample( new double[] { 0, 1 }, new double[] { 0 } ) );
		
		b.train( net, training, 0.3, 0.9 );
		
		System.out.println( Arrays.toString( net.calculateOutput( 1.0, 0.0 ) ) );
		System.out.println( Arrays.toString( net.calculateOutput( 0.0, 1.0 ) ) );
	}
}
