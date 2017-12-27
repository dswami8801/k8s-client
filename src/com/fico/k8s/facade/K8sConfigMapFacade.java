package com.fico.k8s.facade;

import okhttp3.Call;

import com.fico.k8s.core.http.Body;
import com.fico.k8s.core.http.GET;
import com.fico.k8s.core.http.Headers;
import com.fico.k8s.core.http.POST;
import com.fico.k8s.core.http.Path;

public interface K8sConfigMapFacade {

	@Headers({ "Accept: application/json" })
	@POST(value = "/api/v1/namespaces/{namespace}/configmaps")
	public Call create(@Path("namespace") String namespace,
			@Body Object configMap);

	@Headers({ "Accept: application/json" })
	@GET(value = "/api/v1/namespaces/{namespace}/configmaps/{configmap}")
	public Call get(@Path("namespace") String namespace,
			@Path("configmap") String configmap);

}
