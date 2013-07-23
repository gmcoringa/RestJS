/**
 * 
 */
package fabiano.santos.restjs.generator.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author fabiano.santos
 * 
 */
public class FreemarkerRender {

	private Configuration config;

	public FreemarkerRender() {
		config = new Configuration();
		config.setDefaultEncoding("UTF-8");
		TemplateLoader loader = new ClassTemplateLoader(FreemarkerRender.class,
				"/templates");
		config.setTemplateLoader(loader);
	}

	public void render(String name, Map<String, Object> variables,
			Writer writer, Locale locale) throws IOException, TemplateException {
		Template template;

		if (locale != null) {
			template = config.getTemplate(name, locale);
		} else {
			template = config.getTemplate(name);
		}

		Environment env = template.createProcessingEnvironment(variables,
				writer);
		env.process();
	}
}
