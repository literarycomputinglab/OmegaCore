/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.datatype;

import it.cnr.ilc.lc.omega.core.ManagerAction;
import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.core.SearchManager;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    @Part
    private static SearchManager searchManager;

    private Text(String text, URI uri) throws ManagerAction.ActionException {

        init(text, uri);
    }

    private Text(Source<TextContent> source) {

        this.source = source;
    }

    public static Text of(URL url) throws ManagerAction.ActionException, InvalidURIException {
        log.info("url " + url);
        //FIXME Aggiungere URI della annotazione (?)
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            log.error(ex.getMessage());
            throw new InvalidURIException("Can't convert " + url.toString() + " to a URI", ex);
        }

        return new Text("", uri);
    }

    public static Text of(String text, URI uri) throws ManagerAction.ActionException {
        if (null == text) {
            log.error("Invalid text parameter: text is null!");
            throw new NullPointerException("Invalid text parameter!");
        }
        try {
            if (null != uri.toURL()) { //SE la URI e' una URL => eccezione!
                log.error("URI must not be an URL when text is specified! URI: " + uri.toASCIIString());
                throw new InvalidURIException("URI must not be an URL when text is specified! URI: " + uri.toASCIIString());
            }
        } catch (MalformedURLException ex) {
        } catch (IllegalArgumentException iae) {
        }

        log.info("text " + text + ", uri " + uri);
        //FIXME Aggiungere URI della annotazione
        return new Text(text, uri);
    }

    public static Text load(URI uri) throws ManagerAction.ActionException {
        log.info("uri " + uri);

        return new Text(resourceManager.loadSource(uri, TextContent.class));
    }

    public static List<Text> loadBySearch(String query) throws ManagerAction.ActionException {

        List<Text> array = new ArrayList<>();
        log.info("query " + query);
        List<Source<TextContent>> lostc = searchManager.searchByKeyword(query);
        for (Source<TextContent> source : lostc) {
            array.add(new Text(source));
        }
        return array;
    }

    public static List<Text> loadAll() throws ManagerAction.ActionException {

        List<Text> array = new ArrayList<>();
        List<Source<TextContent>> lostc = resourceManager.loadAllSources(TextContent.class);
        log.info("loadAll() result lenght " + lostc.size());

        for (Source<TextContent> source : lostc) {
            array.add(new Text(source));
        }
        return array;
    }

    public static List<Annotation<TextContent, ?>> loadAllAnnotations() throws ManagerAction.ActionException {

        List<Annotation<TextContent, ?>> lostc = resourceManager.loadAllAnnotation(TextContent.class);
        log.info("loadAllAnnotation() result lenght " + lostc.size());

        return lostc;
    }

    public static Annotation<TextContent, ?> loadAnnotation(URI uri) throws ManagerAction.ActionException {

        Annotation<TextContent, ?> ann = resourceManager.loadAnnotation(uri, TextContent.class);
        log.info("loadAnnotation() ann is [" + ann + "]");
        //log.info ("loci " + ann.getLociIterator(TextContent.class).next());
        return ann;
    }

    public List<TextualHit> search(String query) {

        List<TextualHit> ret = new ArrayList<>();
        Pattern p = Pattern.compile(query);
        Matcher m = p.matcher(getTextContent());

        while (m.find()) {
            ret.add(new TextualHit(m.start(), m.end()));
        }

        return ret;
    }

    private void init(String text, URI uri) throws ManagerAction.ActionException {
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

    public String getTextContent() {
        String ret = "";
        if (null != getSource()
                && null != getSource().getContent()
                && null != getSource().getContent().getText()) {
            ret = this.getSource().getContent().getText();
        }
        return ret;
    }

}
