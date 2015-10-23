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
            //session.beginTransaction();
            System.err.println(session.toString());
            action();
            System.err.println("NELLA DO ACTION DOPO LA ACTION");
            System.err.println("PRENDE LA TRANSAZIONE:");
            //System.err.println(session.getTransaction().toString());

            //session.getTransaction().commit(); //FIXME la trasazione risulta nulla: capire bene!!

            System.err.println("NELLA DO ACTION DOPO IL COMMIT");
        } catch (Exception e) {
            System.err.println("NELLA catch DEL COMMIT");
            try {
                //session.getTransaction().rollback();
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
