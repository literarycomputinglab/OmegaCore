package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.DocumentManagerSPI;
import java.net.URI;
import java.util.Collection;
import javax.activation.MimeType;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = DocumentManager.class)
public final class DocumentManager {

    
    public enum CreateAction {

        SOURCE, CONTENT, FOLDER
    }
    public enum UpdateAction {

        SOURCE, CONTENT, FOLDER
    }
    

    @Parts(value = DocumentManagerSPI.class)
    private Collection<DocumentManagerSPI> managers;

    public void createSource(URI uri, MimeType mimeType) {
        System.err.println("createSource: (" + uri + ", " + mimeType.getBaseType() + ")");
        for (DocumentManagerSPI manager : managers) {

            if (manager.getMimeType().getBaseType().equals(mimeType.getBaseType())) {

                manager.create(CreateAction.SOURCE, uri);
                return;
            }
        }
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
