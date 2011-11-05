package edu.gmu.classifier.naivebayes;

public class P
{
	int input_index;
	int digit;
	
	public P( int input_index, int digit )
	{
		this.input_index = input_index;
		this.digit = digit;
	}

	public int getInput_index( )
	{
		return input_index;
	}

	public int getDigit( )
	{
		return digit;
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + digit;
		result = prime * result + input_index;
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( getClass( ) != obj.getClass( ) ) return false;
		P other = ( P ) obj;
		if ( digit != other.digit ) return false;
		if ( input_index != other.input_index ) return false;
		return true;
	}
}
