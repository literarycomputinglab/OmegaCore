package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.ResourceManagerSPI;
import it.cnr.ilc.lc.omega.core.util.Utils;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Locus;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.entity.TextLocus;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import it.cnr.ilc.lc.omega.persistence.PersistenceHandler;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManagerSPI.class, name = "text")
public class ResourceManagerText implements ResourceManagerSPI {

    private static Logger log = LogManager.getLogger(ManagerAction.class);

    private final MimeType mimeType;

    @Part
    private static PersistenceHandler persistence;

    public ResourceManagerText() throws MimeTypeParseException {
        this.mimeType = new MimeType("text/plain");
    }

    @Override
    public MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public void register(String type, Class<? extends Annotation.Data> clazz) {
        Annotation.register(type, clazz);
    }

    @Override
    public <T extends SuperNode> T create(ResourceManager.CreateAction createAction, URI uri) throws InvalidURIException {
        switch (createAction) {
            case SOURCE:
                return (T) createSource(uri);
            case CONTENT:
                return (T) createContent(uri);
//            case FOLDER:
//                return (T) createFolder(uri);
            case LOCUS:
                return (T) createLocus(uri);
            default:
                throw new UnsupportedOperationException(createAction.name() + " unsupported");
        }
    }

    @Override
    public void update(ResourceManager.UpdateAction updateAction, URI sourceUri, URI targetUri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //create(ResourceManager.CreateAction.CONTENT, targetUri); // controllare se esiste già il content
        switch (updateAction) {
            case CONTENT:
                updateContent(sourceUri, targetUri);
                break;
//            case FOLDER:
//                updateFolder(sourceUri, targetUri);
//                break;

            default:
                throw new UnsupportedOperationException(updateAction.name() + " unsupported");
        }
    }

    private Source<TextContent> createSource(URI uri) throws InvalidURIException {
        // controllare che la risorsa non sia già presente con lo stesso URI
        Source<TextContent> source = Source.sourceOf(TextContent.class, uri);
        source.setUri(uri.toASCIIString());
        log.info("source uri: " + uri.toASCIIString());
        return source;
    }

    private TextContent createContent(URI uri) throws InvalidURIException {

        TextContent content = Content.contentOf(TextContent.class); // non mi piace il tipo così specifico

        /*
         * la uri in input non e' modificata per poter caricare il contenuto della source (puntata da uri)
         * la uri, che identifica il content, e' costruita con un protocollo specifico (per il momento
         * tramite concatenazione con "/content/" + System.currentTimeMillis()
         */
        content.setUri(Utils.appendContentID(uri));

        try {
            //controllare che la risorsa non sia già presente con lo stesso URI
            URLConnection site = uri.toURL().openConnection(); //PER CARICARE RISORSA REMOTA
            content.setText(new Scanner(new BufferedInputStream(site.getInputStream()), "UTF-8").useDelimiter(Pattern.compile("\\A")).next());

        } catch (IOException e) {
            log.error("Opening connection", e);
        } catch (IllegalArgumentException ex) {
            //Logger.getLogger(ResourceManagerText.class.getName()).log(Level.WARNING, "Invalid URI: " + uri.toASCIIString(), new IllegalArgumentException("Invalid URI: " + uri.toASCIIString()));
            log.warn("Invalid URI " + uri.toASCIIString() + ", " + ex.getLocalizedMessage());
            //LA RISORSA NON E' REMOTA
            content.setText("");
        }
        return content;
    }

    private void updateContent(URI sourceUri, URI targetUri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        session.beginTransaction();
//        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", sourceUri.toASCIIString())).iterator().next();
//        source.setContent(session.loadAll(TextContent.class, new Filter("uri", targetUri.toASCIIString())).iterator().next());
//        session.save(source);
//        session.getTransaction().commit();
        throw new UnsupportedOperationException("to be implemented");

    }

    // CLAVIUS
//    private Folder createFolder(URI uri) {
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Folder folder = new Folder();
//        folder.setName(uri.toASCIIString());
//        return folder;
//    }
//
//    private void updateFolder(URI sourceUri, URI targetUri) {
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        session.beginTransaction();
//        Folder folder = session.loadAll(Folder.class, new Filter("name", sourceUri.toASCIIString())).iterator().next();
//        /*TODO controllare se URI target e' un Source oppure un folder. Se è un folder va chiamato il metodo folder.addFolder*/
//        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", targetUri.toASCIIString())).iterator().next();
//        folder.addFile(source);
//        session.save(folder);
//        session.getTransaction().commit();
//    }
    @Override
    public <T extends Content, E extends Annotation.Data> Annotation<T, E>
            create(String type, AnnotationBuilder<E> builder) throws InvalidURIException {

        Annotation<T, E> annotation = Annotation.newAnnotation(type, builder);

        return annotation;
    }

