/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.core.ManagerAction;
import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.core.datatype.Text;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.entity.TextLocus;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Part;

/**
 *
 * @author simone
 */
public class BaseAnnotationText {

    private static final Logger log = LogManager.getLogger(BaseAnnotationText.class);

    private Annotation<TextContent, BaseAnnotationType> annotation;

    @Part
    private static ResourceManager resourceManager; //ERROR: l'injection (SIRIUS KERNEL) funziona solo se dichiarata static in quanto richiamata da una new in un metodo static

    private BaseAnnotationText(String text, URI uri) throws ManagerAction.ActionException {

        init(text, uri);
    }

    public static BaseAnnotationText of(String text, URI uri) throws ManagerAction.ActionException {
        log.info("BaseAnnotationText.of: text=(" + text + "), uri=(" + uri + ")");
        //FIXME Aggiungere URI della annotazione
        return new BaseAnnotationText(text, uri);
    }

    private void init(String text, URI uri) throws ManagerAction.ActionException {
        log.info("BaseAnnotationText init() resourceManager=(" + resourceManager + ")");
        BaseAnnotationBuilder bab = new BaseAnnotationBuilder().text(text);
        bab.setURI(uri);
        annotation = resourceManager.createAnnotation(
                BaseAnnotationType.class, bab);
    }

    public void addLocus(Text text, int start, int end) throws ManagerAction.ActionException, InvalidURIException {
        log.info("start " + start + ", end " + end);
        TextLocus locus = resourceManager.createLocus(text.getSource(), start, end, TextContent.class);
        resourceManager.updateAnnotationLocus(locus, annotation, TextContent.class);
///////        resourceManager.updateTextAnnotationLocus(text.getSource(), annotation, start, end);
    }

    public void save() throws ManagerAction.ActionException {
        log.info("save()");
        resourceManager.saveAnnotation(annotation);
    }

}
