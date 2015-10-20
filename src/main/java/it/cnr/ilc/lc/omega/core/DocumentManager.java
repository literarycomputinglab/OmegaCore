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

        SOURCE
    }

    @Parts(value = DocumentManagerSPI.class)
    private Collection<DocumentManagerSPI> managers;

    public void createSource(URI uri, MimeType mimeType) {
        for (DocumentManagerSPI manager : managers) {
            if (manager.getMimeType().equals(mimeType)) {
                manager.create(CreateAction.SOURCE, uri);
                return;
            }
        }
    }

}
