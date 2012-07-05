/*
 *   MoPress - a J2ME weblog client.
 *   Copyright (C) 2009 Danais s.r.l
 *   Author Danilo Ercoli
 *
 */
package com.danais.blog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;


public class Preferences {


    private SimpleTimeZone mTimeZone = new SimpleTimeZone();
    private int mMaxBodySize = 4096;
    private int mRecentPostCount = 5;
    private int localeIndex=0;
    
	private String photoEncoding=""; //jpg, png, ecc
    private String audioEncoding="";
    private String videoEncoding="";
    

	private String mStoreName;
    private int mRecordSize = 16;

    private final static String PREFS_HEADER = "MPv";
    private final static byte PREFS_VERSION = 1;
    

    public Preferences(String aStoreName) {
        mStoreName = aStoreName;
    }

    public SimpleTimeZone getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(SimpleTimeZone aTimeZone) {
        mTimeZone = aTimeZone;
    }

    public int getMaxBodySize() {
        return mMaxBodySize;
    }

    public void setMaxBodySize(int aMaxBodySize) {
        mMaxBodySize = aMaxBodySize;
    }

    public int getRecentPostCount() {
        return mRecentPostCount;
    }

    public void setRecentPostCount(int aRecentPostCount) {
        mRecentPostCount = aRecentPostCount;
    }
    
    public int getLocaleIndex() {
		return localeIndex;
	}

	public void setLocaleIndex(int localeIndex) {
		this.localeIndex = localeIndex;
	}

    public void load()  throws RecordStoreException, IOException {
   	   //#debug
  	   System.out.println(">>>load preferences");

        RecordStore records = null;
        byte[] record = null;
        DataInputStream data = null;
        int version;

        try {
            records = RecordStore.openRecordStore(mStoreName, true);
            if (records.getNumRecords() > 0) {
                mRecordSize = records.getRecordSize(1);
                record = records.getRecord(1);
                data = new DataInputStream(new ByteArrayInputStream(record));
                                
                if (!PREFS_HEADER.equals(data.readUTF())) {
                    throw new IOException("Invalid prefs data");
                }
                
                version = data.readByte();
                
                if (PREFS_VERSION != version ) {
                	throw new IllegalArgumentException("Invalid version: " + version);
                }
                
                mTimeZone.restore(data);
                mMaxBodySize = data.readInt();
                mRecentPostCount = data.readInt();
                localeIndex = data.readInt();
                videoEncoding= data.readUTF();
                audioEncoding= data.readUTF();
                photoEncoding= data.readUTF();
                data.close();
            }
        } finally {
            if (records != null) {
                try {
                    records.closeRecordStore();
                } catch (Exception e) {
                	//#debug error
            		System.out.println("load FAILED: errore nella chiusura del recordstore delle preferenze" +e);
                }
            }
        }
    }

    public void save() throws RecordStoreException, IOException {
    	//#debug 
		System.out.println("save preferences");
        RecordStore records = null;
        ByteArrayOutputStream bytes;
        DataOutputStream data;
        byte[] record;
        
        try {
            records = RecordStore.openRecordStore(mStoreName, true);
            bytes = new ByteArrayOutputStream(mRecordSize);
            data = new DataOutputStream(bytes);

            data.writeUTF(PREFS_HEADER);
            data.writeByte(PREFS_VERSION);
            
            mTimeZone.persist(data);
            data.writeInt(mMaxBodySize);
            data.writeInt(mRecentPostCount);
            data.writeInt(localeIndex);
            data.writeUTF(videoEncoding);
            data.writeUTF(audioEncoding);
            data.writeUTF(photoEncoding);
            data.close();
            record = bytes.toByteArray();
                
            if (records.getNumRecords() == 0) {
                records.addRecord(record, 0, record.length);
            } else {
                records.setRecord(1, record, 0, record.length);
            }
        } finally {
            if (records != null) {
                try {
                    records.closeRecordStore();
                } catch (Exception e) {
                	//#debug error
            		System.out.println("errore nella chiusura del recordstore delle preferenze" +e);
                }
            }
        }
    }
    
    
    public String getPhotoEncoding() {
		return photoEncoding;
	}

	public void setPhotoEncoding(String photoEncoding) {
		this.photoEncoding = photoEncoding;
	}

	public String getAudioEncoding() {
		return audioEncoding;
	}

	public void setAudioEncoding(String audioEncoding) {
		this.audioEncoding = audioEncoding;
	}

	public String getVideoEncoding() {
		return videoEncoding;
	}

	public void setVideoEncoding(String videoEncoding) {
		this.videoEncoding = videoEncoding;
	}
}

