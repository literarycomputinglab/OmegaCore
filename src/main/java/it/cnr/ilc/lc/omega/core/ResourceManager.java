package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.ResourceManagerSPI;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Locus;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.entity.TextLocus;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import java.net.URI;
import java.util.Collection;
import javax.activation.MimeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManager.class)
public final class ResourceManager {

    private static final Logger log = LogManager.getLogger(ResourceManager.class);

    public enum CreateAction {

        SOURCE, CONTENT, FOLDER, ANNOTATION, LOCUS
    }

    public enum UpdateAction {

        SOURCE, CONTENT, FOLDER, ANNOTATION, LOCUS
    }

    public enum OmegaMimeType {

        PLAIN("text/plain"),
        PNG("image/png");

        private final String type;

        private OmegaMimeType(String s) {
            this.type = s;
        }

        private String getType() {
            return type;

        }

        @Override
        public String toString() {
            return getType();
        }

    }

    @Parts(value = ResourceManagerSPI.class)
    private Collection<ResourceManagerSPI> managers;

//    @Part(configPath = "text/plain")
//    private ResourceManagerSPI textmanager;
    public <T extends Content> Source<T> createSource(final URI uri, final MimeType mimeType) throws ManagerAction.ActionException {

        return new ManagerAction() {

            @Override
            protected Source<T> action() throws ActionException {
                log.info("createSource: (" + uri + ", " + mimeType.getBaseType() + ")");
                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(mimeType.getBaseType())) {
                        try {
                            Source<T> source = manager.create(CreateAction.SOURCE, uri);
                            return source;
                        } catch (InvalidURIException ex) {
                            log.error("creating source", ex);
                            throw new ManagerAction.ActionException(ex);
                        }

                    }
                }
                log.warn("Unable to create Source!");
                return null;
            }
        }.doAction();

        //TODO: se il metodo non trova un manager appropriato non crea il source e quindi solleva un eccezione
    }

    public <T extends Content> T createSourceContent(final Source<T> source) throws ManagerAction.ActionException, InvalidURIException {
        // System.err.println("createSourceContent: (" + source.toString()+ ")");
        return new ManagerAction() { // FIXME attenzione questa soluzione presenta un problema con la session.getTeransaction().commit() - capire!!

            @Override
            protected T action() throws ActionException {
                log.info("action(): (" + source.toString() + ")");
                T content = null;
                for (ResourceManagerSPI manager : managers) {
                    log.info("manager" + manager.toString() + ")");
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) {
                        //  if (manager instanceof ResourceManagerText) {
                        try {
                            content = manager.create(CreateAction.CONTENT,
                                    URI.create(source.getUri()));

                        } catch (InvalidURIException ex) {
                            log.error("creating source content", ex);
                            throw new ActionException(ex);
                        }
                        return content;
                    }
                }
                log.error("No suitable manager for the mimetype " + source.getContent().getMimetype());
                throw new ActionException(new Exception("No suitable manager for the mimetype " + source.getContent().getMimetype()));
            }
        }.doAction();
    }

    public void setContent(URI sourceUri, URI contentUri) throws InvalidURIException {
        log.info("setContent: sourceUri=(" + sourceUri + "), contentUri=(" + contentUri + ")");
        for (ResourceManagerSPI manager : managers) {
            manager.create(CreateAction.CONTENT, contentUri);
            manager.update(UpdateAction.CONTENT, sourceUri, contentUri);
            return;

        }
    }

    public void inFolder(String name, URI sourceURI) throws InvalidURIException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        log.info("inFolder: name=(" + name + "), sourceUri=(" + sourceURI + ")");

        for (ResourceManagerSPI manager : managers) {
            manager.create(CreateAction.FOLDER, URI.create(name));
            manager.update(UpdateAction.FOLDER, URI.create(name), sourceURI);
            return;

        }
    }

    public <T extends Content, E extends Annotation.Data> void
            saveAnnotation(final Annotation<T, E> annotation) throws ManagerAction.ActionException {

        new ManagerAction() {

            @Override
            protected Boolean action() {
                for (ResourceManagerSPI manager : managers) {
                    manager.save(annotation);
                    return true;
                }
                return false;
            }

        }.doAction();

    }

    public <T extends Content> void saveSource(final Source<T> source) throws ManagerAction.ActionException {

        new ManagerAction() {

            @Override
            protected Boolean action() {
                for (ResourceManagerSPI manager : managers) {
                    manager.save(source);
                    return true;
                }
                return false;
            }

        }.doAction();

    }

    public <T extends Content> Source<T> loadSource(final URI uri, Class<T> clazz) throws ManagerAction.ActionException {

        return new ManagerAction() {

            @Override
            protected Source<T> action() throws ManagerAction.ActionException {
                for (ResourceManagerSPI manager : managers) {
                    return manager.load(uri, Source.class );
                }
                log.error("Unable to load resource at uri " + uri);
                throw new ActionException(new Exception("Unable to load resource at uri " + uri));
            }

        }.doAction();

    }

    //FIXME: Da generalizzare?
    /*public <T extends Content, E extends Annotation.Data> void
     updateTextAnnotationLocus(Source<T> source, Annotation<T, E> annotation, WKT? ) {*/
    public <E extends Annotation.Data> void
            updateTextAnnotationLocus(final Source<TextContent> source,
                    final Annotation<TextContent, E> annotation,
                    final int start, final int end) throws ManagerAction.ActionException {

        //PRENDERE UN MANAGER
        //FARE CREATE DEL LOCUS CON QUEL MANAGER
        //POPOLARE IL LOCUS
        //AGGANCIARE LOCUS ALLA ANNOTAZIONE
        new ManagerAction() {

            @Override
            protected <T> T action() throws ActionException {
                TextLocus locus;
                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) {
                        try {
                            locus = manager.create(CreateAction.LOCUS,
                                    URI.create("/uri/annotation/locus/" + String.valueOf(System.currentTimeMillis())));
                            // locus.setStart(start); //FIXME i metodi per aggiornare il locus devono essere fatti nel manager
                            // locus.setEnd(end);
                            // locus.setSource(source);
                            // locus.setAnnotation(annotation);
                        } catch (InvalidURIException ex) {
                            log.error("creating a URI ", ex);
                            throw new ActionException(ex);
                        }

                        locus = manager.update(UpdateAction.LOCUS, locus,
                                new ResourceStatus<>() // controllare i tipi generici
                                .clazz(locus.getClass())
                                .source(source)
                                .start(start)
                                .end(end)
                                .annotation(annotation)
                        );
                        log.info("+++ uri " + locus.getUri());
                        manager.update(UpdateAction.ANNOTATION,
                                annotation,
                                new ResourceStatus<>().textLocus(locus));
                    }
                }

                return null;

            }

        }.doAction();

    }

    public TextContent updateTextContent(final TextContent content, final String text) throws ManagerAction.ActionException {
        return new ManagerAction() {

            @Override
            protected TextContent action() throws ManagerAction.ActionException {
                log.info("createAnnotation() start");
                TextContent ret = content;

                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) {
                        ret = manager.update(UpdateAction.CONTENT, content, new ResourceStatus().text(text));
                    }
                }
                return ret;

            }
        }.doAction();
    }

    public <T extends Content, E extends Annotation.Data> Annotation<T, E>
            createAnnotation(final Class<E> clazz,
                    final AnnotationBuilder<E> builder)
            throws ManagerAction.ActionException {
        // TODO gestire opporutnamente le eccezioni

        return new ManagerAction() {

            @Override
            protected Annotation<T, E> action() throws ActionException {
                log.info("createAnnotation() start");

                for (ResourceManagerSPI manager : managers) {
                    manager.register(clazz.getSimpleName(), clazz);
                    Annotation<T, E> annotation;
                    try {
                        annotation = manager.create(clazz.getSimpleName(), builder);
                    } catch (InvalidURIException ex) {
                        log.error("creating annotation for clazz " + clazz.getSimpleName(), ex);
                            throw new ActionException(ex);
                    }
                    log.info("createAnnotation() end");
///////////////METTERE LA URI !!!!!!!!!!!!!
                    return annotation;
                }
                log.warn("createAnnotation() end null");

                return null;
            }
        ;
    }

    .doAction();
    }
         
    public <T extends Content, V extends Content, L extends Locus<V>> L
            createLocus(final Source<T> source, final int start, final int end, Class<V> contentClazz) throws InvalidURIException, ManagerAction.ActionException {
        return new ManagerAction() {
            L locus;

            @Override
            protected L action() throws ManagerAction.ActionException {

                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) { //WARN l'IF dipende da T e non dal tipo del manager
                        try {
                            locus = manager.create(CreateAction.LOCUS,
                                    URI.create("/resourcemanager/createLocus/action/locus/" + System.currentTimeMillis())); //FIXME: da metteer in Utils la creazione delle uri
                            manager.update(UpdateAction.LOCUS, locus,
                                    new ResourceStatus<T, Annotation.Data, V>()
                                    .start(start)
                                    .end(end)
                                    .source(source)
                                    .pointsTo(Locus.PointsTo.CONTENT));
                            return locus;
                        } catch (InvalidURIException ex) {
                            log.error("creating a URI ", ex);
                            throw new ManagerAction.ActionException(new Exception("AAAAAAAAAAHHHHHHHH BUG!!!"));
                        }
                    }
                }
                log.error("No suitable manager for Locus<TextContent>");

                throw new ManagerAction.ActionException(new Exception("No suitable manager for Locus<TextContent>"));
            }
        }.doAction();
    }

    /**
     *
     * @param <T> Tipo del contenuto puntato dal Locus (ovvero Content)
     * @param <V> Tipo del contenuto della annotazione che punta Content tramite
     * il locus
     * @param <E> Tipo della annotazione
     * @param locus
     * @param annotation
     * @param textContentClazz
     * @throws it.cnr.ilc.lc.omega.core.ManagerAction.ActionException
     */
    //RISOLVERE IL PROBLEMA DEL PASSAGGIO DEL LOCUS: NON SI PUO' PASSARE UN TEXTLOCUS O IMAGELOCUS
    //PERCHE' ESTENDONO DA UNA CLASSSE DI BASE DI TIPO PARAMETRICO DIVERSO (DA IMAGE CONTENT E DA TEXT CONTENT)
    public <T extends Content, E extends Annotation.Data, V extends Content> void
            updateAnnotationLocus(final Locus<V> locus,
                    final Annotation<T, E> annotation,
                    final Class<V> textContentClazz) throws ManagerAction.ActionException {
        new ManagerAction() {

            @Override
            protected Boolean action() throws ManagerAction.ActionException {

                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) {
                        manager.update(UpdateAction.LOCUS, locus,
                                new ResourceStatus<T, E, V>().annotation(annotation));
                        manager.update(UpdateAction.ANNOTATION, annotation,
                                new ResourceStatus<T, E, V>().textLocus((Locus<TextContent>) locus));
                        return true;
                    }
                }
                log.error("No suitable manager for Locus<TextContent>");
                throw new ManagerAction.ActionException(new Exception("No suitable manager for Locus<TextContent>"));
            }

        }.doAction();
    }

}
