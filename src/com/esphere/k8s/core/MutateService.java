package com.esphere.k8s.core;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.text.StrSubstitutor;

import com.esphere.k8s.core.http.Body;
import com.esphere.k8s.core.http.DELETE;
import com.esphere.k8s.core.http.GET;
import com.esphere.k8s.core.http.HEAD;
import com.esphere.k8s.core.http.HTTP;
import com.esphere.k8s.core.http.OPTIONS;
import com.esphere.k8s.core.http.POST;
import com.esphere.k8s.core.http.PUT;
import com.esphere.k8s.core.http.Path;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

public class MutateService {

	static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
	static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
	static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

	private final HttpUrl baseUrl;
	private final String httpMethod;
	private final String relativeUrl;
	private final Headers headers;
	private final MediaType contentType;
	private final boolean hasBody;
	private Object body;
	
	

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public HttpUrl getBaseUrl() {
		return baseUrl;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getRelativeUrl() {
		return relativeUrl.replaceFirst("\\/", "");
	}

	public Headers getHeaders() {
		return headers;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public boolean isHasBody() {
		return hasBody;
	}

	public MutateService(Builder builder) {
		this.baseUrl = builder.mutator.baseUrl();
		this.httpMethod = builder.httpMethod;
		this.relativeUrl = builder.relativeUrl;
		this.headers = builder.headers;
		this.contentType = builder.contentType;
		this.hasBody = builder.hasBody;
		this.body = builder.body;
		System.err.println(builder.relativeUrl);
	}

	static class Builder {

		final Mutator mutator;
		final Method method;
		final Annotation[] methodAnnotations;
		final Annotation[][] parameterAnnotationsArray;
		final Type[] parameterTypes;
		final Object[] args;

		Type responseType;
		Object body;
		boolean gotField;
		boolean gotPart;
		boolean gotBody;
		boolean gotPath;
		boolean gotQuery;
		boolean gotUrl;
		String httpMethod;
		boolean hasBody;

		private String relativeUrl;
		Headers headers;
		MediaType contentType;
		Set<String> relativeUrlParamNames;
		
		

		public String getRelativeUrl() {
			return relativeUrl;
		}

		public void setRelativeUrl(String relativeUrl) {
			this.relativeUrl = relativeUrl;
		}

		public Builder(Method method, Mutator mutator) {
			this.mutator = mutator;
			this.method = method;
			this.methodAnnotations = method.getAnnotations();
			this.parameterTypes = method.getGenericParameterTypes();
			this.parameterAnnotationsArray = method.getParameterAnnotations();
			this.args = mutator.getArgs();
			System.out.println(parameterTypes[0]);
			System.out.println(parameterAnnotationsArray[0][0]);
		}

		public MutateService build() {

			for (Annotation annotation : methodAnnotations) {
				parseMethodAnnotation(annotation);
			}

			if (httpMethod == null) {
				throw methodError("HTTP method annotation is required (e.g., @GET, @POST, etc.).");
			}
			if (relativeUrl == null && !gotUrl) {
				throw methodError("Missing either @%s URL or @Url parameter.", httpMethod);
			}
			System.out.println(headers);
			return new MutateService(this);
		}

		private void parseMethodAnnotation(Annotation annotation) {
			if (annotation instanceof DELETE) {
				parseHttpMethodAndPath("DELETE", ((DELETE) annotation).value(), false);
			} else if (annotation instanceof GET) {
				parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
			} else if (annotation instanceof HEAD) {
				parseHttpMethodAndPath("HEAD", ((HEAD) annotation).value(), false);
				if (!Void.class.equals(responseType)) {
					throw methodError("HEAD method must use Void as response type.");
				}
			} else if (annotation instanceof POST) {
				parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
			} else if (annotation instanceof PUT) {
				parseHttpMethodAndPath("PUT", ((PUT) annotation).value(), true);
			} else if (annotation instanceof OPTIONS) {
				parseHttpMethodAndPath("OPTIONS", ((OPTIONS) annotation).value(), false);
			} else if (annotation instanceof HTTP) {
				HTTP http = (HTTP) annotation;
				parseHttpMethodAndPath(http.method(), http.path(), http.hasBody());
			} else if (annotation instanceof com.esphere.k8s.core.http.Headers) {
				String[] headersToParse = ((com.esphere.k8s.core.http.Headers) annotation).value();
				if (headersToParse.length == 0) {
					throw methodError("@Headers annotation is empty.");
				}
				headers = parseHeaders(headersToParse);
			}

		}

		private Headers parseHeaders(String[] headers) {
			Headers.Builder builder = new Headers.Builder();
			for (String header : headers) {
				int colon = header.indexOf(':');
				if (colon == -1 || colon == 0 || colon == header.length() - 1) {
					throw methodError("@Headers value must be in the form \"Name: Value\". Found: \"%s\"", header);
				}
				String headerName = header.substring(0, colon);
				String headerValue = header.substring(colon + 1).trim();
				if ("Content-Type".equalsIgnoreCase(headerName)) {
					MediaType type = MediaType.parse(headerValue);
					if (type == null) {
						throw methodError("Malformed content type: %s", headerValue);
					}
					contentType = type;
				} else {
					builder.add(headerName, headerValue);
				}
			}
			return builder.build();
		}

		private void parseHttpMethodAndPath(String string, String value, boolean b) {
			if (this.httpMethod != null) {
				throw methodError("Only one HTTP method is allowed. Found: %s and %s.", this.httpMethod, httpMethod);
			}
			this.httpMethod = string;
			this.hasBody = b;

			if (value.isEmpty()) {
				return;
			}

			// Get the relative URL path and existing query string, if present.
			int question = value.indexOf('?');
			if (question != -1 && question < value.length() - 1) {
				// Ensure the query string does not have any named parameters.
				String queryParams = value.substring(question + 1);
				Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
				if (queryParamMatcher.find()) {
					throw methodError("URL query string \"%s\" must not have replace block. "
							+ "For dynamic query parameters use @Query.", queryParams);
				}
			}

			this.relativeUrl = value;
			this.relativeUrlParamNames = parsePathParameters(value);
			resolveUrl(relativeUrl);
			bodyArgumentResolver();

		}

		private void resolveUrl(String relativeUrl) {

			Map<String, String> params = new HashMap<>();
			for (int i = 0; i < parameterAnnotationsArray.length; i++) {
				for (int j = 0; j < parameterAnnotationsArray[i].length; j++) {
					Annotation annotation = parameterAnnotationsArray[i][j];
					if (annotation instanceof Path) {
						params.put(((Path) annotation).value(), args[i].toString());
					}
				}
			}
			relativeUrl = StrSubstitutor.replace(relativeUrl, params,"{","}");
			setRelativeUrl(relativeUrl);
		}

		private void bodyArgumentResolver() {

			for (int i = 0; i < parameterAnnotationsArray.length; i++) {
				for (int j = 0; j < parameterAnnotationsArray[i].length; j++) {
					Annotation annotation = parameterAnnotationsArray[i][j];
					if (annotation instanceof Body) {
						body = args[i];
						gotBody = true;
						hasBody = true;
					}
				}
			}

		}

		static Set<String> parsePathParameters(String path) {
			Matcher m = PARAM_URL_REGEX.matcher(path);
			Set<String> patterns = new LinkedHashSet<>();
			while (m.find()) {
				patterns.add(m.group(1));
			}
			return patterns;
		}

		private RuntimeException methodError(String message, Object... args) {
			return methodError(null, message, args);
		}

		private RuntimeException methodError(Throwable cause, String message, Object... args) {
			message = String.format(message, args);
			return new IllegalArgumentException(
					message + "\n    for method " + method.getDeclaringClass().getSimpleName() + "." + method.getName(),
					cause);
		}

	}

}
