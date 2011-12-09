package edu.gmu.classifier.knearest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		
		TreeMultimap<Integer,TrainingExample> map = getDistancesFrom( dataListTest.get( 0 ), dataListTrain );
		Collection<TrainingExample> k = pickLowestK( map, 5 );
		System.out.println( k );
	}
	
	// given a sorted map containing the distances from a test example to all training examples, choose the k lowest
	public static Collection<TrainingExample> pickLowestK( TreeMultimap<Integer,TrainingExample> map, int k )
	{
		Collection<TrainingExample> list = new ArrayList<TrainingExample>( k );
		int count = 0;
		
		for( TrainingExample example : map.values( ) )
		{
			if ( count == k ) break;
			
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
