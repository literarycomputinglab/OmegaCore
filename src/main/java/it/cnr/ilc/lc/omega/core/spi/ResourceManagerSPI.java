package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.core.ResourceManager;
import it.cnr.ilc.lc.omega.core.ResourceStatus;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import java.net.URI;
import javax.activation.MimeType;

/**
 *
 * @author oakgen
 */
public interface ResourceManagerSPI {

    /*
     * cambiata la semantica della create:
     * CREATE istanzia un oggetto in memoria (no persistenza nel db)
     * SAVE salva (persiste) un oggetto (in memoria) nel database.
     */
    public MimeType getMimeType();

    public void register(String type, Class<? extends Annotation.Data> clazz);

    /* CREATE */
    public <T extends SuperNode> T
            create(ResourceManager.CreateAction createAction, URI uri) throws InvalidURIException; // considerare anche la creazione di una annotazione attraverso questa funzione? Problema per il passaggio del builder?
    //FIXME: refactoring del metodo come SAVE

    /*public <T extends SuperNode> T
            create(Source<? extends Content> source) throws InvalidURIException;  //considerare una create(T resource) e anche una create(Object o);
     */
    public <T extends Content, E extends Annotation.Data> Annotation<T, E>
            create(String type, AnnotationBuilder<E> builder) throws InvalidURIException; // creazione di una annotazione

    /* UPDATE */
    public void update(ResourceManager.UpdateAction updateAction, URI sourceUri, URI targetUri); // per l'aggiornamento del file system

    public void update(ResourceManager.UpdateAction updateAction, URI resourceUri, ResourceStatus status); // per aggiornare una risorsa bisogna passare la risorsa e i parametri con la quale aggiornarla

    public <T extends SuperNode> T
            update(ResourceManager.UpdateAction updateAction, T resource, ResourceStatus status); // update con risorsa da aggiornare e restituisce la risorsa aggiornata

    /* DATABASE */
    public <T extends SuperNode> void save(T resource);

    public <T extends SuperNode> T load(URI uri, Class<T> clazz);

}
