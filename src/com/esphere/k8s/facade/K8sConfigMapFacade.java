package com.esphere.k8s.facade;

import com.esphere.k8s.core.http.Body;
import com.esphere.k8s.core.http.GET;
import com.esphere.k8s.core.http.Headers;
import com.esphere.k8s.core.http.POST;
import com.esphere.k8s.core.http.Path;

import okhttp3.Call;

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
