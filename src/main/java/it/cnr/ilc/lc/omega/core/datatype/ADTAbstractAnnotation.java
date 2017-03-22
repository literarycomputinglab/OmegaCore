/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationRelation;

/**
 *
 * @author simone
 */
public abstract class ADTAbstractAnnotation implements ADTAnnotation {
    
    @Override
    public void registerAsSource(AnnotationRelation relation) {
        
        relation.setSourceAnnotation(getAnnotation());
        getAnnotation().addRelation(relation);
        
    }

    @Override
    public void registerAsTarget(AnnotationRelation relation) {
        
        relation.setTargetAnnotation(getAnnotation());
    }

    protected abstract Annotation<?,?> getAnnotation();

    
}
