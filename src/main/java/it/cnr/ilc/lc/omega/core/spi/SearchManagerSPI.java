/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lc.omega.core.spi;

import it.cnr.ilc.lc.omega.entity.Source;
import java.util.List;
import sirius.kernel.di.std.Register;

/**
 *
 * @author simone
 */
public interface SearchManagerSPI {

    public <T extends Source<?>> List<T> search(String query, Class<T> clazz);

}
