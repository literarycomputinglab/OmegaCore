/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.core.ManagerAction;
import it.cnr.ilc.lc.omega.entity.AnnotationRelation;

/**
 *
 * @author simone
 */
public interface ADTAnnotation {

    public void registerAsSource(AnnotationRelation relation);

    public void registerAsTarget(AnnotationRelation relation);

    public void save() throws ManagerAction.ActionException;

    public boolean isRemovable()  throws ManagerAction.ActionException;
}
