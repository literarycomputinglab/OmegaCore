/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.annotation;

import it.cnr.ilc.lc.omega.core.ManagerAction;
import it.cnr.ilc.lc.omega.core.OmegaCore;
import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.core.datatype.Text;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeTypeParseException;
import sirius.kernel.di.std.Part;

/**
 *
 * @author simone
 */
public class BaseAnnotationText {

    private Annotation<TextContent, BaseAnnotationType> annotation;

    @Part
    private static ResourceManager resourceManager; //ERROR: l'injection funziona solo se dichiarata static in quanto richiamata da una new in un metodo static
    
    private BaseAnnotationText(String text) throws ManagerAction.ActionException {

        init(text);
    }

    public static BaseAnnotationText of(String text) throws ManagerAction.ActionException {
        System.err.println("BaseAnnotationText.of");
        return new BaseAnnotationText(text);
    }

    private void init(String text) throws ManagerAction.ActionException {
        System.err.println("BaseAnnotationText init() " + resourceManager);
        annotation = resourceManager.createAnnotation(
                BaseAnnotationType.class,
                new BaseAnnotationBuilder().text(text));
    }

    public void addLocus(Text text, int start, int end) {
        
     //   resourceManager.updateAnnotationLocus(text.getSource(), annotation, start, end);
    }
    
    public void save() throws ManagerAction.ActionException {

        resourceManager.saveAnnotation(annotation);
    }

}
