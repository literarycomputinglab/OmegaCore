/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.entity.Annotation;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author angelo
 */
@Entity
@Indexed
public class BaseAnnotationType extends Annotation.Data {

    @Field
    @Column(length = 4096)
    private String text;
    
    public String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

//    @Override
//    public <T extends Annotation.Data> T build(Builder<T> builder) {
//        this.builder = (Builder<BaseAnnotationExtension>) builder;
//        return (T) this.builder.build(this);
//    }

    @Override
    public <E extends Annotation.Data> E get() {
        return (E) this;
    }
}
