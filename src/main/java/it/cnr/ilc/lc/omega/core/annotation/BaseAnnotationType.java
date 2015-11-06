/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.entity.Annotation;

/**
 *
 * @author angelo
 */
public class BaseAnnotationType extends Annotation.Type {

    private String text;
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    @Override
//    public <T extends Annotation.Type> T build(Builder<T> builder) {
//        this.builder = (Builder<BaseAnnotationExtension>) builder;
//        return (T) this.builder.build(this);
//    }

}
