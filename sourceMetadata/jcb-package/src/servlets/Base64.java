import java.util.*;

public class Base64 {

   /**
	 * Encodes a byte array (binary data) into a base64 string
	 */ 
	public static String encode(byte[] i) {
		String s = new String();
		byte[] dChunk = new byte[3];
		byte[] eChunk = new byte[4];
		boolean eof = false, 
			eof1 = false;
		int numpad = 3 - (i.length % 3);
		int ch;
		for ( int n = 0; n < i.length + numpad; n+=3 ) { 
			if ( n >= i.length ) {
				ch = 0;
				eof = true; 
				break;
			} else 
				ch = i[n];
			dChunk[0]= (byte)ch;
			eChunk[0] = btable[( ( dChunk[0] & 0xFF ) >> 2 )];
			if ( n + 1 >= i.length ) {
				ch = 0;
				eof = eof1 = true;
			} else 
				ch = i[n+1];
			dChunk[1] = (byte)ch;
			eChunk[1] = btable[( ( ( ( dChunk[0] & 0xff ) & 3 ) << 4 ) | ( ( dChunk[1] & 0xFF ) >> 4 ) )];
			if ( n + 2 >= i.length ) {
				ch = 0; 
				eof =true;
			} else 
				ch = i[n+2];
			dChunk[2] = (byte)ch;
			eChunk[2] = btable[( ( ( ( dChunk[1] & 0xFF ) & 0xF ) << 2 ) | ( ( dChunk[2] & 0xFF ) >> 6 ) )];
			eChunk[3] = btable[( ( dChunk[2] & 0xFF ) & 0x3F )];
			if ( eof1 ) 
				eChunk[2] =(byte)'=';
			if ( eof ) 
				eChunk[3] = (byte)'=';
			s += (char)eChunk[0] + "" + (char)eChunk[1] + "" + (char)eChunk[2] + "" + (char)eChunk[3];
		}
		return s;
	}
	
	
	/**
	 * Decodes a base64 String into a byte array (binary data)
	 */
	public static byte[] decode( String i ) {
		int ilen = i.length();
		Vector v = new Vector();
		byte[] dChunk = new byte[3];
		byte[] eChunk = new byte[4];
		boolean eof = false;
		char ch;
		
		for (int n = 0, idx = 0; n < ilen; n+=4, idx+=3) {
			if ( n >= ilen ) // decode 0
				break;
			else 
				ch = i.charAt( n );
			eChunk[0] = lookup( ch );
			if ( n + 1 >= ilen ) { // decode 1
				ch = '=';
				eof = true;
			} else 
				ch = i.charAt( n + 1 );
			eChunk[1] = lookup( ch );
			dChunk[0] = (byte)( ( eChunk[0] << 2 ) | ( eChunk[1] >> 4 ) );
			v.addElement( new Byte(dChunk[0]) );
			if ( n + 2 >= ilen ) { // decode 2
				ch = '=';
				eof = true;
			} else 
				ch = i.charAt( n + 2 ); 
			if ( ch != '=' ) {
				eChunk[2] = lookup( ch );
				dChunk[1] = (byte)( ( eChunk[1] << 4 ) | (eChunk[2] >> 2 ) );
				v.addElement( new Byte(dChunk[1]) );
			}
			if ( n + 3 >= ilen ) { // decode 3
				ch = '=';
				eof = true;
			} else 
				ch = i.charAt( n + 3 );
			if ( ch != '=' ) {
				eChunk[3] = lookup( ch );
				dChunk[2] = (byte)( ( eChunk[2] << 6 ) | eChunk[3] );
				v.addElement( new Byte(dChunk[2]) );
			}
		}
		
		byte[] o = new byte[ v.size() ];
		for ( int j = 0; j < v.size(); j++ )
			o[j] = ((Byte)v.elementAt(j)).byteValue();
		
		return o;
	}
	

	private static byte[] btable=new byte[64];
	
	private static char[] ctable= {'A','B','C','D','E','F','G','H','I','J','K','L','M',
											 'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
											 'a','b','c','d','e','f','g','h','i','j','k','l','m',
											 'n','o','p','q','r','s','t','u','v','w','x','y','z',
											 '0','1','2','3','4','5','6','7','8','9','+','/'};
	
	static {
		for (int i = 0; i < 64; i++ ) 
			btable[i] = (byte)ctable[i]; 
	}
	
	private static byte lookup(char b) {
		for ( byte i = 0 ; i < btable.length ; i++ ) 
			if ( b == btable[i] )
				return i;
		return -1;
	}
	
}
