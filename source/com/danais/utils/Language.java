//#condition polish.i18n.useDynamicTranslations
package com.danais.utils;

import java.io.IOException;

import de.enough.polish.util.Locale;

public class Language {

	private static Language singletonObject;
	private String localesFileNames[];
	private String languagesNames[];
	
	public String[] getLanguagesNames() {
		return languagesNames;
	}

	public static Language getIstance() {
		if (singletonObject == null) {
			singletonObject = new Language();
		}
		return singletonObject;
	}
	
	// Note that the constructor is private
	private Language() {
		try {
			//#= this.localesFileNames = new String[ ${number(polish.SupportedLocales)} ];
			//#= this.languagesNames = new String[ ${number(polish.SupportedLocales)} ];
			//#message trovate num lingue: ${number(polish.SupportedLocales)}
			int i = 0;
			//#foreach locale in polish.SupportedLocales
				//#message adding supported locale ${locale}
				//#= this.localesFileNames[i] = "${ localefilename(locale) }";
				//#= this.languagesNames[i] = "${ displaylanguage( locale) }";
				i++;					
			//#next locale
			
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to init Language " + e );
		}
	}
		
		
	/**
	 * @param url
	 */
	public void loadTranslations(int localeIndex) {
		try {
			String url=this.localesFileNames[localeIndex];		
			Locale.loadTranslations(url);
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load translations " + e );
		}
	}
}
