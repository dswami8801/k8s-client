package com.fico.k8s.service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fico.k8s.core.Mutator;
import com.fico.k8s.facade.K8sConfigMapFacade;
import com.fico.k8s.model.ConfigMap;

import okhttp3.Response;

public class K8SConfigMapService {

	private String baseUrl;

	public K8SConfigMapService(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Response createConfig() {
		Mutator mutator = new Mutator.Builder().baseUrl(baseUrl).build();
		K8sConfigMapFacade configMapFacade = mutator.mutate(K8sConfigMapFacade.class);
		Response response = null;
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("port", "5000");
			response = configMapFacade.create("default", new ConfigMap("test2", map)).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public Response getConfig() {
		Mutator mutator = new Mutator.Builder().baseUrl(baseUrl).build();
		K8sConfigMapFacade configMapFacade = mutator.mutate(K8sConfigMapFacade.class);
		Response response = null;
		try {
			response = configMapFacade.get("default", "dummy21").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(new K8SConfigMapService("http://localhost:8000").createConfig().code());
	}

}
