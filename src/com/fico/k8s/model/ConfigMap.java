package com.fico.k8s.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "kind", "apiVersion", "metadata", "data" })
public class ConfigMap extends Kind {

	public ConfigMap(String name, Object data) {
		this.metadata = new Metadata();
		this.metadata.setName(name);
		this.data = data;
	}

	@JsonProperty("metadata")
	private Metadata metadata;
	@JsonProperty("data")
	private Object data;

	@JsonProperty("metadata")
	public Metadata getMetadata() {
		return metadata;
	}

	@JsonProperty("metadata")
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@JsonProperty("data")
	public Object getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ConfigMap [metadata=" + metadata + ", data=" + data + ", getKind()=" + getKind() + ", getApiVersion()="
				+ getApiVersion() + "]";
	}

}