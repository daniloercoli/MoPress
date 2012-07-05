package com.danais.net;

import java.io.IOException;

import javax.microedition.io.HttpConnection;

import com.danais.utils.StringUtils;


public class HttpConnUtils {
	
	 public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	  /**
	   * URLEncode method.
	   * 
	   * @param s
	   *          string to urlencode
	   * @return urlencoded string
	   */
	  public static synchronized  String URLEncode(final String s) {
	    if (s == null) {
	      return null;
	    }

	    byte[] b = null;

	    try {
	      b = s.getBytes("utf-8");
	    } catch (final Exception ex) {
	      try {
	        b = s.getBytes("UTF8");
	      } catch (final Exception ex2) {
	        b = s.getBytes();
	      }
	    }

	    final StringBuffer result = new StringBuffer();

	    for (int i = 0; i < b.length; i++) {
	      if ((b[i] >= 'A' && b[i] <= 'Z') || (b[i] >= 'a' && b[i] <= 'z')
	          || (b[i] >= '0' && b[i] <= '9')) {
	        result.append((char) b[i]);
	      } else {
	        result.append('%');
	        result.append(HEX_ARRAY[((b[i] + 256) >> 4) & 0x0F]);
	        result.append(HEX_ARRAY[b[i] & 0x0F]);
	      }
	    }

	    return result.toString();
	  }
	
	public static synchronized  String oldURLEncode(String s)
	   {
	      StringBuffer sbuf = new StringBuffer();
	      int ch;
	      for (int i = 0; i < s.length(); i++) 
	      {
	         ch = s.charAt(i);
	         switch(ch)
	         {
	            case ' ': { sbuf.append("+"); break;} 
	            case '!': { sbuf.append("%21"); break;} 
	            case '*': { sbuf.append("%2A"); break;} 
	            case '\'': { sbuf.append("%27"); break;} 
	            case '(': { sbuf.append("%28"); break;} 
	            case ')': { sbuf.append("%29"); break;} 
	            case ';': { sbuf.append("%3B"); break;} 
	            case ':': { sbuf.append("%3A"); break;} 
	            case '@': { sbuf.append("%40"); break;} 
	            case '&': { sbuf.append("%26"); break;} 
	            case '=': { sbuf.append("%3D"); break;} 
	            case '+': { sbuf.append("%2B"); break;} 
	            case '$': { sbuf.append("%24"); break;} 
	            case ',': { sbuf.append("%2C"); break;} 
	            case '/': { sbuf.append("%2F"); break;} 
	            case '?': { sbuf.append("%3F"); break;} 
	            case '%': { sbuf.append("%25"); break;} 
	            case '#': { sbuf.append("%23"); break;} 
	            case '[': { sbuf.append("%5B"); break;} 
	            case ']': { sbuf.append("%5D"); break;} 
	            default: sbuf.append((char)ch);
	         }         
	      }      
	      return sbuf.toString();
	   }

	
	  // Simple routine that iterates through the
    // response headers and looks for a Set-Cookie
    // header.  It then splits the header value
    // into parts and looks for a cookie value
    // with the given name.
    // Adapt this code appropriately for other
    // kinds of cookies.

    public static synchronized String readCookie( HttpConnection conn, String cookieName )
                          throws IOException { 
        String   key;
        String   value;
        String[] substrs;

        for( int i = 0;( key = conn.getHeaderFieldKey( i ) )!= null; ++i ){

            key = key.toLowerCase();

            if( key.equals( "set-cookie" ) ){
                value = conn.getHeaderField( i );

                while( value != null ){
                    substrs = StringUtils.split2Strings( value, ';' );
                    if( substrs[0].startsWith(
                    		cookieName+"=" ) ){
                        return substrs[0];
                    }
                    value = substrs[1];
                }
            }
        }

        return null;
    }
}
