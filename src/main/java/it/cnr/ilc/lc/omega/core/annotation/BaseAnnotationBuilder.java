/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;

/**
 *
 * @author angelo
 */
public class BaseAnnotationBuilder implements AnnotationBuilder<BaseAnnotationType> {

    private String text = "";
    private String uri;
    
    public BaseAnnotationBuilder text(String f) {
        this.text = f;
        return this;
    }

    @Override
    public BaseAnnotationType build(BaseAnnotationType extension) {
        extension.setText(this.text);
        return extension;
    }

    @Override
    public void setURI(String uri) {
        this.uri = uri;
    } 
    
    @Override
    public String getURI() {
        return uri;
    }

}
