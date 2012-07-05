package com.danais.utils;

import java.util.Vector;

public class StringUtils {

	  // Splits string 
	  public static String[] split(final String string, final String splitBy) {
		    final Vector tokens = new Vector();
		    final int tokenLength = splitBy.length();

		    int tokenStart = 0;
		    int splitIndex;
		    while ((splitIndex = string.indexOf(splitBy, tokenStart)) != -1) {
		      tokens.addElement(string.substring(tokenStart, splitIndex));
		      tokenStart = splitIndex + tokenLength;
		    }

		    tokens.addElement(string.substring(tokenStart));

		    final String[] result = new String[tokens.size()];
		    tokens.copyInto(result);
		    return result;
		  }
	
	
	  // Split One string in 2 string 
	 public synchronized static String[] split2Strings( String in, char ch ){
	      String[] result = new String[2];
	      int      pos = in.indexOf( ch );

	      if( pos != -1 ){
	          result[0] = in.substring( 0, pos ).trim();
	          result[1] = in.substring( pos+1 ).trim();
	      } else {
	          result[0] = in.trim();
	      }

	      return result;
	  }

	
  public synchronized static int ensureInt(final String string, final int defaultValue) {
	    try {
	      return Integer.parseInt(string);
	    } catch (final NumberFormatException e) {
	      return defaultValue;
	    }
	  }

	  public synchronized static double ensureDouble(final String string, final double defaultValue) {
	    try {
	      return Double.parseDouble(string);
	    } catch (final NumberFormatException e) {
	      return defaultValue;
	    }
	  }
		 
	 
	  public static String replaceAll(final String original, final String tokenToBeReplaced, final String value) {
		    //TODO : optimize
		    final StringBuffer result = new StringBuffer();
		    final String[] originalSplit = split(original, tokenToBeReplaced);
		    for (int i = 0; i < originalSplit.length; i++) {
		      result.append(originalSplit[i]);
		      if (i != originalSplit.length - 1) {
		        result.append(value);
		      }
		    }
		    return result.toString();
		  }
	  
		//utility to check the url string inserted by user
		
		public static String checkURL(String url){
			
	        System.out.println(">>> checkURL");
	        System.out.println("in URL: "+url);

	        if(url == null || url.trim().length() == 0 ) {
	              return null;
	        }

	        if (url.endsWith("xmlrpc.php")){
	            
	        } else {
	              if (!url.endsWith("/")){
	                    url+="/";
	              }
	              url+="xmlrpc.php";
	        }
	        System.out.println("out URL: "+url);    
	        return url;

	  }
	  
}

