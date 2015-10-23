package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import java.net.URI;
import javax.activation.MimeType;

/**
 *
 * @author oakgen
 */
public interface DocumentManagerSPI {

    public MimeType getMimeType();
    
    public void create(ResourceManager.CreateAction createAction, URI uri);
    public void create(Source<? extends Content> source); // considerare una create(T resource) e anche una create(Object o);
    
    
    public void update(ResourceManager.UpdateAction updateAction, URI sourceUri, URI targetUri);
    
}
