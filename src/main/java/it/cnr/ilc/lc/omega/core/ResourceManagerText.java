package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.annotation.DummyAnnotation;
import it.cnr.ilc.lc.omega.annotation.DummyAnnotationBuilder;
import it.cnr.ilc.lc.omega.core.datatype.ADTAnnotation;
import it.cnr.ilc.lc.omega.core.spi.ResourceManagerSPI;
import it.cnr.ilc.lc.omega.core.util.Utils;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.AnnotationBuilder;
import it.cnr.ilc.lc.omega.entity.AnnotationRelation;
import it.cnr.ilc.lc.omega.entity.Content;
import it.cnr.ilc.lc.omega.entity.Locus;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.entity.TextLocus;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
import it.cnr.ilc.lc.omega.persistence.PersistenceHandler;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = ResourceManagerSPI.class, name = "text")
public class ResourceManagerText implements ResourceManagerSPI {

    private static Logger log = LogManager.getLogger(ResourceManagerText.class);

    private final MimeType mimeType;

    @Part
    private static PersistenceHandler persistence;

    public ResourceManagerText() throws MimeTypeParseException {
        this.mimeType = new MimeType("text/plain");
    }

    @Override
    public MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public void register(String type, Class<? extends Annotation.Data> clazz) {
        Annotation.register(type, clazz);
    }

    @Override
    public <T extends SuperNode> T create(ResourceManager.CreateAction createAction, URI uri) {
        switch (createAction) {
            case SOURCE:
                return (T) createSource(uri);
            case CONTENT:
                return (T) createContent(uri);
//            case FOLDER:
//                return (T) createFolder(uri);
            case LOCUS:
                return (T) createLocus(uri);
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
//            case FOLDER:
//                updateFolder(sourceUri, targetUri);
//                break;

            default:
                throw new UnsupportedOperationException(updateAction.name() + " unsupported");
        }
    }

    private Source<TextContent> createSource(URI uri) {
        // controllare che la risorsa non sia già presente con lo stesso URI
        Source<TextContent> source = Source.sourceOf(TextContent.class, uri);
        log.info("source uri: " + source.getUri());

        return source;
    }

    private TextContent createContent(URI uri) {

        TextContent content = Content.contentOf(TextContent.class); // non mi piace il tipo così specifico

        /*
         * la uri in input non e' modificata per poter caricare il contenuto della source (puntata da uri)
         * la uri, che identifica il content, e' costruita con un protocollo specifico (per il momento
         * tramite concatenazione con "/content/" + System.currentTimeMillis()
         */
        content.setUri(Utils.appendContentID(uri));

        try {
            //controllare che la risorsa non sia già presente con lo stesso URI
            URLConnection site = uri.toURL().openConnection(); //PER CARICARE RISORSA REMOTA
            content.setText(new Scanner(new BufferedInputStream(site.getInputStream()), "UTF-8").useDelimiter(Pattern.compile("\\A")).next());

        } catch (MalformedURLException mue) {
            log.error("In verify URI as an URL: " + uri.toASCIIString() + " => " + mue.getMessage());
        } catch (IOException e) {
            log.error("Opening connection", e);
        } catch (IllegalArgumentException ex) {
            //Logger.getLogger(ResourceManagerText.class.getName()).log(Level.WARNING, "Invalid URI: " + uri.toASCIIString(), new IllegalArgumentException("Invalid URI: " + uri.toASCIIString()));
            log.warn("Invalid URI " + uri.toASCIIString() + ", " + ex.getLocalizedMessage());
            //LA RISORSA NON E' REMOTA
        }
        return content;
    }

    private void updateContent(URI sourceUri, URI targetUri) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        session.beginTransaction();
//        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", sourceUri.toASCIIString())).iterator().next();
//        source.setContent(session.loadAll(TextContent.class, new Filter("uri", targetUri.toASCIIString())).iterator().next());
//        session.persist(source);
//        session.getTransaction().commit();
        throw new UnsupportedOperationException("to be implemented");

    }

    // CLAVIUS
