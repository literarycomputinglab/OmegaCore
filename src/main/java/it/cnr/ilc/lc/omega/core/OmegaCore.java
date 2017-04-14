package it.cnr.ilc.lc.omega.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author oakgen
 * @author simone
 * @author angelo
 */
public class OmegaCore {

    private static final Logger log = LogManager.getLogger(OmegaCore.class);

    private static final int DEFAULT_PORT = 7777;
    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    public static void init(String[] args) {
        log.info("init() start");
        boolean kill = Boolean.parseBoolean(System.getProperty("kill"));
        int port = DEFAULT_PORT;
        if (System.getProperty("port") != null) {
            port = Integer.parseInt(System.getProperty("port"));
        }
        // When we're started as windows service, the start/stop command and port are passed in
        // as arguments
        if (args.length == 2) {
            if ("stop".equals(args[0])) {
                kill = true;
            }
            port = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        if (port > 0) {
            if (kill) {
                kill(port);
            } else {
//                kickstart(port);
            }
        }
        log.info("init() end");

    }

    /**
     * Kills an app by opening a connection to the lethal port.
     */
    private static void kill(int port) {
        try {
            log.info("Killing localhost: " + port);
            long now = System.currentTimeMillis();
            Socket socket = new Socket("localhost", port);
            socket.getInputStream().read();
            log.info("Kill succeeded after: " + (System.currentTimeMillis() - now) + " ms");
        } catch (Exception e) {
            log.error("Kill failed: ", e);
        }
    }

    /*
     * Sets up a classloader and loads <tt>Sirius</tt> to initialize the framework.
     */
//    private static void kickstart(int port) {
    public static void start() {
        boolean debug = true;
        //boolean debug = Boolean.parseBoolean(System.getProperty("debug"));
        boolean ide = Boolean.parseBoolean(System.getProperty("ide"));
        // File home = new File(System.getProperty("user.dir"));
        File home = new File("/home/simone/NetBeansProjects/OmegaTest/target/"); // FIXME: attenzione risistemare questa parte!
        log.info("IDE Flag: " + ide);
        log.info("I N I T I A L   P R O G R A M   L O A D");
        log.info("---------------------------------------");
        log.info("IPL from: " + home.getAbsolutePath());

        if (!ide) {
            List<URL> urls = new ArrayList<>();
            try {
                File jars = new File(home, "lib");
                if (jars.exists()) {
                    for (URL url : allJars(jars)) {
                        log.info(" - Classpath: " + url);
                        urls.add(url);
                    }
                } else {
                    log.info ("no jars in " + jars.getPath());
                }
            } catch (Throwable e) {
                log.error("reading jar from lib", e);
            }
            try {
                File classes = new File(home, "app");
                if (classes.exists()) {
                    log.info(" - Classpath: " + classes.toURI().toURL());
                    urls.add(classes.toURI().toURL());
                }
            } catch (Throwable e) {
                log.error("reading jar from app ", e);
            }
            loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), loader);
            Thread.currentThread().setContextClassLoader(loader);
        } else {
            log.warn("IPL from IDE: not loading any classes or jars!");
        }

        try {
            log.info("IPL completed - Loading Sirius as stage2...");
            log.info("");
            //   System.setProperty("logging", "level = OFF");

//            Class.forName("sirius.kernel.Setup", true, loader)
//                    .getMethod("createAndStartEnvironment", ClassLoader.class)
//                    .invoke(null, loader);
//            
            Class<?> clazz = Class.forName("sirius.kernel.Setup", true, loader);
            log.debug("OmegaCore clazz " + clazz);
            Method startEnv = clazz.getMethod("createAndStartEnvironment", ClassLoader.class);
            log.debug("OmegaCore startEnv " + startEnv);
            Object retu = startEnv.invoke(null, loader);
            log.debug("OmegaCore startEnv retu " + retu);

//            Setup.createAndStartEnvironment(loader);
            log.info("Sirius L O A D E D...");

//            final KernelTest test = new KernelTest();
//            Thread testThread = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                    test.test();
//                }
//            });
//            testThread.start();
//            
            // test();
//            waitForLethalConnection(port);
//            System.exit(0);
        } catch (ClassNotFoundException | NoSuchMethodException |
                SecurityException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
            log.fatal("on starting...", e);
            System.exit(-1);
        }
    }

    public static void stop() {
        try {
            log.info("stopping Sirius");
            Class.forName("sirius.kernel.Sirius", true, loader).getMethod("stop").invoke(null);
            log.info("stopped Sirius");
        } catch (Exception ex) {
            log.error("on stopping...", ex);
        }
    }

    /**
     * Waits until a connection to the given port is made.
     */
    private static void waitForLethalConnection(int port) {
        try {
            log.info(String.format("Opening port %d as shutdown listener%n", port));
            ServerSocket socket = new ServerSocket(port);
            try {
                Socket client = socket.accept();
                log.info("C L O S I N G   M I C R O K E R N E L");
                log.info("---------------------------------------");
                Class.forName("sirius.kernel.Sirius", true, loader).getMethod("stop").invoke(null);
                client.close();
            } finally {
                socket.close();
                log.info("M I C R O K E R N E L  C L O S E D");
                log.info("---------------------------------------");
            }
        } catch (Exception e) {
            log.error("on wait for lethal connection...", e);
        }
    }


    /*
     * Enumerates all jars in the given directory
     */
    private static List<URL> allJars(File libs) throws MalformedURLException {
        List<URL> urls = new ArrayList<>();
        if (libs.listFiles() != null) {
            for (File file : libs.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    urls.add(file.toURI().toURL());
                }
            }
        }
        return urls;
    }

//    @Part
//    private static DocumentManager documentManager;
//
//    private static void test() throws MimeTypeParseException {
//        URI sourceURI = URI.create("http://claviusontheweb.it:8080/exist/rest//db/clavius/documents/147");
//        URI contentURI = URI.create("http://claviusontheweb.it:8080/exist/rest//db/clavius/documents/147/147.txt");
//        documentManager.createSource(sourceURI, new MimeType("text/plain"));
//        documentManager.setContent(sourceURI,contentURI);
//        documentManager.inFolder("archivio", sourceURI);
//    }
}
