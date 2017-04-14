/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.entity.RelationType;

/**
 *
 * @author simone
 */
public enum AnnotationRelationType implements RelationType {
    
       TYPE_OF, PART_OF, CATALOGRAPHIC_DESCRIPTION_OF, HAS_CHILD, HAS_PARENT,
       HAS_RESOURCE;
     
}