//    private Folder createFolder(URI uri) {
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Folder folder = new Folder();
//        folder.setName(uri.toASCIIString());
//        return folder;
//    }
//
//    private void updateFolder(URI sourceUri, URI targetUri) {
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        session.beginTransaction();
//        Folder folder = session.loadAll(Folder.class, new Filter("name", sourceUri.toASCIIString())).iterator().next();
//        /*TODO controllare se URI target e' un Source oppure un folder. Se è un folder va chiamato il metodo folder.addFolder*/
//        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", targetUri.toASCIIString())).iterator().next();
//        folder.addFile(source);
//        session.persist(folder);
//        session.getTransaction().commit();
//    }
    @Override
    public <T extends Content, E extends Annotation.Data> Annotation<T, E>
            create(String type, AnnotationBuilder<E> builder) throws InvalidURIException {

        Annotation<T, E> annotation = Annotation.newAnnotation(type, builder);

        return annotation;
    }

    @Override
    public <T extends SuperNode> void persist(T resource) {

//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        session.persist(resource);
        log.info("persist resource, is the transaction active? " + persistence.getEntityManager().getTransaction().isActive());
        try {

            persistence.getEntityManager().persist(resource);
            log.debug("resource persisted");
        } catch (EntityExistsException | TransactionRequiredException e) {
            // } catch (Exception e) {
            log.error("in persist resource " + e);
        } catch (org.hibernate.exception.ConstraintViolationException e2) {
            // throw new UnsupportedOperationException("to be implemented");            log.error("in persist resource " + e);
            log.error("in persist resource 2 ", e2);

        }
    }

    @Override
    public <T extends SuperNode> T merge(T resource) {

        log.info("merge resource, is the transaction active? " + persistence.getEntityManager().getTransaction().isActive());
        try {

            log.debug("resource merged");
            return persistence.getEntityManager().merge(resource);
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.error("in merge resource " + e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <T extends SuperNode> Enum<EntityStatusEnum> status(T resource) {
        log.info("merge resource, is the transaction active? " + persistence.getEntityManager().getTransaction().isActive());

        Enum<EntityStatusEnum> status = EntityStatusEnum.NAN;
        try {

            if (persistence.getEntityManager().contains(resource)) {
                status = EntityStatusEnum.MANAGED;
                log.debug("resource managed");
            } else {
                status = EntityStatusEnum.UNMANAGED;
                log.debug("resource unmanaged");
            }
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.error("in merge resource " + e);
        }

        return status;
    }

    private TextLocus createLocus(URI uri) {

        TextLocus locus = Locus.locusOf(TextLocus.class, uri, Locus.PointsTo.SOURCE);
        locus.setUri(uri.toASCIIString());
        return locus;

    }

    @Override
    public <T> T load(URI uri, Class<T> clazz) {
//        Session session = Neo4jSessionFactory.getNeo4jSession();
//        Source<TextContent> source = session.loadAll(Source.class, new Filter("uri", uri.toASCIIString())).iterator().next();
//
//        return (T) source;
        T result = null;
        EntityManager em = persistence.getEntityManager();
        log.info("load resource, is the transaction active? " + em.getTransaction().isActive());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);

        Root<T> root = cq.from(clazz);
        cq.where(cb.equal(root.get("uri"), uri.toASCIIString()));

        TypedQuery<T> q = em.createQuery(cq);

        try {
            result = q.getSingleResult();
        } catch (NoResultException e) {
            log.warn(e.getMessage());
        }

//        loadAll(Source.sourceOf(TextContent.class, URI.create("")).getClass());
        return result;
    }

    @Override
    public <T extends SuperNode> List<T> loadAll(Class<T> clazz) {

        List<T> ret = null;
        log.info("Loading all, clazz is " + clazz);
        if (clazz.isInstance(Source.sourceOf(TextContent.class, URI.create("dummyURI")))) {
            log.info("loading all " + clazz.toString() + " object instance of Source");

            ret = (List<T>) loadAllTextResource((Source.sourceOf(TextContent.class, URI.create("dummyURI"))).getClass());
        } else {
            try {
                Annotation.register("dummy", DummyAnnotation.class);

                if (clazz.isInstance(Annotation.newAnnotation("dummy", new DummyAnnotationBuilder().URI(URI.create("/dummyUri/"))))) {
                    log.info("loading all " + clazz + " object instance of Annotation");

                    ret = (List<T>) loadAllTextResource(Annotation.newAnnotation("dummy", new DummyAnnotationBuilder().URI(URI.create("/dummyUri/"))).getClass());
                } else {
                    log.info("loading all " + clazz.toString() + " object instance of Other");
                    ret = (List<T>) loadAllTextResource(clazz);
//                    throw new IllegalArgumentException("Aspected TextContent as resource type");
                }
            } catch (InvalidURIException ex) {
                log.error(ex);
            }
        }

        return ret;
    }

    //@Override
    public <T extends SuperNode> List<T> loadAll2(Class<T> clazz) {

        List<T> ret = null;
        log.info("Loading all, clazz is " + clazz);
        Annotation.register("dummy", DummyAnnotation.class);

        if (clazz.isInstance(Source.sourceOf(TextContent.class, URI.create("dummyURI")))) {
            log.info("loading all (" + clazz.toString() + ") object instance of Source");
            ret = (List<T>) loadAllTextResource(clazz);

        } else if (clazz.isInstance(Annotation.newAnnotation("dummy", new DummyAnnotationBuilder().URI(URI.create("/dummyUri/"))))) {
            log.info("loading all (" + clazz.toString() + ") object instance of Annotation");

            ret = (List<T>) loadAllTextResource(clazz);

        } else {
            log.info("loading all (" + clazz.toString() + ") object instance of Other");
            ret = (List<T>) loadAllTextResource(clazz);
        }
//        else {
//                    throw new IllegalArgumentException("Aspected TextContent as resource type");
//    }
        return ret;
    }

    private <T> List<T> loadAllTextResource(Class<T> clazz) {
        EntityManager em = persistence.getEntityManager();
        log.info("Creating local criteria query for load all text content ");

        //Query q =  em.createQuery("Select s From Source s");
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(clazz);

        Root<T> c = cq.from(clazz);

        //Subquery<Annotation> subq = cq.subquery(Annotation.class);
        //Root<Annotation> annotationRoot = subq.from(Annotation.class);
//        Path<Annotation.Data> pathData = annotationRoot.join("data", JoinType.LEFT);
//        log.info("URI " + pathData.get("uri"));
//        Predicate correlatedSubqJoin = cb.equal(c, annotationRoot);
        //subq.select(annotationRoot).where(correlatedSubqJoin);
        //subq.select(annotationRoot);
        //annotationRoot.fetch("data", JoinType.LEFT);
        /* CriteriaQuery<Annotation> cqAnn = cb.createQuery(Annotation.class);
        Root<Annotation> rootAnn = cqAnn.from(Annotation.class);
        rootAnn.fetch("data", JoinType.LEFT);

        cqAnn.select(rootAnn);
        TypedQuery<Annotation> qAnn = em.createQuery(cqAnn);
        List<Annotation> los = qAnn.getResultList();
        log.warn("resultset lenght " + los.size());
        log.warn("Authors(0) " + ((WorkAnnotation)los.get(0).getData()).getAuthors());
        return null; */
        cq.select(c);//.where(cb.exists(subq));
        TypedQuery<T> q = em.createQuery(cq);
//
//        //System.err.println("result " + result);
        List<T> los = q.getResultList();
        log.warn("resultset lenght " + los.size());
        return (List<T>) los;
    }

    @Override
    public void update(ResourceManager.UpdateAction updateAction, URI resourceUri, ResourceStatus status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends SuperNode> T update(ResourceManager.UpdateAction updateAction, T resource, ResourceStatus status) {
        switch (updateAction) {
            case ANNOTATION:
                return (T) updateTextAnnotation((Annotation<TextContent, Annotation.Data>) resource, status); // controllare i cast per i tipi parmetrici
            case LOCUS:
                return (T) updateTextLocus((TextLocus) resource, status); // controllare i cast per i tipi parmetrici
            case CONTENT:
                return (T) updateTextContent((TextContent) resource, status);
            case ANNOTATION_RELATION:
                return (T) updateAnnotationRelation((AnnotationRelation) resource, status);
            default:
                throw new UnsupportedOperationException(updateAction.name() + " unsupported");
        }
    }

    private <E extends Annotation.Data> Annotation<TextContent, E>
            updateTextAnnotation(Annotation<TextContent, E> annotation,
                    ResourceStatus<TextContent, E, TextContent> status) {

        if (status.getTextLocus().isPresent()) {
            annotation.addLocus(status.getTextLocus().get());
        }
        return annotation;
    }

    private <E extends Annotation.Data> TextLocus
            updateTextLocus(TextLocus locus, ResourceStatus<TextContent, E, TextContent> status) {

        if (status.getPointsTo().isPresent()) {
            locus.setPointsTo(status.getPointsTo().get().name());
        }

        if (status.getSource().isPresent()) {
            locus.setSource(status.getSource().get());
        }

        if (status.getStart().isPresent()) {
            locus.setStartLocus(status.getStart().getAsInt());
        }

        if (status.getEnd().isPresent()) {
            locus.setEndLocus(status.getEnd().getAsInt());
        }

        return locus;
    }

    private <E extends Annotation.Data> TextContent
            updateTextContent(TextContent content, ResourceStatus<TextContent, E, TextContent> status) {

        content.setText(status.getText().get());
        return content;
    }

    private AnnotationRelation updateAnnotationRelation(AnnotationRelation relation, ResourceStatus status) {

        Optional<ADTAnnotation> source = status.getSourceAnnotation();
        source.get().registerAsSource(relation);

        Optional<ADTAnnotation> target = status.getTargetAnnotation();
        target.get().registerAsTarget(relation);

        return relation;
    }

    @Override
    public void delete(ResourceManager.DeleteAction deleteAction, ResourceStatus status) {
        switch (deleteAction) {
            case SOURCE:
                deleteSource(status);
                break;
            case ANNOTATION:
                deleteAnnotation(status);
                break;
            case LOCUS:
            case CONTENT:
            case ANNOTATION_RELATION:
            default:
                throw new UnsupportedOperationException(deleteAction.name() + " unsupported");
        }
    }

    private void deleteSource(ResourceStatus status) {
        log.info("deleteSource(), is the transaction active? " + persistence.getEntityManager().getTransaction().isActive());
        try {
            Source<TextContent> source = (Source<TextContent>) status.getSource().get();
            List<TextLocus> loci = loadLoci(source);
            persistence.getEntityManager().remove(persistence.getEntityManager().merge(source));
            HashSet<Annotation> annotationList = new HashSet<>();
            for (TextLocus locus : loci) {
                Annotation current = locus.getAnnotation();
                annotationList.add(locus.getAnnotation());
                locus.setAnnotation(null);
                current.removeLocus(locus);
                persistence.getEntityManager().remove(locus);
                persistence.getEntityManager().merge(current);
            }

            //persistence.getEntityManager().flush(); //forza la rimozione dei locus prima della fine della transazione(?)
            for (Annotation annotation : annotationList) {
                annotation = persistence.getEntityManager().merge(annotation);
                if (annotation.isEmptyLoci() && annotation.isEmptyRelation()) {
                    persistence.getEntityManager().remove(annotation); //TODO deve richiamare il metodo per la cancellazione delle annotazioni!
                    log.warn("TODO deve richiamare il metodo per la cancellazione delle annotazioni!");
                }
            }
            log.info("source deleted");
        } catch (TransactionRequiredException e) {
            log.error("in delete resource " + e);
        }
    }

    private void deleteAnnotation(ResourceStatus status) {
        log.info("deleteAnnotation(), is the transaction active? " + persistence.getEntityManager().getTransaction().isActive());

        if (null != status && status.getAnnotation().isPresent()) {
            Annotation ann = (Annotation) status.getAnnotation().get();
            ann = persistence.getEntityManager().merge(ann);
            List<TextLocus> loci = ann.getLoci();
            if (null != loci) {
                int size = loci.size();
                for (int i = 0; i < size; i++) {
                    TextLocus locus = loci.get(i);
                    locus.setAnnotation(null);
                    locus.setSource(null);
                    ann.removeLocus(locus);
                    persistence.getEntityManager().remove(locus);
                    persistence.getEntityManager().merge(ann);
                }
                log.info("Loci must be empty! Isn't it? " + loci.isEmpty());
            } else {
                log.info("Loci is null");
            }

            List<AnnotationRelation> relations = ann.getRelations();
            if (null != relations) {
                int size = relations.size();
                for (int i = 0; i < size; i++) {
                    relations.get(i).setSourceAnnotation(null);
                    relations.get(i).setTargetAnnotation(null);
                    persistence.getEntityManager().remove(persistence.getEntityManager().merge(relations.get(i)));
                    ann.removeRelation(relations.get(i));
                    persistence.getEntityManager().merge(ann);
                }
                log.info("Relations must be empty! Isn't it? " + relations.isEmpty());
            } else {
                log.info("Relations is null");
            }

            List<AnnotationRelation> targetRelations = loadAnnotationRelationAsTarget(ann);
            if (null != targetRelations) {
                int size = targetRelations.size();
                for (int i = 0; i < size; i++) {
                    Annotation sourceAnnotation = targetRelations.get(i).getSourceAnnotation();
                    targetRelations.get(i).setSourceAnnotation(null);
                    targetRelations.get(i).setTargetAnnotation(null);
                    sourceAnnotation.removeRelation(targetRelations.get(i));
                    persistence.getEntityManager().remove(targetRelations.get(i));
                    targetRelations.remove(i);
                    persistence.getEntityManager().merge(sourceAnnotation);

                }
                log.info("targetRelations must be empty! Isn't it? " + targetRelations.isEmpty());
            } else {
                log.info("targetRelations is null");
            }

            try {
                if (persistence.getEntityManager().contains(ann)) {
                    log.info("deleteAnnotation(), annotation is attached");
                    persistence.getEntityManager().remove(ann);
                } else {
                    log.info("deleteAnnotation(), annotation is detached");
                    persistence.getEntityManager().remove(persistence.getEntityManager().merge(ann));
                }

                log.info("deleteAnnotation(), annotation is deleted");

            } catch (TransactionRequiredException e) {
                log.error("in delete resource " + e);
            }
        }

    }

    private List<TextLocus> loadLoci(Source source) {
        EntityManager em = persistence.getEntityManager();
        log.info("Creating local criteria query for load loci pointing to source " + source.getUri());

        //Query q =  em.createQuery("Select s From Source s");
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TextLocus> cq = cb.createQuery(TextLocus.class);

        Root<TextLocus> textLocus = cq.from(TextLocus.class);

        Predicate predicate = cb.equal(textLocus.get("source"), source);

        cq.select(textLocus).where(predicate);
        TypedQuery<TextLocus> query = em.createQuery(cq);
//
//        //System.err.println("result " + result);
        List<TextLocus> los = query.getResultList();
        log.info("resultset lenght " + los.size());
        return los;
    }

    @Override
    public boolean check(ResourceManager.CheckAction action, ResourceStatus status) {
        switch (action) {

            case ANNOTATION_RELATION:
                return checkAnnotationRelationAsTarget((Annotation<?, ?>) status.getAnnotation().get());
            default:
                throw new UnsupportedOperationException(action.name() + " unsupported");
        }
    }

    private <T extends Content, E extends Annotation.Data>
            boolean checkAnnotationRelationAsTarget(Annotation<T, E> annotation) {

        EntityManager em = persistence.getEntityManager();
        log.info("checkAnnotationRelationAsTarget " + annotation.getUri());

        //Query q =  em.createQuery("Select s From Source s");
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<AnnotationRelation> root = cq.from(AnnotationRelation.class);
        Predicate predicate = cb.equal(root.get("targetAnnotation"), annotation);
        cq.select(cb.count(root));
        cq.where(predicate);

        Long count = em.createQuery(cq).getSingleResult();
        log.info("How many incoming relation ? " + (count > 0) + " count = " + count);
        return (count > 0);
    }

    private <T extends Content, E extends Annotation.Data>
            List<AnnotationRelation> loadAnnotationRelationAsTarget(Annotation<T, E> annotation) {

        EntityManager em = persistence.getEntityManager();
        log.info("loadAnnotationRelationAsTarget " + annotation.getUri());

        //Query q =  em.createQuery("Select s From Source s");
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<AnnotationRelation> cq = cb.createQuery(AnnotationRelation.class);

        Root<AnnotationRelation> root = cq.from(AnnotationRelation.class);
        Predicate predicate = cb.equal(root.get("targetAnnotation"), annotation);
        cq.select(root);
        cq.where(predicate);

        List<AnnotationRelation> result = em.createQuery(cq).getResultList();
        log.info("How many incoming relation ? " + result.size());
        return result;
    }

}
