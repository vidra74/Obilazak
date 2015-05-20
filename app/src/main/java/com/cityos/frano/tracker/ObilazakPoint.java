package com.cityos.frano.tracker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;


/**
 * Created by Frano on 9.5.2015..
 */
public class ObilazakPoint implements Serializable {
    private String Vrijeme;
    private String Longitude;
    private String Latitude;
    private String Opis;
    private String FileImagePath;

    public String getVrijeme() {
        return Vrijeme;
    }

    public void Spremi(String filename, Context ctx) {

        // save the object to file
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(this);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Ucitaj(String filename,  Context ctx) {

        // read the object from file
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = ctx.openFileInput(filename);
            in = new ObjectInputStream(fis);
            ObilazakPoint temp = (ObilazakPoint) in.readObject();
            this.setVrijeme(temp.getVrijeme());
            this.setLongitude(temp.getLongitude());
            this.setLatitude(temp.getLatitude());
            this.setOpis(temp.getOpis());
            this.setFileImagePath(temp.getFileImagePath());
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void setVrijeme(String vrijeme) {
        Vrijeme = vrijeme;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getOpis() {
        return Opis;
    }

    public void setOpis(String opis) {
        Opis = opis;
    }

    public String getFileImagePath() {
        return FileImagePath;
    }

    public void setFileImagePath(String fileImagePath) {
        FileImagePath = fileImagePath;
    }

    public ObilazakPoint(String strVrijeme,
                         String strLongitude,
                         String strLatitude,
                         String strOpis,
                         String strFileNamePath){
        Vrijeme = strVrijeme;
        Longitude = strLongitude;
        Latitude = strLatitude;
        Opis = strOpis;
        FileImagePath = strFileNamePath;

    }
}
