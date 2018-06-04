/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.SearchManagerSPI;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.persistence.PersistenceHandler;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;

/**
 *
 * @author simone
 */
@Register(classes = SearchManagerSPI.class, name = "hibernateSearch")
public class SearchManagerHibernateSearch implements SearchManagerSPI {

    private static Logger log = LogManager.getLogger(SearchManagerHibernateSearch.class);

    @Part
    PersistenceHandler persistence;

    @Override
    public <T extends SuperNode> List<T> search(SearchManager.SearchType type, String queryString, Class<T> clazz) {

        return searchOnField(type, queryString, null, clazz);
    }

    @Override
    public <E extends SuperNode, T extends SuperNode> List<E> searchOnField(SearchManager.SearchType type, String queryString, String field, Class<T> clazz) {

        List<?> lstc = null;

        EntityManager em = persistence.getEntityManager();
        if (!em.getTransaction().isActive()) {
            throw new IllegalStateException("Not in transaction! Transaction must be opened by the caller!");
        }

        FullTextEntityManager fullTextEntityManager
                = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        switch (type) {
            case KEYWORD_ON_SOURCE:
                lstc = searchSourceByKeyword(queryString, fullTextEntityManager);
                break;
            case KEYWORD_ON_ANNOTATION:
                lstc = searchAnnotationByKeyword(queryString, fullTextEntityManager);
                break;
            case QUERY_FIELD_ON_ANNOTATION_DATA:
                lstc = searchAnnotationByTypeAndField(queryString, field, (Class<Annotation.Data>) clazz, fullTextEntityManager);
                break;
            default:
                break;
        }

        log.info("End.");
        return (List<E>) lstc;
    }

    private <T extends Source> List<T> searchSourceByKeyword(String queryString, FullTextEntityManager fullTextEntityManager) {
        List<T> lstc = new ArrayList<>();

        QueryBuilder builder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(TextContent.class).get();

        org.apache.lucene.search.Query query = builder
                .keyword()
                .onField("text") //campo legato alla Entity TextContent
                .matching(queryString)
                .createQuery();

        //Query luceneQuery = builder.all().createQuery();
        log.info("Searching for TextContent " + query.toString());

        javax.persistence.Query persistenceQuery
                = fullTextEntityManager.createFullTextQuery(query, TextContent.class);

        List<TextContent> results = persistenceQuery.getResultList();

        log.info("Result is empty? " + results.isEmpty());

        for (TextContent result : results) {
            lstc.add((T) result.getSource());
        }
        return lstc;

    }

    private List<Annotation<TextContent, ?>> searchAnnotationByKeyword(String queryString, FullTextEntityManager fullTextEntityManager) {
        List<Annotation<TextContent, ?>> lstc = new ArrayList<>();

        QueryBuilder builder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Annotation.Data.class).get();

        org.apache.lucene.search.Query query = builder
                .keyword()
                .onField("indexField") //campo legato alla Entity TextContent
                .matching(queryString)
                .createQuery();

        //Query luceneQuery = builder.all().createQuery();
        log.info("Searching for TextContent " + query.toString());

        javax.persistence.Query persistenceQuery
                = fullTextEntityManager.createFullTextQuery(query, Annotation.Data.class);

        List<Annotation.Data> results = persistenceQuery.getResultList();

        log.info("Result is empty? " + results.isEmpty());
        log.info("no of result " + results.size());

        for (Annotation.Data result : results) {
            lstc.add((Annotation<TextContent, ?>) result.getAnnotation());
        }
        return lstc;

    }

    private <T extends Annotation.Data > List<Annotation<TextContent, ?>>
        searchAnnotationByTypeAndField(String queryString, String field, Class<T> clazz, FullTextEntityManager fullTextEntityManager) {

        List<Annotation<TextContent, ?>> lstc = new ArrayList<>();

        QueryBuilder builder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(clazz).get();

        org.apache.lucene.search.Query query = builder
                .simpleQueryString()
                .onField(field) //campo legato alla Entity TextContent
                .matching(queryString)
                .createQuery();

        //Query luceneQuery = builder.all().createQuery();
        log.info("Searching for TextContent " + query.toString());

        javax.persistence.Query persistenceQuery
                = fullTextEntityManager.createFullTextQuery(query, clazz);

        List<T> results = persistenceQuery.getResultList();

        log.info("Result is empty? " + results.isEmpty());
        log.info("no of result " + results.size());

        for (T result : results) {
            lstc.add((Annotation<TextContent, ?>) result.getAnnotation());
        }
        return lstc;
    }

}
