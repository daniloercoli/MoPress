package com.danais.mobile;

import com.danais.security.MD5;

public class AuthPassCripto {

	public AuthPassCripto() {
		
	}

	public static synchronized String getCriptedPassword(String plainPassword) throws Exception{
		if(plainPassword==null || plainPassword.equals(""))
			return "";
		
 	    MD5 md5 = new MD5();
   	    md5.Update(plainPassword, null);
   	    String hash = md5.asHex();
   	    md5.Final();
   	    md5.Init();
   	    md5.Update(hash, null);
   	    hash = md5.asHex();
   	    return hash;
	}	
}
