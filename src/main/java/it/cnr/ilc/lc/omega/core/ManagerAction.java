package it.cnr.ilc.lc.omega.core;

import org.neo4j.ogm.session.Session;

/**
 *
 * @author oakgen
 */
public abstract class ManagerAction {

    public void doAction() throws ActionException {
        Session session = Neo4jSessionFactory.getNeo4jSession();
        try {
            session.beginTransaction();
            action();
            session.getTransaction().commit();
        } catch (Exception e) {
            try {
                session.getTransaction().rollback();
            } catch (Exception r) {
            }
            throw new ActionException(e);
        } 
    }

    protected abstract void action();

    public static class ActionException extends Exception {

        public ActionException(Exception e) {
            super(e);
        }
    }
}
