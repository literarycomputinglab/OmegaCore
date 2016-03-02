/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.entity.AbstractAnnotationBuilder;
import java.net.URI;


/**
 *
 * @author angelo
 */
public class BaseAnnotationBuilder extends AbstractAnnotationBuilder<BaseAnnotationType>{

    private String text = "";
    
    public BaseAnnotationBuilder text(String f) {
        this.text = f;
        return this;
    }
    
    public BaseAnnotationBuilder URI(URI uri) {
        setURI(uri);
        return this;
    }
    
    

    public BaseAnnotationType build(BaseAnnotationType extension) {
        extension.setText(this.text);
        return extension;
    }

}