    @Override
    public <T extends SuperNode> void save(T resource) {

//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        session.save(resource);
        log.info("persist resource, is the transaction active? " + persistence.getEntityManager().getTransaction().isActive());
        try {

            persistence.getEntityManager().persist(resource);
            log.debug("resource persisted");
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.error("in persist resource " + e);
        }
        // throw new UnsupportedOperationException("to be implemented");

    }

    private TextLocus createLocus(URI uri) throws InvalidURIException {

        TextLocus locus = Locus.locusOf(TextLocus.class, uri, Locus.PointsTo.CONTENT);
        locus.setUri(uri.toASCIIString());
        return locus;

    }

    @Override
    public <T extends SuperNode> T load(URI uri, Class<T> clazz) {
//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", uri.toASCIIString())).iterator().next();
//
//        return (T) source;
        EntityManager em = persistence.getEntityManager();
        log.info("load resource, is the transaction active? " + em.getTransaction().isActive());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.where(cb.equal(root.get("uri"), uri.toASCIIString()));

        TypedQuery<T> q = em.createQuery(cq);
        T result = q.getSingleResult();

        //System.err.println("result " + result);
//        loadAll(Source.sourceOf(TextContent.class, URI.create("")).getClass());
        return result;
    }

    @Override
    public <T extends SuperNode> List<T> loadAll(Class<T> clazz) {

        log.info("Loading all, clazz is " + clazz);
        if (!clazz.isInstance(Source.sourceOf(TextContent.class, URI.create("dummyURI")))) {
            throw new IllegalArgumentException("Aspected TextContent as resource type");
        }

        return (List<T>) loadAllTextContent((Source.sourceOf(TextContent.class, URI.create("dummyURI"))).getClass());
    }

    private <T extends Source<TextContent>> List<T> loadAllTextContent(Class<T> clazz) {
        EntityManager em = persistence.getEntityManager();
        log.info("Creating local criteria query for load all text content ");

        //Query q =  em.createQuery("Select s From Source s");
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> c = cq.from(clazz);
        cq.select(c);
        TypedQuery<T> q = em.createQuery(cq);

        //System.err.println("result " + result);
        List<T> los = q.getResultList();
        log.warn("resultset lenght " + los.size());
        return (List<T>) los;
    }

    @Override
    public void update(ResourceManager.UpdateAction updateAction, URI resourceUri, ResourceStatus status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends SuperNode> T update(ResourceManager.UpdateAction updateAction, T resource, ResourceStatus status) {
        switch (updateAction) {
            case ANNOTATION:
                return (T) updateTextAnnotation((Annotation<TextContent, Annotation.Data>) resource, status); // controllare i cast per i tipi parmetrici
            case LOCUS:
                return (T) updateTextLocus((TextLocus) resource, status); // controllare i cast per i tipi parmetrici
            case CONTENT:
                return (T) updateTextContent((TextContent) resource, status);
            default:
                throw new UnsupportedOperationException(updateAction.name() + " unsupported");
        }
    }

    private <E extends Annotation.Data> Annotation<TextContent, E>
            updateTextAnnotation(Annotation<TextContent, E> annotation,
                    ResourceStatus<TextContent, E, TextContent> status) {

        if (status.getTextLocus().isPresent()) {
            annotation.addLocus(status.getTextLocus().get());
        }
        return annotation;
    }

    private <E extends Annotation.Data> TextLocus
            updateTextLocus(TextLocus locus, ResourceStatus<TextContent, E, TextContent> status) {

        if (status.getSource().isPresent()) {
            locus.setSource(status.getSource().get());
        }

        if (status.getStart().isPresent()) {
            locus.setStartLocus(status.getStart().getAsInt());
        }

        if (status.getEnd().isPresent()) {
            locus.setEndLocus(status.getEnd().getAsInt());
        }

        return locus;
    }

    private <E extends Annotation.Data> TextContent
            updateTextContent(TextContent content, ResourceStatus<TextContent, E, TextContent> status) {

        content.setText(status.getText().get());
        return content;
    }

}
