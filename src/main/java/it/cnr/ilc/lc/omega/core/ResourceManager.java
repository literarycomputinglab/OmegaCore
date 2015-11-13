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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManager.class)
public final class ResourceManager {

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
                System.err.println("createSource: (" + uri + ", " + mimeType.getBaseType() + ")");
                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(mimeType.getBaseType())) {
                        try {
                            Source<T> source = manager.create(CreateAction.SOURCE, uri);
                            return source;
                        } catch (InvalidURIException ex) {
                            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
                            throw new ManagerAction.ActionException(ex);
                        }

                    }
                }
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
                System.err.println("CREATESOURCECONTENT: (" + source.toString() + ")");
                T content = null;
                for (ResourceManagerSPI manager : managers) {
                    System.err.println("NEL FOR: (" + manager.toString() + ")");
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) {
                        //  if (manager instanceof ResourceManagerText) {
                        try {
                            content = manager.create(CreateAction.CONTENT,
                                    URI.create(source.getUri()));

                        } catch (InvalidURIException ex) {
                            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
                            throw new ActionException(ex);
                        }
                        System.err.println("PRIMA DEL RETURN: (" + manager.toString() + ")");

                        return content;
                    }
                }
                throw new ActionException(new Exception("No suitable manager for the mimetype " + source.getContent().getMimetype()));

            }
        ;
    }

    .doAction();
   }

    public void setContent(URI sourceUri, URI contentUri) throws InvalidURIException {
        System.err.println("setContent: (" + sourceUri + ", " + contentUri + ")");
        for (ResourceManagerSPI manager : managers) {
            manager.create(CreateAction.CONTENT, contentUri);
            manager.update(UpdateAction.CONTENT, sourceUri, contentUri);
            return;

        }
    }

    public void inFolder(String name, URI sourceURI) throws InvalidURIException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        for (ResourceManagerSPI manager : managers) {
            manager.create(CreateAction.FOLDER, URI.create(name));
            manager.update(UpdateAction.FOLDER, URI.create(name), sourceURI);
            return;

        }
    }

    public <T extends Content, E extends Annotation.Type> void saveAnnotation(final Annotation<T, E> annotation) throws ManagerAction.ActionException {

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
                    return manager.load(uri);
                }
                throw new ActionException(new Exception("Unable to load resource at uri " + uri));
            }

        }.doAction();

    }

    //FIXME: Da generalizzare?
    /*public <T extends Content, E extends Annotation.Type> void
     updateTextAnnotationLocus(Source<T> source, Annotation<T, E> annotation, WKT? ) {*/
    public <E extends Annotation.Type> void
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
                            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
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
                        System.err.println("+++ uri " + locus.getUri());
                        annotation.addLocus(locus);
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
                System.err.println("createAnnotation() start");
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

    public <T extends Content, E extends Annotation.Type> Annotation<T, E>
            createAnnotation(final Class<E> clazz,
                    final AnnotationBuilder<E> builder)
            throws ManagerAction.ActionException {
        // TODO gestire opporutnamente le eccezioni

        return new ManagerAction() {

            @Override
            protected Annotation<T, E> action() throws ActionException {
                System.err.println("createAnnotation() start");

                for (ResourceManagerSPI manager : managers) {
                    manager.register(clazz.getSimpleName(), clazz);
                    Annotation<T, E> annotation;
                    try {
                        annotation = manager.create(clazz.getSimpleName(), builder);
                    } catch (InvalidURIException ex) {
                        Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
                        throw new ActionException(ex);
                    }
                    System.err.println("createAnnotation() end");
///////////////METTERE LA URI !!!!!!!!!!!!!
                    return annotation;
                }
                System.err.println("createAnnotation() end null");

                return null;
            }
        ;
    }

    .doAction();
    }
         
    public <T extends Content, L extends Locus<T>> L createLocus(final Source<T> source, final int start, final int end) throws InvalidURIException, ManagerAction.ActionException {
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
                                    new ResourceStatus<TextContent, Annotation.Type>().start(start).end(end).source(source));
                            return locus;
                        } catch (InvalidURIException ex) {
                            Logger.getLogger(ResourceManager.class.getName()).log(Level.INFO, null, ex);
                            throw new ManagerAction.ActionException(new Exception("AAAAAAAAAAHHHHHHHH BUG!!!"));
                        }
                    }
                }
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
     * @throws it.cnr.ilc.lc.omega.core.ManagerAction.ActionException
     */
    public <T extends Content, V extends Content, E extends Annotation.Type> void
            updateAnnotationLocus(final Locus<T> locus, final Annotation<V, E> annotation) throws ManagerAction.ActionException {
        new ManagerAction() {

            @Override
            protected Boolean action() throws ManagerAction.ActionException {

                for (ResourceManagerSPI manager : managers) {
                    if (manager.getMimeType().getBaseType().equals(OmegaMimeType.PLAIN.toString())) {
                        manager.update(UpdateAction.LOCUS, locus,
                                new ResourceStatus<V, E>().annotation(annotation));
                        return true;
                    }
                }
                throw new ManagerAction.ActionException(new Exception("No suitable manager for Locus<TextContent>"));
            }

            
        }.doAction();
    }

}
