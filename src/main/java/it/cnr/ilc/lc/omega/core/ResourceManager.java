package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.DocumentManagerSPI;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import java.net.URI;
import java.util.Collection;
import javafx.scene.text.Text;
import javax.activation.MimeType;
import org.neo4j.ogm.session.Session;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManager.class)
public final class ResourceManager {

    public enum CreateAction {

        SOURCE, CONTENT, FOLDER
    }

    public enum UpdateAction {

        SOURCE, CONTENT, FOLDER
    }

    @Parts(value = DocumentManagerSPI.class)
    private Collection<DocumentManagerSPI> managers;

//    @Part(configPath = "text/plain")
//    private DocumentManagerSPI textmanager;
    public void createSource(final URI uri, final MimeType mimeType) throws ManagerAction.ActionException {

        new ManagerAction() {

            @Override
            protected void action() {
                System.err.println("createSource: (" + uri + ", " + mimeType.getBaseType() + ")");

//                textmanager.create(CreateAction.SOURCE, uri);
                for (DocumentManagerSPI manager : managers) {

                    if (manager.getMimeType().getBaseType().equals(mimeType.getBaseType())) {
                        manager.create(CreateAction.SOURCE, uri);
                        return;
                    }
                }
            }
        }.doAction();

        //TODO: se il metodo non trova un manager appropriato non crea il source e quindi solleva un eccezione
    }

    public void createSourceContent(final Source<? extends Content> source) throws ManagerAction.ActionException {
       // System.err.println("createSourceContent: (" + source.toString()+ ")");
        new ManagerAction() { // FIXME attenzione questa soluzione presenta un problema con la session.getTeransaction().commit() - capire!!

            @Override
            protected void action() {
                System.err.println("CREATESOURCECONTENT: (" + source.toString()+ ")");
                for (DocumentManagerSPI manager : managers) {
                    System.err.println("NEL FOR: (" + manager.toString()+ ")");
                    //if (manager.getMimeType().getBaseType().equals(source.getContent().getMimetype())) {
                    if(manager instanceof DocumentManagerText){
                        manager.create(source);
                         System.err.println("PRIMA DEL RETURN: (" + manager.toString()+ ")");
                        
                        return;
                    }
                }
            };
        }.doAction();
    }

    public void setContent(URI sourceUri, URI contentURI) {
        System.err.println("setContent: (" + sourceUri + ", " + contentURI + ")");
        for (DocumentManagerSPI manager : managers) {
            manager.create(CreateAction.CONTENT, contentURI);
            manager.update(UpdateAction.CONTENT, sourceUri, contentURI);
            return;

        }
    }

    public void inFolder(String name, URI sourceURI) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        for (DocumentManagerSPI manager : managers) {
            manager.create(CreateAction.FOLDER, URI.create(name));
            manager.update(UpdateAction.FOLDER, URI.create(name), sourceURI);
            return;

        }
    }

}
