package com.danais.mobile;

import java.util.Timer;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Screen;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.danais.blog.BlogController;
import com.danais.blog.BlogDeleteForm;
import com.danais.blog.BlogPostForm;
import com.danais.blog.BlogUpdateForm;
import com.danais.blog.LoginDetailsForm;
import com.danais.blog.PostList;
import com.danais.blog.Preferences;
import com.danais.blog.PreferencesForm;
import com.danais.blog.conn.BlogConn;
import com.danais.blog.conn.ConnectionMessageBox2;
import com.danais.blog.model.Blog;
import com.danais.utils.Globals;
import com.danais.utils.Language;
import com.danais.utils.MessageBox;

import de.enough.polish.ui.UiAccess;

public class MainCanvas extends Canvas implements CommandListener {

	private MIDlet midlet;
	private boolean initialized;
	private Timer timer =null;
	private static final Font FONT = Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD, Font.SIZE_MEDIUM);
	

    public final static String LABEL_OKAY = "Ok";
    public final static String LABEL_CANCEL = "Cancel";

	
	private final static String LABEL_NEW = "New Post";
	private final static String LABEL_DRAFT = "Local Posts";
	private final static String LABEL_RECENT = "Recent Posts";
	private final static String LABEL_REFRESH = "Refresh Blog";
	private final static String LABEL_ADD = "Add Blog";
	private final static String LABEL_REMOVE = "Remove Blog";
	private final static String LABEL_SETUP = "Setup";
	private final static String LABEL_ABOUT = "About";
	private final static String LABEL_EXIT = "Exit";
	
	private final String modNew = "new";
	private final String modEdit = "edit";
	private final static String LABEL_EDIT_CREDENTIALS = "Edit credentials";


	
	private	Command newPostCommand = new Command(LABEL_NEW, Command.SCREEN, 1);
	private	Command draftPostCommand = new Command(LABEL_DRAFT, Command.SCREEN, 2);
	private	Command recentPostCommand= new Command(LABEL_RECENT, Command.SCREEN, 3);
	private	Command refreshBlogCommand= new Command(LABEL_REFRESH, Command.SCREEN, 4);
	private	Command addBlogCommand= new Command(LABEL_ADD, Command.SCREEN, 5);
	private	Command removeBlogCommand= new Command(LABEL_REMOVE, Command.SCREEN, 6);
	
	private	Command editCredentialsCommand= new Command(LABEL_EDIT_CREDENTIALS, Command.SCREEN, 6);

	private	Command setupCommand= new Command(LABEL_SETUP, Command.SCREEN, 7);
	private	Command aboutCommand= new Command(LABEL_ABOUT, Command.SCREEN, 8); 
	private Command exitCommand = new Command(LABEL_EXIT, Command.EXIT, 9);
	
    private final static String STORE_PREFS = "MoPressPreferences"; //nome del RMS per le preferenze
    private Preferences blogPrefs = null;
    private BlogController blogController = null;

    private Vector mDisplayStack = new Vector(4); //stack degli elementi da visualizzare

	
	public MainCanvas(MIDlet midlet) {
		this.midlet = midlet;
		Globals globals = Globals.getIstance();
		blogController= new BlogController(this);
		blogPrefs = new Preferences(STORE_PREFS);
	}
	
		
	private void initialize() {
        //#debug
  		System.out.println(">>>initialize");

		try {
			blogPrefs.load();
		} catch (Exception e) {
			displayError(e, "Non riesco a caricare le preferenze");
		}
		
		//carico il locale dalle preferenze
		//#if polish.i18n.useDynamicTranslations	
			Language.getIstance().loadTranslations(blogPrefs.getLocaleIndex());
		//#endif
		
		try {
			String[] rmsBlogName= blogController.loadBlogNameFromRMS();
			
			for (int i = 0; i < rmsBlogName.length; i++) {
				Blog currBlog=blogController.loadBlog(rmsBlogName[i]);
				blogController.addBlog(currBlog, false);
			}
		} catch (Exception e) {
			displayError(e, "Non riesco a leggere i blog precedenti");
		}
		
		displayBlogListUI();
		initialized = true;
		repaint();
	}
	
	public void commandAction(final Command cmd, final Displayable d) {
		if (cmd == addBlogCommand) {
			
			String url="";
			
				url="http://testing.mopress.danais.org/";				
				
				
            LoginDetailsForm login= new LoginDetailsForm(this,url ,"editore", "cancello", null, blogController);
            pushCurrent(login);
        } else if (cmd == newPostCommand) {
              int selected = ((List) d).getSelectedIndex();
              BlogPostForm postform = new BlogPostForm(blogController.getBlog(selected), blogController, this);
              pushCurrent(postform);
        }else if (cmd == draftPostCommand) {
	            int selected = ((List) d).getSelectedIndex();
	            PostList postlist = new PostList(this, blogController, blogController.getBlog(selected), BlogController.TYPE_DRAFT);
	           // setCurrent(postlist);
        } else if (cmd == recentPostCommand) {
	         int selected = ((List) d).getSelectedIndex();
	         PostList postlist = new PostList(this, blogController, blogController.getBlog(selected), BlogController.TYPE_RECENT);
	         //setCurrent(postlist);
        }
          else if (cmd == exitCommand) {
			try {
				MoPressMidlet.instance.destroyApp(true);
			} catch (final MIDletStateChangeException ignore) { }
		} else if (cmd == aboutCommand)	{
			  pushCurrent(new AboutForm(this));
		} else if (cmd == refreshBlogCommand)	{
			int selected = ((List) d).getSelectedIndex();
            new BlogUpdateForm(blogController.getBlog(selected), this);
		} else if (cmd == editCredentialsCommand) {
			//TODO
	        int selected = ((List) d).getSelectedIndex();
			Blog selectedBlog = blogController.getBlog(selected);
			String url = selectedBlog.getBlogUrl();
			String user = selectedBlog.getUsername();
			String pwd = selectedBlog.getPassword();
			LoginDetailsForm login= new LoginDetailsForm(this,url , user, pwd, selectedBlog, blogController);
            pushCurrent(login);
		} else if (cmd == setupCommand) {
			pushCurrent(new PreferencesForm(this, this.getPreferences()));
		} else if (cmd.getLabel() == LABEL_REMOVE) {
            int selected = ((List) d).getSelectedIndex();
            new BlogDeleteForm(this,selected,blogController);
        } else if(cmd == List.SELECT_COMMAND){
        //togliamo il tasto fire di mezzo	
        }
		else {
			displayError("Non ancora disponibile");
		}
	}

	/**
	 * Viene chiamata quando abbiamo nuovi blog da aggiungere
	 * @param blogs
	 */

	public void addNewBlogs(Blog[] blogs){
        for (int i = 0; i < blogs.length; i++) {
        	blogController.addBlog(blogs[i], true);        	
        }
        displayBlogListUI();
	}

    public void displayBlogListUI() {
    	
    	//#style blogList
        List blogs = new List("My Blogs", List.IMPLICIT);
        String[] blogCaricati= blogController.getBlogNames();
        for (int i = 0; i < blogCaricati.length; i++) {
			String blogCorrente = blogCaricati[i];
			//#style blogEntry
			blogs.append( blogCorrente, null);
		}
        
        if (blogController.getBlogCount() > 0) {
        	Command postCmd=new Command("Post", Command.SCREEN, 1);
        	blogs.addCommand(postCmd);
        	UiAccess.addSubCommand( newPostCommand, postCmd, blogs ); 
        	UiAccess.addSubCommand( draftPostCommand, postCmd, blogs );
        	UiAccess.addSubCommand( recentPostCommand, postCmd, blogs );
            blogs.addCommand(refreshBlogCommand);
        }
        blogs.addCommand(addBlogCommand);
        
        if (blogController.getBlogCount() > 0) {
            blogs.addCommand(removeBlogCommand);
            blogs.addCommand(editCredentialsCommand);

        } else {
        	//#style blogEntry
        	blogs.append("Empty blogs list! Add a blog from menu" , null);
        }
        
        blogs.addCommand(setupCommand);
        blogs.addCommand(aboutCommand);
        blogs.addCommand(exitCommand);
        blogs.setCommandListener(this);
        blogs.setSelectCommand(null);

        setCurrent(blogs);
        repaint();
    }
	
    
	protected void paint(final Graphics g) {

		if (!this.initialized) {
	        //#debug
      		System.out.println("INIT GRAFICA");
		  	
      		initialize();
			
		} else {
			/*int altezzaCorrente = getHeight();
			int larghezzaCorrente = getWidth();

			g.setColor(0xFFFFFFFF);
			g.setClip(0, 0, larghezzaCorrente, altezzaCorrente);
			*/
		}
	}

	/**
	 * Ritorna le preferenze dell'utente
	 * @return
	 */
    public Preferences getPreferences() {
        return blogPrefs;
    }

	
	/**
	  * Aggiunge in ultima posizione la schermata passata come parametro.
	  * e la visualizza immediatamente.
	  * @param aCurrent
	  */
	 
	 public synchronized void pushCurrent(Displayable aCurrent) {
	        mDisplayStack.addElement(aCurrent);
	        show(aCurrent);
	        dumpStack();
	    }
	 	
	 /**
	  * Inserisce in ultima posizione la schermata passata come parametro
	  * e la visualizza immediatamente.
	  * n.b: quella che era in ultima pos viene eliminata dallo stack
	  * @param aCurrent
	  */
	    public synchronized void setCurrent(Displayable aCurrent) {
	        if (mDisplayStack.size() == 0) {
	            mDisplayStack.addElement(aCurrent);
	        } else {
	            mDisplayStack.setElementAt(aCurrent, mDisplayStack.size() - 1);
	        }

	        show(aCurrent);
	        dumpStack();
	    }

	    public synchronized void popCurrent() {
	        popCurrent(1);
	    }

	    public synchronized void popCurrent(int aCount) {
	        if (aCount >= 1) {
	            int size = mDisplayStack.size();

	            if (size < 1) {
	                throw new RuntimeException("Cannot go back that far");
	            }
	            for (int i = 1; i <= aCount; i++) {
	                mDisplayStack.removeElementAt(size - i);
	            }
	            show((Displayable) mDisplayStack.lastElement());
	        }
	        dumpStack();
	    }

	    private void dumpStack() {
	    	
	        Object frame;
	        //#debug
      		System.out.println("Stack size: " + mDisplayStack.size());

	        for (int i = mDisplayStack.size() - 1; i >= 0; i--) {
	            frame = mDisplayStack.elementAt(i);
	            if (frame instanceof Screen) {
		       		//#debug
		       		System.out.println("  " + ((Screen) frame).getTitle());
	            } else {
		       		//#debug
		       		System.out.println("  " + frame);
	            }
	        } 
	    }
	
	
	/**
	 * Chiama la midlet per accedere alle risorse definite nel file jad
	 * @param key
	 * @return
	 */
	public String getAppProperty(String key){
		return midlet.getAppProperty(key);
	}

	
	/**
	 * Mostra il Display passata come parametro. Non 
	 * utilizza la coda dei Display element
	 * 
	 * @param next
	 */
	public void show(final Displayable next) {
		Display.getDisplay(this.midlet).setCurrent(next);
	}
	
	/**
	 * @param e
	 * @param message
	 */
	public synchronized void displayErrorBlocking(final Exception e, String message) {
		displayErrorBlocking(message + "\n" + e.getMessage());
	}

	// Utility routine to display errors
	public synchronized void displayErrorBlocking(String msg) {
		//#debug error
		System.out.println(msg);
		
		//per sicurezza rimetto in primo piano l'ultima schermata registrata
		//in questo modo non ho 2 alert sovrapposti
		show((Displayable) mDisplayStack.lastElement());
		
		new MessageBox("MoPress Error", msg, AlertType.ERROR, this.midlet, (Displayable) mDisplayStack.lastElement());
	}
	
	// Utility routine to display message
	public synchronized void displayMessageBlocking(String msg) {
		//#debug info
		System.out.println(msg);
		
		//per sicurezza rimetto in primo piano l'ultima schermata registrata
		//in questo modo non ho 2 alert sovrapposti che con k2mepolish e le trasparenze erano una schifezza
		show((Displayable) mDisplayStack.lastElement());
			
		new MessageBox("MoPress Information", msg, AlertType.INFO, this.midlet, (Displayable) mDisplayStack.lastElement());
	}
	
	// Utility routine to display msg
	public synchronized void displayMessage(String msg) {
		//#debug info
		System.out.println(msg);
		show((Displayable) mDisplayStack.lastElement());
		
		//#style generalAlert
		Alert errorAlert = new Alert("MoPress Info", msg, null, AlertType.INFO);
		errorAlert.setTimeout(Alert.FOREVER);
		//FIXME:Display.getDisplay(midlet).setCurrent(errorAlert,(Displayable) mDisplayStack.lastElement()); 
		Display.getDisplay(midlet).setCurrent(errorAlert);
	}
	
	/**
	 * @param e
	 * @param message
	 */
	public synchronized void displayError(final Exception e, String message) {
		displayError(message + "\n" + e.getMessage());
	}

	// Utility routine to display errors
	public synchronized void displayError(String msg) {
		//#debug error
		System.out.println(msg);

		//per sicurezza rimetto in primo piano l'ultima schermata registrata
		//in questo modo non ho 2 alert sovrapposti che con k2mepolish e le trasparenze erano una schifezza
		show((Displayable) mDisplayStack.lastElement());
		
		//#style generalAlert
		Alert errorAlert = new Alert("MoPress Error", msg, null, AlertType.ERROR);
		errorAlert.setTimeout(Alert.FOREVER);
		Display.getDisplay(midlet).setCurrent(errorAlert);
	}
	
	// Utility routine to display running indicator for connection
	public synchronized void displayConnectionInProgress(String msg, BlogConn conn) {
		//#debug
		System.out.println(msg);			
		new ConnectionMessageBox2("MoPress Information", msg, AlertType.INFO,conn, this.midlet, (Displayable) mDisplayStack.lastElement());
	}
	
	

		
} 