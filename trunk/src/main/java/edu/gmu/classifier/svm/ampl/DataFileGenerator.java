package edu.gmu.classifier.svm.ampl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
	
	public static void generateAllDataFiles( String inDirectoryString, String outDirectoryString ) throws IOException
	{
		generateDataFile( inDirectoryString, outDirectoryString,  "classify_2-5", 2, 5 );
		
		for ( int i = 0 ; i < 10 ; i++ )
		{
			generateDataFile( inDirectoryString, outDirectoryString, String.format( "classify_%d", i ), i );
		}
	}
	
	public static void generateDataFile( String inFileName, String outDirectoryName, String outFilePrefix, int digit ) throws IOException
	{
		List<TrainingExample> dataList = DataLoader.loadDirectory( inFileName );
		
		File outDirectory = new File( outDirectoryName );
		File outFile = new File( outDirectory, outFilePrefix + ".dat" );
		outputDataFile( new FileOutputStream( outFile ), dataList, new OneVersusAll( digit ) );
		
		File outCommandFile = new File( outDirectory, outFilePrefix + ".cmd" );
		outputCommandFile( new FileOutputStream( outCommandFile ), outFile.getName( ) );
	}
	
	public static void generateDataFile( String inFileName, String outDirectoryName, String outFilePrefix, int digit1, int digit2 ) throws IOException
	{
		List<TrainingExample> dataList = DataLoader.loadDirectory( inFileName );
		
		List<TrainingExample> filteredList = new ArrayList<TrainingExample>( dataList.size( ) );
		for ( TrainingExample data : dataList )
		{
			if ( data.getDigit( ) == digit1 || data.getDigit( ) == digit2 )
			{
				filteredList.add( data );
			}
		}
		
		File outDirectory = new File( outDirectoryName );
		File outFile = new File( outDirectory, outFilePrefix + ".dat" );
		outputDataFile( new FileOutputStream( outFile ), filteredList, new TwoClass( digit1, digit2 ) );
		
		File outCommandFile = new File( outDirectory, outFilePrefix + ".cmd" );
		outputCommandFile( new FileOutputStream( outCommandFile ), outFile.getName( ) );
	}
	
	public static void outputCommandFile( OutputStream stream, String dataFileName ) throws IOException
	{
		BufferedWriter out = new BufferedWriter( new OutputStreamWriter( stream ) );
		
		out.write( "reset;" );
		out.newLine( );

		out.write( "model classify.mod;" );
		out.newLine( );

		out.write( String.format( "data %s;%n", dataFileName ) );

		out.write( "solve;" );
		out.newLine( );

		out.write( "display a;" );
		out.newLine( );
		
		out.close( );
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
		
		out.write( String.format( "param C := %f;%n", 100.0 ) );
		
		out.write( String.format( "param alpha := %f;%n", 0.0156 ) );
		
		out.write( String.format( "param beta := %f;%n", 0.0 ) );
		
		out.write( String.format( "param delta := %f;%n", 3.0 ) );
		
		out.write( String.format( "param y :=%n" ) );
		for ( int i = 0 ; i < l ; i++ )
		{
			TrainingExample data = dataList.get( i );
			out.write( String.format( " %d %.1f%n", i+1, gen.getOutput( data ) ) );
		}
		out.write( ";" );
		out.newLine( );
		
		out.write( String.format( "param x:" ) );
		for ( int i = 0 ; i < n ; i++ )
		{
			out.write( String.format( " %d", i+1 ) ); 
		}
		out.write( String.format( " :=%n" ) );
		for ( int i = 0 ; i < l ; i++ )
		{
			TrainingExample data = dataList.get( i );
			double[] input = data.getInputs( );
			
			out.write( String.format( " %d", i+1 ) );
			
			for ( int j = 0 ; j < n ; j++ )
			{
				out.write( String.format( " %.1f", input[j] ) ); 
			}
			
			out.newLine( );
		}
		out.write( ";" );
		out.newLine( );
		
		out.close( );
	}
	
	public static void main( String[] args ) throws IOException
	{
		generateAllDataFiles( "/home/ulman/CSI873/midterm/data", "/home/ulman/CSI873/midterm/repository/final/ampl" );
	}
}
