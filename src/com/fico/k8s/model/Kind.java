package com.fico.k8s.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "kind", "apiVersion" })
public class Kind {

	@JsonProperty("kind")
	private String kind;
	@JsonProperty("apiVersion")
	private String apiVersion ;

	@JsonProperty("kind")
	public String getKind() {
		return kind;
	}

	@JsonProperty("kind")
	public void setKind(String kind) {
		this.kind = kind;
	}

	@JsonProperty("apiVersion")
	public String getApiVersion() {
		return apiVersion;
	}

	@JsonProperty("apiVersion")
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
}
