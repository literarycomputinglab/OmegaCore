/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.util;

import java.net.URI;

/**
 *
 * @author simone
 */
public class Utils {

    public static final String CONTENT_PATH = "content";
    public static final String SEPARATOR_URL_PATH = "/";

  public static String appendContentID(URI uri) {
      String s = uri.toASCIIString();
        if (!s.endsWith(SEPARATOR_URL_PATH)) {
            s = s.concat(SEPARATOR_URL_PATH);
        }
        return appendId(s, CONTENT_PATH);
    }

    private static String appendId(String s, String type) {

        return s + type + SEPARATOR_URL_PATH + System.currentTimeMillis();
    }

    public static void main(String[] args) {

        URI uri = URI.create("http://claviusontheweb.it:8080/exist/rest//db/clavius/documents/147/147.txt");
        
        System.err.println(uri.getSchemeSpecificPart());

    }
    
    
}


