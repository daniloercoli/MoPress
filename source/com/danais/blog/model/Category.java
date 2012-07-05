/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */

package com.danais.blog.model;


public class Category {

    
    private String mId;
    private String mLabel;


    public Category(String aId, String aLabel) {
        mId = aId;
        mLabel = aLabel;
    }

    public String getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public String toString() {
        return '[' + mId + '/' + mLabel + ']';
    }

    public boolean equals(Object aObj) {
        return (aObj != null &&
                aObj instanceof Category &&
                mId.equals(((Category) aObj).mId));
    }

    public int hashCode() {
        return mId.hashCode();
    }

}

