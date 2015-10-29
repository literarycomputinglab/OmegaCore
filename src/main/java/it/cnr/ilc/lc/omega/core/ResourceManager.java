package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.ResourceManagerSPI;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Source;
import java.net.URI;
import java.util.Collection;
import javax.activation.MimeType;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManager.class)
public final class ResourceManager {

    public enum CreateAction {

        SOURCE, CONTENT, FOLDER, ANNOTATION
    }

    public enum UpdateAction {

        SOURCE, CONTENT, FOLDER, ANNOTATION
    }

    @Parts(value = ResourceManagerSPI.class)
    private Collection<ResourceManagerSPI> managers;

//    @Part(configPath = "text/plain")
//    private ResourceManagerSPI textmanager;
    public void createSource(final URI uri, final MimeType mimeType) throws ManagerAction.ActionException {

        new ManagerAction() {

            @Override
            protected <T> T action() {
                System.err.println("createSource: (" + uri + ", " + mimeType.getBaseType() + ")");

//                textmanager.create(CreateAction.SOURCE, uri);
                for (ResourceManagerSPI manager : managers) {

                    if (manager.getMimeType().getBaseType().equals(mimeType.getBaseType())) {
                        manager.create(CreateAction.SOURCE, uri);
                        return (T) new Boolean(true);
                    }
                }
                return (T) new Boolean(false);
            }
        }.doAction();

        //TODO: se il metodo non trova un manager appropriato non crea il source e quindi solleva un eccezione
    }

    public void createSourceContent(final Source<? extends Content> source) throws ManagerAction.ActionException {
        // System.err.println("createSourceContent: (" + source.toString()+ ")");
        new ManagerAction() { // FIXME attenzione questa soluzione presenta un problema con la session.getTeransaction().commit() - capire!!

            @Override
            protected Boolean action() {
                System.err.println("CREATESOURCECONTENT: (" + source.toString() + ")");
                for (ResourceManagerSPI manager : managers) {
                    System.err.println("NEL FOR: (" + manager.toString() + ")");
                    //if (manager.getMimeType().getBaseType().equals(source.getContent().getMimetype())) {
                    if (manager instanceof ResourceManagerText) {
                        manager.create(source);
                        System.err.println("PRIMA DEL RETURN: (" + manager.toString() + ")");

                        return true;
                    }
                }

                return false;
            }
        ;
    }

    .doAction();
   }

    public void setContent(URI sourceUri, URI contentUri) {
        System.err.println("setContent: (" + sourceUri + ", " + contentUri + ")");
        for (ResourceManagerSPI manager : managers) {
            manager.create(CreateAction.CONTENT, contentUri);
            manager.update(UpdateAction.CONTENT, sourceUri, contentUri);
            return;

        }
    }

    public void inFolder(String name, URI sourceURI) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        for (ResourceManagerSPI manager : managers) {
            manager.create(CreateAction.FOLDER, URI.create(name));
            manager.update(UpdateAction.FOLDER, URI.create(name), sourceURI);
            return;

        }
    }

    public <T extends Content, E extends Annotation.Extension> Annotation<T, E>
            createAnnotation(final Class<E> clazz, final AnnotationBuilder<E> builder)
            throws ManagerAction.ActionException {
        // TODO gestire opporutnamente le eccezioni

        return new ManagerAction() {

            @Override
            protected Annotation<T, E> action() {
                for (ResourceManagerSPI manager : managers) {
                    manager.register(clazz.getSimpleName(), clazz);
                    Annotation<T, E> annotation = manager.create(clazz.getSimpleName(), builder);
                    return annotation;
                }
                return null;
            }
        ;

    }


.doAction();
        
 // non dovrebbe mai arrivare qui

    }

}
