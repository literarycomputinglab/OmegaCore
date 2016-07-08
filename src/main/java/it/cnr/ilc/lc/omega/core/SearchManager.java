/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.spi.ResourceManagerSPI;
import it.cnr.ilc.lc.omega.core.spi.SearchManagerSPI;
import it.cnr.ilc.lc.omega.entity.Source;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import it.cnr.ilc.lc.omega.entity.TextContent;
import it.cnr.ilc.lc.omega.exception.InvalidURIException;
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

    public List<Source<TextContent>> searchByKeyword(final String keyword) throws ManagerAction.ActionException {

        return new ManagerAction() {

            @Override
            protected List<Source> action() throws ManagerAction.ActionException {
                log.info("searchByKeyword: (" + keyword + ")");
                log.info("searchByKeyword: (" + searchers + ")");
                for (SearchManagerSPI manager : searchers) {
                    List<Source> results = manager.search(keyword, Source.class);
                    return results;
                }
                return null;
            }
        }.doAction();
        
    }
}
