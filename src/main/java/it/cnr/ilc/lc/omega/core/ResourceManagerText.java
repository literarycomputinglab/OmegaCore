package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.persistence.Neo4jSessionFactory;
import it.cnr.ilc.lc.omega.clavius.catalog.entity.Folder;
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
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManagerSPI.class, name = "text")
public class ResourceManagerText implements ResourceManagerSPI {

    private final MimeType mimeType;

    public ResourceManagerText() throws MimeTypeParseException {
        this.mimeType = new MimeType("text/plain");
    }

    @Override
    public MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public void register(String type, Class<? extends Annotation.Type> clazz) {
        Annotation.register(type, clazz);
    }

    @Override
    public <T extends SuperNode> T create(ResourceManager.CreateAction createAction, URI uri) throws InvalidURIException {
        switch (createAction) {
            case SOURCE:
                return (T) createSource(uri);
            case CONTENT:
                return (T) createContent(uri);
            case FOLDER:
                return (T) createFolder(uri);
            case LOCUS:
                return (T) createLocus(uri);
            default:
                throw new UnsupportedOperationException(createAction.name() + " unsupported");
        }
    }

    /*
     @Override
     public void create(Source<? extends Content> source) {
        
     Session session = Neo4jSessionFactory.getNeo4jSession();
     try {
     System.err.println("NELL CREATE PRIMA DELLA SESSION");

     session.beginTransaction(); //FIXME da spostare nel metodo chiamante, ma al momento non funziona le commit all'uscita
     System.err.println("NELL CREATE DOPO LA SESSION");
     session.save(source);
     System.err.println("NELL CREATE DOPO LA SALVA");
     session.getTransaction().commit();
     System.err.println("NELL CREATE DOPO LA COMMIT");
     } catch (Exception e) {
     System.err.println("NEL CATCH DELLA CREATE PER LA COMMIT");
     try {
     session.getTransaction().rollback();
     } catch (Exception ex) {
     ex.printStackTrace();
     }
     }
     }
     */
    @Override
    public void update(ResourceManager.UpdateAction updateAction, URI sourceUri, URI targetUri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //create(ResourceManager.CreateAction.CONTENT, targetUri); // controllare se esiste già il content
        switch (updateAction) {
            case CONTENT:
                updateContent(sourceUri, targetUri);
                break;
            case FOLDER:
                updateFolder(sourceUri, targetUri);
                break;

            default:
                throw new UnsupportedOperationException(updateAction.name() + " unsupported");
        }
    }

    private Source<TextContent> createSource(URI uri) throws InvalidURIException {
        // controllare che la risorsa non sia già presente con lo stesso URI
        Source<TextContent> source = Source.sourceOf(TextContent.class);
        source.setUri(uri.toASCIIString());
        System.err.println("source uri: " + uri.toASCIIString());
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
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResourceManagerText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | IllegalArgumentException ex) {
            Logger.getLogger(ResourceManagerText.class.getName()).log(Level.WARNING, null, ex);
            //LA RISORSA NON E' REMOTA
            content.setText("");
        }

        return content;
    }

    private void updateContent(URI sourceUri, URI targetUri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Session session = Neo4jSessionFactory.getNeo4jSession();
        session.beginTransaction();
        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", sourceUri.toASCIIString())).iterator().next();
        source.setContent(session.loadAll(TextContent.class, new Filter("uri", targetUri.toASCIIString())).iterator().next());
        session.save(source);
        session.getTransaction().commit();
    }

    /*
     private Folder createFolder(URI uri) {
     //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     Session session = Neo4jSessionFactory.getNeo4jSession();
     session.beginTransaction();
     if (!session.loadAll(Folder.class, new Filter("name", uri.toASCIIString())).iterator().hasNext()) {
     Folder folder = new Folder();
     folder.setName(uri.toASCIIString());
     session.save(folder);
     }
     session.getTransaction().commit();
     } */
    private Folder createFolder(URI uri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Folder folder = new Folder();
        folder.setName(uri.toASCIIString());
        return folder;
    }

    private void updateFolder(URI sourceUri, URI targetUri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Session session = Neo4jSessionFactory.getNeo4jSession();
        session.beginTransaction();
        Folder folder = session.loadAll(Folder.class, new Filter("name", sourceUri.toASCIIString())).iterator().next();
        /*TODO controllare se URI target e' un Source oppure un folder. Se è un folder va chiamato il metodo folder.addFolder*/
        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", targetUri.toASCIIString())).iterator().next();
        folder.addFile(source);
        session.save(folder);
        session.getTransaction().commit();
    }

    @Override
    public <T extends Content, E extends Annotation.Type> Annotation<T, E>
            create(String type, AnnotationBuilder<E> builder) throws InvalidURIException {

        Annotation<T, E> annotation = Annotation.newAnnotation(type, builder);

        return annotation;
    }

    @Override
    public <T extends SuperNode> void save(T resource) {

        Session session = Neo4jSessionFactory.getNeo4jSession();
        session.save(resource);

    }

    private TextLocus createLocus(URI uri) throws InvalidURIException {

        TextLocus locus = Locus.locusOf(TextLocus.class, Locus.PointsTo.CONTENT);
        locus.setUri(uri.toASCIIString());
        return locus;

    }

    @Override
    public <T extends SuperNode> T load(URI uri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(ResourceManager.UpdateAction updateAction, URI resourceUri, ResourceStatus status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends SuperNode> T update(ResourceManager.UpdateAction updateAction, T resource, ResourceStatus status) {
        switch (updateAction) {
            case LOCUS:
                return (T) updateTextLocus((TextLocus) resource, status); // controllare i cast per i tipi parmetrici
            case CONTENT:
                return (T) updateTextContent((TextContent) resource, status);
            default:
                throw new UnsupportedOperationException(updateAction.name() + " unsupported");
        }
    }

    private TextLocus updateTextLocus(TextLocus locus, ResourceStatus status) {
        locus.setSource(status.getSource());
        locus.setStart(status.getStart());
        locus.setEnd(status.getEnd());
        locus.setAnnotation(status.getAnnotation());
        //System.err.println("******** uri " + locus.getUri());
        return locus;
    }

    private TextContent updateTextContent(TextContent content, ResourceStatus status) {

        content.setText(status.getText());
        return content;
    }

}
