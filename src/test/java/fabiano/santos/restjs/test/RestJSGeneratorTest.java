/**
 * 
 */
package fabiano.santos.restjs.test;

import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;
import org.scannotation.ClasspathUrlFinder;

import fabiano.santos.restjs.generator.exception.RestJSException;
import fabiano.santos.restjs.generator.service.RestJSGenerator;

/**
 * @author fabiano.santos
 * 
 */
public class RestJSGeneratorTest {

	@Test
	public void testSimpleClass() throws RestJSException {
		StringWriter writer = new StringWriter();
		RestJSGenerator generator = new RestJSGenerator("/api/restjs", writer);
		generator.generate("RestJS", ClasspathUrlFinder.findClassPaths());
		String api = writer.toString();
		Assert.assertTrue("API has content", api.length() > 0);
		System.out.println(api);
	}
}
