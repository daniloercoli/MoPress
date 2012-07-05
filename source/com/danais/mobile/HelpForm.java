package com.danais.mobile;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

public class HelpForm extends Form implements CommandListener {
  
  private final Command back;
  private final Canvas kmlCanvas;
  private final MIDlet midlet;
  
  /*
	 * Costanti per la scelta dello stile del Font
	 */
	private final static String[] STYLE_FONT_LABELS = { "PLAIN", "ITALIC",
			"BOLD", "UNDERLINED" };
	private final static int[] STYLE_FONT_VALUES = { Font.STYLE_PLAIN,
			Font.STYLE_ITALIC, Font.STYLE_BOLD, Font.STYLE_UNDERLINED };

	/*
	 * Costanti per la scelta della face del Font
	 */
	private final static String[] FACE_FONT_LABELS = { "SYSTEM",
			"PROPORTIONAL", "MONOSPACE" };
	private final static int[] FACE_FONT_VALUES = { Font.FACE_SYSTEM,
			Font.FACE_PROPORTIONAL, Font.FACE_MONOSPACE };

	/*
	 * Costanti per la scelta della size del Font
	 */
	private final static String[] SIZE_FONT_LABELS = { "SMALL", "MEDIUM",
			"LARGE" };
	private final static int[] SIZE_FONT_VALUES = { Font.SIZE_SMALL,
			Font.SIZE_MEDIUM, Font.SIZE_LARGE };


  public HelpForm(final MIDlet midlet, final Canvas returnTo) {
    super("Guida");
    this.kmlCanvas = returnTo;   
    this.midlet=midlet;

	
    StringItem intro = new StringItem("Introduzione","Quando l'applicazione GeoMobile viene avviata per la prima volta, " +
    		"la mappa viene centrata sulla città di Bologna ed il posizionamento risulta disattivato.\n");
    
    StringItem trasferimento = new StringItem("Trasferimento Dati","L'indicatore del trasferimento dati, presente sulla maggior parte dei telefoni " +
    		"accanto all'indicatore del livelli di segnale, quando attivo indica che si sta accendo alla rete internet per scaricare i file delle mappe.\n"+
    		"Il download delle mappe potrebbe provocare la trasmissione di grandi quantità di dati sulla rete del proprio fornitore di servizi. " +
    		"Per informazioni delle tariffe relative alle trasmissioni dati.\n");		
    		   
    StringItem gps = new StringItem("GPS","L'indicatore dello stato del posizionamento è visibile in una sezione dello schermo, " +
    		"nella parte in alto a sinistra, ed indica lo stato attuale in cui si trova il sistema di posizionamento.\n");
    
    StringItem comandi = new StringItem("Comandi ",
    		"- Per eseguire degli spostamenti in mappa, utilizzare il tasto di scorrimento del dispositivo portatile; oppure è possibile utilizzare i tasti 2-6-8-4.\n"+
    		"- Per eseguire lo zoom della mappa utilizzare i tasti * e #.\n" +
    		"- Per visualizzare le informazioni dettagliate di oggetto presente in mappa premere il tasto 5.\n"+
    		"- Per visualizzare la propria posizione in mappa premere il tasto 9 (uno dei sistemi di posizionamento deve essere attivo).\n"+
    		"- Per abilitare la modalita' full screen premere il tasto 1.\n" +
    		"- Per scegliere la cartografia di sfondo selezionare Opzioni > Mappa.\n");

    back = new Command("Indietro", Command.BACK, 0);   
    append(intro);
    append(comandi);
    append(gps);
    append(trasferimento);
    


    addCommand(back);
    setCommandListener(this);
  }

  public void commandAction(final Command cmd, final Displayable d) {
    if (cmd == back) {
      Display.getDisplay(midlet).setCurrent(kmlCanvas);
    }
  }
}


