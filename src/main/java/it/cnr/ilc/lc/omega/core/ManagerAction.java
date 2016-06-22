package it.cnr.ilc.lc.omega.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Part;
import it.cnr.ilc.lc.omega.persistence.PersistenceHandler;
import javax.persistence.EntityManager;
import sirius.kernel.commons.Exec;

/**
 *
 * @author oakgen
 */
public abstract class ManagerAction {

    private static Logger log = LogManager.getLogger(ManagerAction.class);

    @Part
    PersistenceHandler persistence;

    public <T> T doAction() throws ActionException {
        T ret = null;
        EntityManager entityManager = null;
        try {
            entityManager = persistence.getEntityManager();
            entityManager.getTransaction().begin();
            log.info("Transaction is in progress? " + entityManager.getTransaction().isActive());
            ret = action();
            log.debug("after action()");
            //System.err.println(session.getTransaction().toString());
            entityManager.getTransaction().commit();

            log.debug("after commit transaction");
        } catch (Exception e) {
            log.error("In transaction ", e);
            throw new ActionException(e);
        } finally {
            try {
                entityManager.close();
            } catch (Exception ee) {
                log.error("Closing entityManager ", ee);
            }
        }
        return ret;
    }

    protected abstract <T> T action() throws ActionException;

    public static class ActionException extends Exception {

        public ActionException(Exception e) {
            super(e);
        }
    }
}
