package edu.gmu.classifier.neuralnet.test;

import static edu.gmu.classifier.neuralnet.util.NeuralNetUtils.calculateError;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.gmu.classifier.database.ResultsUploader;
import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;
import edu.gmu.classifier.neuralnet.factory.NetFactory;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;
import edu.gmu.classifier.neuralnet.train.Backpropagation;

/**
 * Main class which uses the data loading and neural net training classes defined elsewhere
 * in order to train neural networks using a variety of parameters to classify handwriting
 * images and report results.
 * 
 * @author ulman
 */
public class Midterm
{
	public static void main( String[] args ) throws IOException
	{
		// list the test data files
		File dataDirectory = new File( "/home/ulman/CSI873/midterm/data" );
		String[] testDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "test" );
			}
		});
		
		// sort the testDataFiles
		Arrays.sort( testDataFiles );
		
		// list the training data files
		String[] trainingDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "train" );
			}
		});
		
		// sort the trainingDataFiles
		Arrays.sort( trainingDataFiles );
		
		// load all test data examples
		List<TrainingExample> testDataList = new ArrayList<TrainingExample>( );
		for ( String fileName : testDataFiles )
		{
			testDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		// load all training data examples
		List<TrainingExample> trainingDataListAll = new ArrayList<TrainingExample>( );
		final Map<Integer,List<TrainingExample>> trainingDataMap = new HashMap<Integer,List<TrainingExample>>( );
		for ( String fileName : trainingDataFiles )
		{
			List<TrainingExample> list = DataLoader.loadFile( new File( dataDirectory, fileName ) );
			int digit = list.get( 0 ).getDigit( );
			trainingDataMap.put( digit, list );
			trainingDataListAll.addAll( list );
		}
		
		// create training and validation sets
		List<TrainingExample> trainingDataList = new ArrayList<TrainingExample>( );
		List<TrainingExample> validationDataList = new ArrayList<TrainingExample>( );
		for ( Entry<Integer,List<TrainingExample>> entry : trainingDataMap.entrySet( ) )
		{
			List<TrainingExample> list = entry.getValue( );
			validationDataList.addAll( list.subList( 0, 5 ) );
			trainingDataList.addAll( list.subList( 5, list.size( ) ) );
		}
		
		// create a network
		Net net = NetFactory.newNet( 64, 10, 10 );
		
		// randomize the network weights
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		
		// run the backpropagation algorithm to train the network using the above training examples
		Backpropagation b = new Backpropagation( );
		b.train( net, trainingDataList, validationDataList, 0.15, 0.3 );
		
		// calculate error and 95% confidence interval for test, training, and validation data sets
		double trainError = calculateError( net, trainingDataList );
		double validationError = calculateError( net, validationDataList );
		double testError = calculateError( net, testDataList );
		
		double trainErrorInterval = 1.96 * Math.sqrt( trainError * ( 1 - trainError ) / trainingDataList.size( ) );
		double validationErrorInterval = 1.96 * Math.sqrt( validationError * ( 1 - validationError ) / validationDataList.size( ) );
		double testErrorInterval = 1.96 * Math.sqrt( testError * ( 1 - testError ) / testDataList.size( ) );
		
		System.out.printf( "Train Error: %.3f Train Interval: (%.3f, %.3f)%n", trainError, trainError - trainErrorInterval, trainError + trainErrorInterval );
		System.out.printf( "Validation Error: %.3f Validation Interval: (%.3f, %.3f)%n", validationError, validationError - validationErrorInterval, validationError + validationErrorInterval );
		System.out.printf( "Test Error: %.3f Test Interval: (%.3f, %.3f)%n", testError, testError - testErrorInterval, testError + testErrorInterval );
		
		// upload results for visualization
		ResultsUploader.uploadTrainingResults( net, trainingDataListAll, "64-10-10_trainrate=0.15_momentum=0.3_stop=200_train" );
		ResultsUploader.uploadTestingResults( net, testDataList, "64-10-10_trainrate=0.15_momentum=0.3_stop=200_test" );
	}
}
