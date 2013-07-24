/**
 * 
 */
package fabiano.santos.restjs.generator.service;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fabiano.santos.restjs.generator.exception.RestJSException;
import fabiano.santos.restjs.generator.exception.RestJSInvalidArgumentException;
import fabiano.santos.restjs.generator.exception.RestJSNullURL;
import fabiano.santos.restjs.generator.render.FreemarkerRender;
import freemarker.template.TemplateException;

/**
 * This class generate a javascript based on all JAX-RS annotations found in the
 * class.
 * 
 * @author fabiano.santos
 * 
 */
public class RestJSGenerator {

	private static final Logger log = LoggerFactory
			.getLogger(RestJSGenerator.class);

	private AnnotationDB annotationDB;
	private Writer writer;
	private FreemarkerRender render;
	private String basePath;

	/**
	 * Creates a instance of JS generator. Parameter basePath is the URL prefix
	 * for all rest commands.
	 * 
	 * @param basePath
	 * @param writer
	 * @throws RestJSInvalidArgumentException
	 */
	public RestJSGenerator(String basePath, Writer writer)
			throws RestJSInvalidArgumentException {
		if (basePath == null || basePath.trim().isEmpty()) {
			throw new RestJSInvalidArgumentException(
					"Base path cannot be null or empty");
		}

		annotationDB = new AnnotationDB();
		this.writer = writer;
		render = new FreemarkerRender();
		this.basePath = basePath;
	}

