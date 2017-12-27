package com.esphere.k8s.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;

import com.esphere.k8s.serdes.JsonRequestSerializer;
import com.esphere.k8s.serdes.RequestSerializer;

import okhttp3.Call.Factory;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Mutator {
	private HttpUrl baseUrl;
	private okhttp3.Call.Factory callFactory;
	private Object[] args;
	private RequestSerializer requestSerializer;

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Mutator(HttpUrl baseUrl, Factory callFactory,
			RequestSerializer requestSerializer) {
		this.baseUrl = baseUrl;
		this.callFactory = callFactory;
		this.requestSerializer = requestSerializer;
	}

	public HttpUrl baseUrl() {
		return baseUrl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T mutate(Class clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
				new Class[] { clazz }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						setArgs(args);
						if (method.getDeclaringClass() == Object.class) {
							return method.invoke(this, args);
						}

						MutateService serviceMethod = loadServiceMethod(method);

						return buildCall(serviceMethod);
					}

				});
	}

	private Object buildCall(MutateService serviceMethod) {
		System.out.println("===" + serviceMethod.getBody());
		RequestBody body = null;
		if (serviceMethod.isHasBody()) {
			body = RequestBody.create(
					MediaType.parse("application/json; charset=utf-8"),
					requestSerializer.serialize(serviceMethod.getBody()));
		}

		Request request = new Request.Builder()
				.url(baseUrl.toString() + serviceMethod.getRelativeUrl())
				.method(serviceMethod.getHttpMethod(), body)
				.headers(serviceMethod.getHeaders()).build();

		if (serviceMethod.isHasBody()) {

		}

		OkHttpClient client = new OkHttpClient.Builder().build();
		return client.newCall(request);
	}

	MutateService loadServiceMethod(Method method) {

		return new MutateService.Builder(method, this).build();

	}

	public static final class Builder {

		private okhttp3.Call.Factory callFactory;
		private HttpUrl baseUrl;
		private RequestSerializer requestSerializer = new JsonRequestSerializer();

		public Builder() {

		}

		public Builder baseUrl(String baseUrl) {
			Objects.requireNonNull(baseUrl, "baseUrl == null");
			HttpUrl httpUrl = HttpUrl.parse(baseUrl);
			if (httpUrl == null) {
				throw new IllegalArgumentException("Illegal URL: " + baseUrl);
			}
			return baseUrl(httpUrl);
		}

		public Builder baseUrl(HttpUrl baseUrl) {
			Objects.requireNonNull(baseUrl, "baseUrl == null");
			List<String> pathSegments = baseUrl.pathSegments();
			if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
				throw new IllegalArgumentException("baseUrl must end in /: "
						+ baseUrl);
			}
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder requestSerializer(RequestSerializer requestSerializer) {
			this.requestSerializer = requestSerializer;
			return this;
		}

		public Mutator build() {
			if (baseUrl == null) {
				throw new IllegalStateException("Base URL required.");
			}

			okhttp3.Call.Factory callFactory = this.callFactory;
			if (callFactory == null) {
				callFactory = new OkHttpClient();
			}

			return new Mutator(baseUrl, callFactory, requestSerializer);
		}

	}

}
