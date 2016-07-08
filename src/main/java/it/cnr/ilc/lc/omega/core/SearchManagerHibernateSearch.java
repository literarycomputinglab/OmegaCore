/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.SearchManagerSPI;
import it.cnr.ilc.lc.omega.entity.Content;
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
    public <T extends Source<?>> List<T> search(String keyword, Class<T> clazz) {

        List<T> lstc = new ArrayList<>();

        EntityManager em = persistence.getEntityManager();
        if (!em.getTransaction().isActive()) {
            throw new IllegalStateException("Not in transaction! Transaction must be opened by the caller!");
        }

        FullTextEntityManager fullTextEntityManager
                = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        QueryBuilder builder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(TextContent.class).get();

        org.apache.lucene.search.Query query = builder
                .keyword()
                .onField("text")
                .matching(keyword)
                .createQuery();

        //Query luceneQuery = builder.all().createQuery();
        log.info("Searching for TextContent");

        javax.persistence.Query persistenceQuery
                = fullTextEntityManager.createFullTextQuery(query, TextContent.class);

        List<TextContent> results = persistenceQuery.getResultList();

        log.info("Result is empty? " + results.isEmpty());

        for (TextContent result : results) {
            lstc.add((T) result.getSource());
        }

        log.info("End.");
        return lstc;
    }

}
