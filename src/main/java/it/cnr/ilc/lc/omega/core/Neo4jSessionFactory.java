package it.cnr.ilc.lc.omega.core;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

/**
 *
 * @author oakgen
 */
public class Neo4jSessionFactory {

    private static final SessionFactory SESSION_FACTORY = new SessionFactory("it.cnr.ilc.lc.omega");
    private static final Session Neo4jSession = SESSION_FACTORY.openSession("http://localhost:7474"); //FIXME Messo singleton per evitare nullpointer dovuto alla rigenerazione della session
    static {
        System.setProperty("username", "neo4j");
        System.setProperty("password", "omega");
    }

    public static Session getNeo4jSession() {
//        return SESSION_FACTORY.openSession("http://wafi.iit.cnr.it:8074");
       // return SESSION_FACTORY.openSession("http://localhost:7474"); 
        return Neo4jSession;
    }

}
