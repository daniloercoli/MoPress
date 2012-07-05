package com.danais.utils.mm;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;

public class PhotoPreviewForm extends Form {

	private TextField description;
	
	public PhotoPreviewForm(String title, MediaObject multimediaObj) throws Exception {
		super(title);

		Image image = Image.createImage(multimediaObj.getMediaData(), 0, multimediaObj.getMediaData().length);
		append(image);
	
		description=new TextField("Description", multimediaObj.getDescription(), 64, TextField.ANY);
		append(description);
	}
	 
	public String getDescription(){
		return description.getString().trim();
	}
	
}
