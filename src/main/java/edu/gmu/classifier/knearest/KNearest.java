package edu.gmu.classifier.knearest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;

public class KNearest
{
	public static void main( String[] args ) throws IOException
	{
		// load training and testing data
		List<TrainingExample> dataListTrain = DataLoader.loadDirectoryTrain( "/home/ulman/CSI873/midterm/data" );
		List<TrainingExample> dataListTest = DataLoader.loadDirectoryTest( "/home/ulman/CSI873/midterm/data" );
		
		runKNNClassifier( dataListTrain, dataListTest );
	}
	
	public static interface WeightCalculator
	{
		public double getWeight( double distance );
	}
	
	public static void runKNNClassifier( List<TrainingExample> dataListTrain, List<TrainingExample> dataListTest )
	{
		Map<TrainingExample,TreeMultimap<Integer,TrainingExample>> outerMap = new HashMap<TrainingExample,TreeMultimap<Integer,TrainingExample>>( );
		
		for ( TrainingExample test : dataListTest )
		{
			TreeMultimap<Integer,TrainingExample> map = getDistancesFrom( test, dataListTrain );
			outerMap.put( test, map );
		}
		
		WeightCalculator uniformWeight = new WeightCalculator( )
		{
			@Override
			public double getWeight( double distance )
			{
				return 1.0;
			}
		};
		
		calculateErrorRate( outerMap, dataListTest, uniformWeight );
		
		WeightCalculator decayWeight = new WeightCalculator( )
		{
			@Override
			public double getWeight( double distance )
			{
				return 1.0 / ( distance * distance + 1.0 );
			}
		};
		
		calculateErrorRate( outerMap, dataListTest, decayWeight );
	}
	
	public static void calculateErrorRate( Map<TrainingExample,TreeMultimap<Integer,TrainingExample>> outerMap,
			                               List<TrainingExample> dataListTest, WeightCalculator weightCalc )
	{
		for ( int k = 1 ; k <= 7 ; k++ )
		{
			int correct = 0;
			for ( TrainingExample test : dataListTest )
			{
				int predicted_digit = pickDigit( pickLowestK( outerMap.get( test ), k ), weightCalc );
				if ( predicted_digit == test.getDigit( ) ) correct++;
			}
			
			double errorRate = 1.0 - ( (double) correct / (double) dataListTest.size( ) );
			double errorInterval = 1.96 * Math.sqrt( errorRate * ( 1 - errorRate ) / dataListTest.size( ) );
			
			System.out.printf( "K: %d Error Rate: %.3f Train Interval: (%.3f, %.3f)%n", k, errorRate, errorRate - errorInterval, errorRate + errorInterval );
		}
	}
	
	public static int pickDigit( Collection<Entry<Integer,TrainingExample>> list, WeightCalculator weightCalc )
	{
		double[] digitCounts = new double[10];
		
		for ( Entry<Integer,TrainingExample> entry : list )
		{
			TrainingExample example = entry.getValue( );
			Integer distance = entry.getKey( );
			
			digitCounts[example.getDigit( )] += weightCalc.getWeight( distance );
		}
		
		return getLargestIndex( digitCounts );
	}
	
	//returns the index of the largest entry in the array
	public static int getLargestIndex( double[] array )
	{
		double max = 0;
		int index = 0;
		
		for ( int i = 0 ; i < array.length ; i++ )
		{
			double data = array[i];
			
			if ( data > max )
			{
				max = data;
				index = i;
			}
		}
		
		return index;
	}
	
	// given a sorted map containing the distances from a test example to all training examples, choose the k lowest
	public static Collection<Entry<Integer,TrainingExample>> pickLowestK( TreeMultimap<Integer,TrainingExample> map, int k )
	{
		Collection<Entry<Integer,TrainingExample>> list = new ArrayList<Entry<Integer,TrainingExample>>( k );
		int count = 0;
		
		for( Entry<Integer,TrainingExample> example : map.entries( ) )
		{
			if ( count++ == k ) break;
			
			list.add( example );
		}
		
		return list;
	}
	
	// calculates the distance between each training example and the test example, returns the values in a sorted map
	public static TreeMultimap<Integer,TrainingExample> getDistancesFrom( TrainingExample example, List<TrainingExample> dataListTrain )
	{
		TreeMultimap<Integer,TrainingExample> map = TreeMultimap.create( Ordering.natural( ), Ordering.arbitrary( ) );
		
		for ( TrainingExample data : dataListTrain )
		{
			map.put( getDistance( example, data ), data );
		}
		
		return map;
	}
	
	// the distance between two training examples is defined as the number of pixels which differ
	public static int getDistance( TrainingExample e1, TrainingExample e2 )
	{
		double[] d1 = e1.getInputs( );
		double[] d2 = e2.getInputs( );
		
		int count = 0;
		for ( int i = 0 ; i < d1.length ; i++ )
		{
			if ( d1[i] != d2[i] )
				count++;
		}
		
		return count;
	}
}
