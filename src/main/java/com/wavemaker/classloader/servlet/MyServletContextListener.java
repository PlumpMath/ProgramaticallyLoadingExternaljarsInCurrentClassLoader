package com.wavemaker.classloader.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:sunil.pulugula@wavemaker.com">Sunil Kumar</a>
 * @since 19/9/16
 */
public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        try {
            // Get the method URLClassLoader#addURL(URL)
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            // Make it accessible as the method is protected
            method.setAccessible(true);
            String[] paths = {"/WEB-INF/prefab1/lib", "/WEB-INF/prefab1/lib"};
            for (String path : paths) {
                File parent = new File(sce.getServletContext().getRealPath(path));
                File[] jars = parent.listFiles(
                        new FilenameFilter() {
                            @Override
                            public boolean accept(final File dir, final String name) {
                                return name.endsWith(".jar");
                            }
                        }
                );
                if (jars == null)
                    continue;
                for (File jar : jars) {
                    // Add the URL to the context CL which is a URLClassLoader
                    // in case of Tomcat
                    method.invoke(
                            sce.getServletContext().getClass().getClassLoader(),
                            jar.toURI().toURL()
                    );
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("failed to load prefab jars in application class loader");
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

    }
}
