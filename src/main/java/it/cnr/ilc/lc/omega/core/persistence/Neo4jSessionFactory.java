package it.cnr.ilc.lc.omega.core.persistence;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import sirius.kernel.di.std.ConfigValue;
import sirius.kernel.di.std.Register;

/**
 *
 * @author oakgen
 */
@Register(classes = SessionFactory.class)
public class Neo4jSessionFactory implements OmegaSessionFactory {

    @ConfigValue("session.scope")
    private static  String scope;
    
    @ConfigValue("session.host")
    private static  String host;
    
    @ConfigValue("session.username")
    private static  String username;
    
    @ConfigValue("session.password")
    private static  String password;
    
            
    private static final SessionFactory SESSION_FACTORY = new SessionFactory("it.cnr.ilc.lc.omega");
  //  private static final Session Neo4jSession = SESSION_FACTORY.openSession("http://localhost:7474"); //FIXME Messo singleton per evitare nullpointer dovuto alla rigenerazione della session
    // pool di sessioni per gestire le risorse
    // gestire una mappa di sessioni per currentThread

//    private static final Map<Thread,Session> sessionMap =  new Hashtable<>(); // obsolete
    private static final ConcurrentMap<Thread, Session> sessionMap = new ConcurrentHashMap<>();

    static {
        System.setProperty("username", "neo4j");
        System.setProperty("password", "omega");
    }

    @Override
    public  Session getSession(){
    return getNeo4jSession();
    }
    
    public static Session getNeo4jSession() {
//        return SESSION_FACTORY.openSession("http://wafi.iit.cnr.it:8074");
        // return SESSION_FACTORY.openSession("http://localhost:7474"); 
        Thread currentThread = Thread.currentThread();
        Session session = null;

//        if(sessionMap.containsKey(currentThread)){
//            Session session = sessionMap.get(currentThread);
        session = (sessionMap.containsKey(currentThread)) ?  sessionMap.get(currentThread) : createSession(currentThread);
        return session; // FIXME, vedasi sopra.
    }

    private static synchronized Session createSession(Thread currentThread){
        Session session = SESSION_FACTORY.openSession("http://localhost:7474");
        sessionMap.put(currentThread, session);
        return session;
    }
    
}
