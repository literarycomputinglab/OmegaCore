package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.persistence.PersistenceHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirius.kernel.di.std.Part;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author oakgen
 */
public abstract class ManagerAction {

    private static Logger log = LogManager.getLogger(ManagerAction.class);

    @Part
    static PersistenceHandler persistence;

    public <T> T doAction() throws ActionException {
        T ret = null;
        EntityManager entityManager = null;
        try {
            log.info("persistence is " + persistence);
            entityManager = persistence.getEntityManager();
            entityManager.getTransaction().begin();
            log.info("Transaction is in progress? " + entityManager.getTransaction().isActive());
            log.info("ENTITY MANAGER " + entityManager);
            ret = action();
            log.info("after action(), Transaction is in progress? " + entityManager.getTransaction().isActive());
            //System.err.println(session.getTransaction().toString());
            entityManager.getTransaction().commit();
            log.info("after commit transaction");
        } catch (ActionException e) {
            log.error("In transaction ", e);
            throw new ActionException(e);
        } catch (RollbackException rbe) {
            log.error("Errore in commit: " + ExceptionUtils.getRootCause(rbe.getCause()).getMessage());
            throw new ActionException(ExceptionUtils.getRootCause(rbe));
        } finally {
            try {
                if (null != entityManager) {
                    entityManager.close();
                } else {
                    log.warn("entity manager is null!");
                }
                log.info("entity manager is now closed");
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

        public ActionException(Throwable t) {
            super(t);
        }
    }
}
