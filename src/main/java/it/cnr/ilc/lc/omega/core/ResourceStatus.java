/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.annotation.BaseAnnotationText;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import java.util.Map;

/**
 *
 * @author simone
 */
public class ResourceStatus < T extends Content, E extends Annotation.Type> {

   private Class<?> clazz;
   private int start;
   private int end;
   private Source<T> source;
   private Annotation<T, E> annotation;

    public Class<?> getClazz() {
        return clazz;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Source<?> getSource() {
        return source;
    }

    public Annotation<?, ?> getAnnotation() {
        return annotation;
    }
    
    public ResourceStatus(){
        
    }
    
    public ResourceStatus clazz(Class<?> clazz){
        this.clazz = clazz;
        return this;
    }
    
    public ResourceStatus start(int start){
        this.start = start;
        return this;
    }
    
    public ResourceStatus end(int end){
        this.end = end;
        return this;
    }
    
    public ResourceStatus source(Source<T> source){
        this.source = source;
        return this;
    }
    
    public ResourceStatus annotation(Annotation<T,E> annotation){
        this.annotation = annotation;
        return this;
    }
    
    
    
}
