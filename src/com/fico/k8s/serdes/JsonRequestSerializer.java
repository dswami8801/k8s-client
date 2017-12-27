package com.fico.k8s.serdes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fico.k8s.model.ConfigMap;

public class JsonRequestSerializer implements RequestSerializer<Object> {

	private static ObjectMapper objectMapper = new ObjectMapper();
	private static String emptyBody = "{}";
	
	static{
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Override
	public String serialize(Object body) {

		try {
			System.out.println(objectMapper.writeValueAsString(body));
			return objectMapper.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return emptyBody;
	}

}
