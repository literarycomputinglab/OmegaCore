/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.core.ManagerAction;
import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationRelation;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import sirius.kernel.di.std.Part;

/**
 *
 * @author simone
 */
public abstract class ADTAbstractAnnotation implements ADTAnnotation {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(ADTAbstractAnnotation.class);

    @Part
    protected static ResourceManager resourceManager;

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

    protected abstract void setAnnotation(Annotation<?, ?> annotation);

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

    public static List<Annotation<TextContent, ?>> loadAllTextAnnotations() throws ManagerAction.ActionException {

        List<Annotation<TextContent, ?>> lostc = resourceManager.loadAllAnnotation(TextContent.class);

        log.info("loadAllAnnotation() result lenght " + lostc.size());

        return lostc;
    }

    public static Annotation<TextContent, ?> loadTextAnnotation(URI uri) throws ManagerAction.ActionException {

        Annotation<TextContent, ?> ann = resourceManager.loadAnnotation(uri, TextContent.class);
        log.info("loadAnnotation() ann is [" + ann + "]");
        //log.info ("loci " + ann.getLociIterator(TextContent.class).next());
        return ann;
    }

    public static ADTAnnotation delete(ADTAnnotation adtAnnotation) throws ManagerAction.ActionException {
        log.info("delete()");
        if (null != adtAnnotation) {
            ADTAbstractAnnotation adtaa = (ADTAbstractAnnotation) adtAnnotation;
            if (adtaa.isRemovable()) {
                resourceManager.deleteAnnotation(adtaa.getAnnotation());
                adtaa.setAnnotation(null);
                return null;
            } else {
                log.warn("The selected annotation cannot be removed! " + adtaa.getAnnotation().getUri());
            }
        } else {
            log.warn("Annotation is null!");
        }
        return adtAnnotation;
    }

}
