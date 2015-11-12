/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.core.ManagerAction;
import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import sirius.kernel.di.std.Part;

/**
 *
 * @author simone
 */
public class Text {

    private Source<TextContent> source;

    public Source<TextContent> getSource() {
        return source;
    }

    @Part
    private static ResourceManager resourceManager; //ERROR: l'injection (SIRIUS KERNEL) funziona solo se dichiarata static in quanto richiamata da una new in un metodo static

    private Text(String text, URI uri) throws ManagerAction.ActionException, InvalidURIException {

        init(text, uri);
    }

    public static Text of(URI uri) throws ManagerAction.ActionException, InvalidURIException {
        System.err.println("Text.of");
        //FIXME Aggiungere URI della annotazione
        return new Text("", uri);
    }

    public static Text of(String text, URI uri) throws ManagerAction.ActionException, InvalidURIException {
        System.err.println("Text.of");
        //FIXME Aggiungere URI della annotazione
        return new Text(text, uri);
    }

    private void init(String text, URI uri) throws ManagerAction.ActionException, InvalidURIException {
        System.err.println("Text init() " + resourceManager);
        try {
            source = resourceManager.createSource(uri,
                    new MimeType(ResourceManager.OmegaMimeType.PLAIN.toString()));
            TextContent content = resourceManager.createSourceContent(source);
            if ("".equals(text)) {
                content = resourceManager.updateTextContent(content, text);
            }
            source.setContent(content);

        } catch (MimeTypeParseException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    public void save() throws ManagerAction.ActionException {

        resourceManager.saveSource(source);
    }

}
