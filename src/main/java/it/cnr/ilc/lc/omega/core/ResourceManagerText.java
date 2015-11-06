package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.persistence.Neo4jSessionFactory;
import it.cnr.ilc.lc.omega.clavius.catalog.entity.Folder;
import it.cnr.ilc.lc.omega.core.spi.ResourceManagerSPI;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
@Register(classes = ResourceManagerSPI.class, name = "claviustext")
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
    public void create(ResourceManager.CreateAction createAction, URI uri) {
        switch (createAction) {
            case SOURCE:
                createSource(uri);
                break;
            case CONTENT:
                createContent(uri);
                break;
            case FOLDER:
                createFolder(uri);
                break;
            default:
                throw new UnsupportedOperationException(createAction.name() + " unsupported");
        }
    }

    @Override
    public void create(Source<? extends Content> source) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    private void createSource(URI uri) {
        // controllare che la risorsa non sia già presente con lo stesso URI
        Session session = Neo4jSessionFactory.getNeo4jSession();
        Source<TextContent> source = Source.sourceOf(TextContent.class);
        source.setUri(uri.toASCIIString());
        session.save(source);
        System.err.println("source uri: " + uri.toASCIIString());
    }

    private void createContent(URI uri) {
        Session session = Neo4jSessionFactory.getNeo4jSession();
        TextContent content = Content.contentOf(TextContent.class); // non mi piace il tipo così specifico

        try {
            //controllare che la risorsa non sia già presente con lo stesso URI
            session.beginTransaction();
            URLConnection site = uri.toURL().openConnection();
            content.setUri(uri.toASCIIString());
            content.setText(new Scanner(new BufferedInputStream(site.getInputStream()), "UTF-8").useDelimiter(Pattern.compile("\\A")).next());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResourceManagerText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResourceManagerText.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            session.save(content);
            session.getTransaction().commit();
        }

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

    private void createFolder(URI uri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Session session = Neo4jSessionFactory.getNeo4jSession();
        session.beginTransaction();
        if (!session.loadAll(Folder.class, new Filter("name", uri.toASCIIString())).iterator().hasNext()) {
            Folder folder = new Folder();
            folder.setName(uri.toASCIIString());
            session.save(folder);
        }
        session.getTransaction().commit();
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
    public <T extends Content, E extends Annotation.Type> Annotation<T, E> create(String type, AnnotationBuilder<E> builder) {

        Annotation<T, E> annotation = Annotation.newAnnotation(type, builder);
        return annotation;
    }

    @Override
    public <T extends SuperNode> void save(T resource) {

        Session session = Neo4jSessionFactory.getNeo4jSession();
        session.save(resource);

    }

}
