package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.clavius.catalog.entity.Folder;
import it.cnr.ilc.lc.omega.core.spi.DocumentManagerSPI;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
@Register(classes = DocumentManagerSPI.class)
public class DocumentManagerText implements DocumentManagerSPI {

    private final MimeType mimeType;

    public DocumentManagerText() throws MimeTypeParseException {
        this.mimeType = new MimeType("text/plain");
    }

    @Override
    public MimeType getMimeType() {
        return mimeType;
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
            Logger.getLogger(DocumentManagerText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocumentManagerText.class.getName()).log(Level.SEVERE, null, ex);
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

}
