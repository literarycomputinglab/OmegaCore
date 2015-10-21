package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.core.DocumentManager;
import java.net.URI;
import javax.activation.MimeType;

/**
 *
 * @author oakgen
 */
public interface DocumentManagerSPI {

    public MimeType getMimeType();
    
    public void create(DocumentManager.CreateAction createAction, URI uri);
    public void update(DocumentManager.UpdateAction updateAction, URI sourceUri, URI targetUri);
    
}
