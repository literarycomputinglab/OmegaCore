/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationRelation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author simone
 */
public abstract class ADTAbstractAnnotation implements ADTAnnotation {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(ADTAbstractAnnotation.class);

    @Override
    public void registerAsSource(AnnotationRelation relation) {

        relation.setSourceAnnotation(getAnnotation());
        getAnnotation().addRelation(relation);

    }

    @Override
    public void registerAsTarget(AnnotationRelation relation) {

        relation.setTargetAnnotation(getAnnotation());
    }

    protected abstract Annotation<?, ?> getAnnotation();

    public static <T extends ADTAbstractAnnotation> T of(Class<T> clazz, URI uri, Object... params) {
        try {
            
            log.info("parameters lenght: "+ params.length);


            Object o1 = params[0];
            //Object o2 = params[1];

            Class<?> cl1 = o1.getClass();
            //Class<?> cl2 = o2.getClass();

            log.info("parametri inviati: " + "URI: (" + uri + ") - o1: (" + o1 + "), cl1: (" + cl1 + ").");

            Method ofMethod = clazz.getMethod("of", URI.class, cl1);
            log.info("ofMethods " + clazz.getCanonicalName());
            return (T) ofMethod.invoke(null, uri, o1);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            log.error(ex);
        }
        return null;
    }

}
