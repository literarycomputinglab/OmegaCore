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
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Part;

/**
 *
 * @author simone
 * @author angelo
 */
public class Text {

    private static final Logger log = LogManager.getLogger(Text.class);

    private Source<TextContent> source;

    public Source<TextContent> getSource() {
        return source;
    }

    @Part
    private static ResourceManager resourceManager; //ERROR: l'injection (SIRIUS KERNEL) funziona solo se dichiarata static in quanto richiamata da una new in un metodo static

    private Text(String text, URI uri) throws ManagerAction.ActionException, InvalidURIException {

        init(text, uri);
    }

    private Text(Source<TextContent> source) {

        this.source = source;
    }

    public static Text of(URI uri) throws ManagerAction.ActionException, InvalidURIException {
        log.info("uri " + uri);
        //FIXME Aggiungere URI della annotazione
        return new Text("", uri);
    }

    public static Text of(String text, URI uri) throws ManagerAction.ActionException, InvalidURIException {
        log.info("text " + text + "uri " + uri);
        //FIXME Aggiungere URI della annotazione
        return new Text(text, uri);
    }

    public static Text load(URI uri) throws ManagerAction.ActionException {
        log.info("uri " + uri);

        return new Text(resourceManager.loadSource(uri, TextContent.class));
    }

    private void init(String text, URI uri) throws ManagerAction.ActionException, InvalidURIException {
        log.info("resourceManager=(" + resourceManager + ")");
        try {
            source = resourceManager.createSource(uri,
                    new MimeType(ResourceManager.OmegaMimeType.PLAIN.toString()));
            TextContent content = resourceManager.createSourceContent(source);
            if (!"".equals(text)) {
                content = resourceManager.updateTextContent(content, text);
            }
            source.setContent(content);

        } catch (MimeTypeParseException ex) {
            log.error("in init()", ex);
        }

    }

    public void save() throws ManagerAction.ActionException {
        log.info("saving...");
        resourceManager.saveSource(source);
        log.info("saved...");

    }

    // getElement(Granularity)
    @Override
    public String toString() {
        return getSource().getContent().getText();
    }

}
