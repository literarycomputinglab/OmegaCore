/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;

/**
 *
 * @author simone
 */
public class Text {
    
    private Source<TextContent> source;

    public Source<TextContent> getSource() {
        return source;
    }

    public void setSource(Source<TextContent> source) {
        this.source = source;
    }
}
