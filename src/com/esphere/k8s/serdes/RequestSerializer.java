package com.esphere.k8s.serdes;

public interface RequestSerializer<T> {

	public String serialize(T body);
}
