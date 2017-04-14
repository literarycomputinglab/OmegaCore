/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.dto;

import it.cnr.ilc.lc.omega.core.datatype.ADTAnnotation;

/**
 *
 * @author simone
 */
public class ADTAnnotationSource implements DTOValueRM<ADTAnnotation>{
    
    ADTAnnotation  value;

    @Override
    public ADTAnnotation getValue() {
        return value;
    }

    @Override
    public <K extends DTOValueRM<ADTAnnotation>> K withValue(ADTAnnotation t) {
        this.value = t;
        return (K) this;
    }
    
}
