package edu.gmu.classifier.svm.ampl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;

public class DataFileGenerator
{
	public interface OutputGenerator
	{
		public double getOutput( TrainingExample data );
	}
	
	public static class OneVersusAll implements OutputGenerator
	{
		protected int digit;
		
		public OneVersusAll( int digit )
		{
			this.digit = digit;
		}
		
		@Override
		public double getOutput( TrainingExample data )
		{
			return data.getDigit( ) == digit ? 1 : -1;
		}
	};
	
	public static class TwoClass implements OutputGenerator
	{
		protected int digit1;
		protected int digit2;
		
		public TwoClass( int digit1, int digit2 )
		{
			this.digit1 = digit1;
			this.digit2 = digit2;
		}
		
		@Override
		public double getOutput( TrainingExample data )
		{
			int digit = data.getDigit( );
			
			if ( digit == digit1 ) return 1;
			else if ( digit == digit2 ) return -1;
			else return 0;
		}
	};
	
	public static void generateDataFile( String inFileName, String outFileName, int digit ) throws IOException
	{
		List<TrainingExample> dataList = DataLoader.loadDirectory( inFileName );
		outputDataFile( new FileOutputStream( outFileName ), dataList, new OneVersusAll( digit ) );
	}
	
	public static void outputDataFile( OutputStream stream, List<TrainingExample> dataList, OutputGenerator gen ) throws IOException
	{
		BufferedWriter out = new BufferedWriter( new OutputStreamWriter( stream ) );
		
		out.write( "data;" );
		out.newLine( );
		
		int l = dataList.size( );
		out.write( String.format( "param l := %d;%n", l ) );
		
		int n = dataList.get( 0 ).getInputs( ).length;
		out.write( String.format( "param n := %d;%n", n ) );
		
		out.write( String.format( "param C := %f;%n", 100 ) );
		
		out.write( String.format( "param alpha := %f;%n", 0.0156 ) );
		
		out.write( String.format( "param beta := %f;%n", 0 ) );
		
		out.write( String.format( "param delta := %f;%n", 3 ) );
		
		out.write( String.format( "param y :=%n" ) );
		for ( int i = 0 ; i < l ; i++ )
		{
			TrainingExample data = dataList.get( i );
			out.write( String.format( " %d %f%n", i, gen.getOutput( data ) ) );
		}
		out.write( ";" );
		out.newLine( );
		
		out.write( String.format( "param x :=%n" ) );
		for ( int i = 0 ; i < n ; i++ )
		{
			out.write( String.format( " %d", i ) ); 
		}
		for ( int i = 0 ; i < l ; i++ )
		{
			TrainingExample data = dataList.get( i );
			double[] input = data.getInputs( );
			
			out.write( String.format( " %d", i ) );
			
			for ( int j = 0 ; j < n ; j++ )
			{
				out.write( String.format( " %f", input[j] ) ); 
			}
			
			out.newLine( );
		}
		out.write( ";" );
		out.newLine( );
		
		out.close( );
	}
}
