package edu.gmu.classifier.naivebayes;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;
import edu.gmu.classifier.neuralnet.util.NeuralNetUtils;

public class Homework7
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
		} );

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
		} );

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
		final Map<Integer, List<TrainingExample>> trainingDataMap = new HashMap<Integer, List<TrainingExample>>( );
		for ( String fileName : trainingDataFiles )
		{
			List<TrainingExample> list = DataLoader.loadFile( new File( dataDirectory, fileName ) );
			int digit = list.get( 0 ).getDigit( );
			trainingDataMap.put( digit, list );
			trainingDataListAll.addAll( list );
		}

		// create a data structure to store the training frequencies
		Map<P, Double> p0map = new HashMap<P, Double>( );
		
		// loop over digits and input indices, calculating the conditional probabilities:
		// p( index i = 0 | digit = d )
		for ( int d = 0; d < 10; d++ )
		{
			List<TrainingExample> examplesForDigit = trainingDataMap.get( d );

			for ( int i = 0; i < DataLoader.INPUT_SIZE; i++ )
			{
				P key = new P( i, d );
				Double p = calculateTrainingProbability( examplesForDigit, i );
				p0map.put( key, p );
			}
		}
		
		double trainErrorRate = calculateErrorRate( p0map, trainingDataListAll );
		double testErrorRate = calculateErrorRate( p0map, testDataList );
		
		System.out.printf( "Training Error Rate: %.3d Testing ErrorRate: %.3d%n", trainErrorRate, testErrorRate );
	}

	public static double calculateTrainingProbability( List<TrainingExample> examplesForDigit, int inputIndex )
	{
		double count = 0;

		for ( TrainingExample example : examplesForDigit )
		{
			if ( example.getInputs( )[inputIndex] == 0 ) count++;
		}

		return count / ( double ) examplesForDigit.size( );
	}
	
	public static double calculateErrorRate( Map<P, Double> p0map, List<TrainingExample> dataList )
	{
		double correctCount = 0;
		
		for ( TrainingExample data : dataList )
		{
			double[] likelihoods = calculateOutputLikelihoods( p0map, data );
			int predictedDigit = NeuralNetUtils.getLargestIndex( likelihoods );
		
			if ( predictedDigit == data.getDigit( ) )
			{
				correctCount++;
			}
		}
		
		return 1.0 - ( double ) correctCount / (double) dataList.size( );
	}
	
	public static double[] calculateOutputLikelihoods( Map<P, Double> p0map, TrainingExample data )
	{
		double[] likelihoods = new double[10];
		
		// apply the naive bayes classifier using the precalucated conditional probabilities
		// the result is one likelihood value for each digit
		for ( int d = 0; d < 10; d++ )
		{
			// in our case, all the digits have the same number of values, so p(v) is always 0.1
			double likelihood = 0.1;
			
			for ( int i = 0 ; i < DataLoader.INPUT_SIZE; i++ )
			{
				likelihood *= p0map.get( new P( i, d ) );
			}
			
			likelihoods[d] = likelihood;
		}
		
		return likelihoods;
	}
}
