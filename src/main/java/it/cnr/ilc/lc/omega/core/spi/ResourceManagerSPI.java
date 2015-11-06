package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import java.net.URI;
import javax.activation.MimeType;

/**
 *
 * @author oakgen
 */
public interface ResourceManagerSPI {

    public MimeType getMimeType();

    public void register(String type, Class<? extends Annotation.Type> clazz);

    public void create(ResourceManager.CreateAction createAction, URI uri); // considerare anche la creazione di una annotazione attraverso questa funzione? Problema per il passaggio del builder?

    public void create(Source<? extends Content> source); // considerare una create(T resource) e anche una create(Object o);

    public <T extends Content, E extends Annotation.Type> Annotation<T, E> create(String type, AnnotationBuilder<E> builder); // creazione di una annotazione

    public void update(ResourceManager.UpdateAction updateAction, URI sourceUri, URI targetUri);

    public <T extends SuperNode> void save(T resource);

}
