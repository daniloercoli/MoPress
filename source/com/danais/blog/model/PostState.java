/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.blog.model;

import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;


public class PostState implements ItemStateListener {

    private boolean mPublished = true;
    private boolean mModified = false;

    public boolean isPublished() {
        return mPublished;
    }

    public void setPublished(boolean aPublished) {
        mPublished = aPublished;
    }

    public boolean isModified() {
        return mModified;
    }

    public void setModified(boolean aModified) {
        mModified = aModified;	
    }

    public void itemStateChanged(Item aItem) {
        mModified = true;
    }
    
}
