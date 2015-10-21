package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.core.ResourceManager;
import java.net.URI;
import javax.activation.MimeType;

/**
 *
 * @author oakgen
 */
public interface DocumentManagerSPI {

    public MimeType getMimeType();
    
    public void create(ResourceManager.CreateAction createAction, URI uri);
    public void update(ResourceManager.UpdateAction updateAction, URI sourceUri, URI targetUri);
    
}
