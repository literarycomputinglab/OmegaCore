package it.cnr.ilc.lc.omega.core;

import it.cnr.ilc.lc.omega.core.persistence.Neo4jSessionFactory;
import org.neo4j.ogm.session.Session;

/**
 *
 * @author oakgen
 */
public abstract class ManagerAction {

    public <T> T doAction() throws ActionException {
        Session session = Neo4jSessionFactory.getNeo4jSession();
        T ret = null;

        try {
            session.beginTransaction();
            System.err.println(session.toString());
            ret = action();
            System.err.println("NELLA DO ACTION DOPO LA ACTION");
            System.err.println("PRENDE LA TRANSAZIONE:");
            //System.err.println(session.getTransaction().toString());

            session.getTransaction().commit(); //FIXME la trasazione risulta nulla: capire bene!!

            System.err.println("NELLA DO ACTION DOPO IL COMMIT");
        } catch (Exception e) {
            System.err.println("NELLA catch DEL COMMIT");
           // session.getTransaction().rollback();
            e.printStackTrace();
            throw new ActionException(e);
        }
        return ret;
    }

    protected abstract <T> T action();

    public static class ActionException extends Exception {

        public ActionException(Exception e) {
            super(e);
        }
    }
}
