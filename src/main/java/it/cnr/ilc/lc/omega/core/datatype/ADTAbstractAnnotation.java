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

            log.info("parameters lenght: " + params.length);

            Class<?>[] listOfParameterClass = new Class<?>[params.length + 1];

            listOfParameterClass[0] = URI.class;
            for (int i = 0; i < params.length; i++) {
                listOfParameterClass[i + 1] = params[i].getClass();
            }

            Object[] listOfParameter = new Object[params.length + 1];

            listOfParameter[0] = uri;
            for (int i = 0; i < params.length; i++) {
                listOfParameter[i + 1] = params[i];
            }

            Method ofMethod = clazz.getMethod("of", listOfParameterClass);
            log.info("ofMethods " + clazz.getCanonicalName());
            return (T) ofMethod.invoke(null, listOfParameter);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            log.error(ex);
        }
        return null;
    }

}
