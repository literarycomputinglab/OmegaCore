/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.core.SearchManager;
import it.cnr.ilc.lc.omega.entity.SuperNode;
import java.util.List;

/**
 *
 * @author simone
 */
public interface SearchManagerSPI {

    public <T extends SuperNode> List<T> search(SearchManager.SearchType type, String query, Class<T> clazz);

    public <E extends SuperNode, T extends SuperNode> List<E> searchOnField(SearchManager.SearchType type, String query, String field, Class<T> clazz);

    public Boolean reindex();
    
}
