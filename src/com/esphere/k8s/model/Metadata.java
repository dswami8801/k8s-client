package com.esphere.k8s.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "namespace", "selfLink", "uid", "resourceVersion", "creationTimestamp" })
public class Metadata {

	@JsonProperty("name")
	private String name;
	@JsonProperty("namespace")
	private String namespace;
	@JsonProperty("selfLink")
	private String selfLink;
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("resourceVersion")
	private String resourceVersion;
	@JsonProperty("creationTimestamp")
	private String creationTimestamp;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("namespace")
	public String getNamespace() {
		return namespace;
	}

	@JsonProperty("namespace")
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@JsonProperty("selfLink")
	public String getSelfLink() {
		return selfLink;
	}

	@JsonProperty("selfLink")
	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}

	@JsonProperty("uid")
	public String getUid() {
		return uid;
	}

	@JsonProperty("uid")
	public void setUid(String uid) {
		this.uid = uid;
	}

	@JsonProperty("resourceVersion")
	public String getResourceVersion() {
		return resourceVersion;
	}

	@JsonProperty("resourceVersion")
	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	@JsonProperty("creationTimestamp")
	public String getCreationTimestamp() {
		return creationTimestamp;
	}

	@JsonProperty("creationTimestamp")
	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	@Override
	public String toString() {
		return "Metadata [name=" + name + ", namespace=" + namespace + ", selfLink=" + selfLink + ", uid=" + uid
				+ ", resourceVersion=" + resourceVersion + ", creationTimestamp=" + creationTimestamp + "]";
	}

}