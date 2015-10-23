/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.parser;

/**
 *
 * @author angelo
 */
public abstract class Resource {

   public enum Structure {

        DOCUMENT,
        TEXT
    }

    public enum Granularity {

        LEVEL1,
        LEVEL2,
        LEVEL3,
        LEVEL4,
        LEVEL5
    }
}
