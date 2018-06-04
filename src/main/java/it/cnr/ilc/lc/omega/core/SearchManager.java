/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.SearchManagerSPI;
import it.cnr.ilc.lc.omega.entity.Annotation;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.TextContent;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Parts;
import sirius.kernel.di.std.Register;

/**
 *
 * @author simone
 */
@Register(classes = SearchManager.class)
public final class SearchManager {

    private static final Logger log = LogManager.getLogger(SearchManager.class);

    @Parts(value = SearchManagerSPI.class)
    private Collection<SearchManagerSPI> searchers;

    public enum SearchType {
        KEYWORD_ON_SOURCE, KEYWORD_ON_ANNOTATION, QUERY_FIELD_ON_ANNOTATION_DATA
    }

    public List<Source<TextContent>> searchContentByKeyword(final String keyword) throws ManagerAction.ActionException {

        return new ManagerAction() {

            @Override
            protected List<Source> action() throws ManagerAction.ActionException {
                log.info("searchContentByKeyword: (" + keyword + ")");
                log.info("searchContentByKeyword: (" + searchers + ")");
                for (SearchManagerSPI manager : searchers) {
                    List<Source> results = manager.search(SearchType.KEYWORD_ON_SOURCE, keyword, Source.class);
                    return results;
                }
                return null;
            }
        }.doAction();

    }

    public List<Annotation<TextContent, ?>> searchAnnotationByKeyword(final String keyword) throws ManagerAction.ActionException {

        return new ManagerAction() {

            @Override
            protected List<Annotation> action() throws ManagerAction.ActionException {
                log.info("searchAnnotationByKeyword: (" + keyword + ")");
                log.info("searchAnnotationByKeyword: (" + searchers + ")");
                for (SearchManagerSPI manager : searchers) {
                    List<Annotation> results = manager.search(SearchType.KEYWORD_ON_ANNOTATION, keyword, Annotation.class);
                    return results;
                }
                return null;
            }
        }.doAction();    
    }

    public <T extends Annotation.Data> List<Annotation<TextContent, T>> 
        searchAnnotationByTypeAndField(final Class<T> clazz, final String query, final String field) throws ManagerAction.ActionException {

        return new ManagerAction() {

            @Override
            protected List<Annotation> action() throws ManagerAction.ActionException {
                log.info("searchAnnotationByKeyword: clazz=("+ clazz.getSimpleName() +"), query=(" + query + "), field=("+field+")");

                for (SearchManagerSPI manager : searchers) {
                    List<Annotation> results = manager.searchOnField(SearchType.QUERY_FIELD_ON_ANNOTATION_DATA, query, field, clazz);
                    return results;
                }
                return null;
            }
        }.doAction();    
    }

}
