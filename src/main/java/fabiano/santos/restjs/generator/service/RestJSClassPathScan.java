/**
 * 
 */
package fabiano.santos.restjs.generator.service;

import java.net.URL;

import javax.servlet.ServletContext;

import org.scannotation.ClasspathUrlFinder;
import org.scannotation.WarUrlFinder;

/**
 * Various functions CLASSPATH scan.
 * 
 * @author fabiano.santos
 * 
 */
public class RestJSClassPathScan {

	/**
	 * Uses the java.class.path system property to obtain a list of URLs that
	 * represent the CLASSPATH.
	 * 
	 * @return
	 */
	public static URL[] findClassPaths() {
		return ClasspathUrlFinder.findClassPaths();
	}

	/**
	 * Uses the java.class.path system property to obtain a list of URLs that
	 * represent the CLASSPATH paths is used as a filter to only include paths
	 * that have the specific relative file within it.
	 * 
	 * @param paths
	 *            Comma list of files that should exist in a particular path
	 * @return
	 */
	public static URL[] findClassPaths(String... paths) {
		return ClasspathUrlFinder.findClassPaths(paths);
	}

	/**
	 * Find the URL pointing to "/WEB-INF/classes" This method may not work in
	 * conjunction with IteratorFactory if your servlet container does not
	 * extract the /WEB-INF/classes into a real file-based directory
	 * 
	 * @param servletContext
	 * @return null if cannot find /WEB-INF/classes
	 */
	public static URL findWebInfClassesPath(ServletContext servletContext) {
		return WarUrlFinder.findWebInfClassesPath(servletContext);
	}

	/**
	 * Find the URLs pointing to all jars found in lib.
	 * 
	 * @param servletContext
	 * @return
	 */
	public static URL[] findWebInfLibClasspaths(ServletContext servletContext) {
		return WarUrlFinder.findWebInfLibClasspaths(servletContext);
	}
}