	/**
	 * Create a JS script based on the Rest found on the URLs class paths. All
	 * classes with @Path annotation found in the class path URLs will generate
	 * a javascript command.
	 * 
	 * @param nameSpace
	 *            Name space prefix for commands.
	 * @param urls
	 *            Class path URLs
	 * @throws RestJSException
	 */
	public void generate(String nameSpace, URL... urls) throws RestJSException {
		if (urls == null || urls.length == 0) {
			throw new RestJSNullURL("URLs cannot be null or empty");
		}

		log.debug("Scanning " + urls.toString());
		try {
			// Set classes to be scanned
			annotationDB.scanArchives(urls);
		} catch (IOException e) {
			log.error("Failed to scan urls: " + e.getMessage());
			throw new RestJSException(e);
		}
		// Load annotated rest classes
		Set<String> classNames = annotationDB.getAnnotationIndex().get(
				Path.class.getName());

		if (classNames == null) {
			throw new RestJSNullURL("No classes found in urls");
		}

		// Basic api render
		try {
			render.render("api.ftl", null, writer, null);
		} catch (IOException | TemplateException e) {
			throw new RestJSException("Failed to render api.ftl", e);
		}

		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				writeRestService(clazz, nameSpace);
			} catch (ClassNotFoundException e) {
				log.error("Javascript class [" + className
						+ "] not found. Skipping its execution.", e);
			}
		}

	}

	/**
	 * Generates an object type per class found.
	 * 
	 * @param clazz
	 * @param nameSpace
	 * @throws RestJSException
	 */
	private void writeRestService(Class<?> clazz, String nameSpace)
			throws RestJSException {
		Path path = clazz.getAnnotation(Path.class);
		if (path == null)
			return;

		String cmdPath = path.value();

		if (cmdPath.length() > 0 && cmdPath.charAt(0) != '/') {
			cmdPath = "/".concat(cmdPath);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("nameSpace", nameSpace);
		data.put("name", clazz.getSimpleName());
		data.put("basePath", basePath.concat(cmdPath));
		// Service class definition
		try {
			render.render("service-class.ftl", data, writer, null);
		} catch (IOException | TemplateException e) {
			log.error("Failed to render service-class.ftl: " + e.getMessage());
			throw new RestJSException("Failed to render service-class.ftl", e);
		}

		for (Method method : clazz.getMethods()) {
			// Not a rest, skip
			if (!isRestAssigned(method)) {
				continue;
			}

			writeMethod(method, nameSpace, clazz.getSimpleName());
		}
	}

	/**
	 * Validates rest methods.True if is a public method that is annotated with
	 * {@link Path}.
	 * 
	 * @param method
	 * @return True if is a public method that is annotated with {@link Path}.
	 */
	private boolean isRestAssigned(Method method) {
		return Modifier.isPublic(method.getModifiers())
				&& method.isAnnotationPresent(Path.class);
	}

	/**
	 * Write a command based on method JAX-RS annotations to the writer.
	 * 
	 * @param method
	 * @param nameSpace
	 * @param name
	 * @throws RestJSException
	 */
	private void writeMethod(Method method, String nameSpace, String name)
			throws RestJSException {
		Produces produces = method.getAnnotation(Produces.class);
		Consumes consumes = method.getAnnotation(Consumes.class);
		Path path = method.getAnnotation(Path.class);

		Class<?>[] paramTypes = method.getParameterTypes();
		Annotation[][] paramAnnon = method.getParameterAnnotations();
		// Parameters
		// Specific parameter lists
		List<String> queryParams = new ArrayList<>();
		List<String> formParams = new ArrayList<>();
		List<String> pathParams = new ArrayList<>();
		List<String> paramNames = new ArrayList<>();
		// Extract parameters
		extractParameters(paramTypes, paramAnnon, queryParams, formParams,
				pathParams, paramNames);

		Map<String, Object> variables = new HashMap<>();
		variables.put("nameSpace", nameSpace);
		variables.put("name", name);
		variables.put("queryParams", queryParams);
		variables.put("formParams", formParams);
		variables.put("pathParams", pathParams);
		variables.put("paramNames", paramNames);
		variables.put("dataType", getDataType(produces));
		variables.put("contentType", getContentType(consumes));
		variables.put("httpMet", getHttpMethod(method));
		variables.put("path", path.value());
		try {
			// Method definition
			render.render("method.ftl", variables, writer, null);
		} catch (IOException | TemplateException e) {
			throw new RestJSException("Failed to render method.ftl", e);
		}
	}

	/**
	 * Add " around the string.
	 * 
	 * @param value
	 * @return
	 */
	private String envelop(String value) {
		return new StringBuilder("\"").append(value).append("\"").toString();
	}

	/**
	 * Resolve data type (Produces content). Default is text.
	 * 
	 * @param produces
	 * @return
	 */
	private String getDataType(Produces produces) {
		if (produces == null) {
			return envelop("text");
		}

		// TODO deal with other Produces types
		switch (produces.value()[0]) {
		case "application/json":
			return envelop("json");
		case "text/plain":
		default:
			return envelop("text");
		}
	}

	/**
	 * Resolve content type (Consumes content). Default is null, no content is
	 * received.
	 * 
	 * @param consumes
	 * @return
	 */
	private String getContentType(Consumes consumes) {
		if (consumes != null) {
			return envelop(consumes.value()[0]);
		} else {
			return "null";
		}
	}

	/**
	 * Resolve HTTP method. Default is GET.
	 * 
	 * @param method
	 * @return
	 */
	private String getHttpMethod(Method method) {
		String methodStr = HttpMethod.GET;
		// Most used first
		if (method.isAnnotationPresent(GET.class)) {
			methodStr = HttpMethod.GET;
		} else if (method.isAnnotationPresent(POST.class)) {
			methodStr = HttpMethod.POST;
		} else if (method.isAnnotationPresent(DELETE.class)) {
			methodStr = HttpMethod.DELETE;
		} else if (method.isAnnotationPresent(PUT.class)) {
			methodStr = HttpMethod.PUT;
		} else if (method.isAnnotationPresent(HEAD.class)) {
			methodStr = HttpMethod.HEAD;
		}

		return envelop(methodStr);
	}

	/**
	 * Extract all parameters from method. All parameters are added into
	 * paramNames list in the order declared in the method. ParamTypes list is
	 * the class type of each parameter.
	 * 
	 * @param paramTypes
	 *            Type of all parameters.
	 * @param paramAnnon
	 *            Annotations of all parameters
	 * @param queryParams
	 *            Query parameters of the method
	 * @param formParams
	 *            Form parameters of the method
	 * @param pathParams
	 *            Path parameters of the method
	 * @param paramNames
	 *            Names of all parameters of the method
	 * @throws RestJSInvalidArgumentException
	 */
	private void extractParameters(Class<?>[] paramTypes,
			Annotation[][] paramAnnon, List<String> queryParams,
			List<String> formParams, List<String> pathParams,
			List<String> paramNames) throws RestJSInvalidArgumentException {
		boolean hasNonAnnoted = false;

		// If an Annotated parameter is found, continue outer loop (annonLoop)
		annonLoop: for (int i = 0; i < paramAnnon.length; i++) {
			Annotation[] a = paramAnnon[i];
			for (Annotation an : a) {
				if (an.annotationType().equals(QueryParam.class)) {
					QueryParam qp = (QueryParam) an;
					queryParams.add(qp.value());
					paramNames.add(qp.value());
					continue annonLoop;
				}

				if (an.annotationType().equals(PathParam.class)) {
					PathParam pp = (PathParam) an;
					pathParams.add(pp.value());
					paramNames.add(pp.value());
					continue annonLoop;
				}

				if (an.annotationType().equals(FormParam.class)) {
					FormParam fp = (FormParam) an;
					formParams.add(fp.value());
					paramNames.add(fp.value());
					continue annonLoop;
				}
			}

			hasNonAnnoted = true;
			// No annotated parameters, treat as form parameter
			String object = null;
			// Remove [] from array parameters
			if (paramTypes[i].isArray()) {
				object = "obj_".concat(paramTypes[i].getSimpleName()
						.replaceAll("\\[\\]", ""));
			} else {
				object = "obj_".concat(paramTypes[i].getSimpleName());
			}

			formParams.add(object);
			paramNames.add(object);
		}

		// Non annotated parameter and form parameters are not allowed mutually
		if (hasNonAnnoted && formParams.size() > 1) {
			log.debug("Non annotated and form parameters found: "
					+ formParams.toString());
			throw new RestJSInvalidArgumentException(
					"Non annotated parameter and form parameters are not allowed mutually");
		}
	}
}
