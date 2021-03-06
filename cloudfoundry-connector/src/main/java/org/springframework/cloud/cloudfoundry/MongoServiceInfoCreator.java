package org.springframework.cloud.cloudfoundry;

import java.util.Map;

import org.springframework.cloud.service.common.MongoServiceInfo;

/**
 * 
 * @author Ramnivas Laddad
 *
 */
public class MongoServiceInfoCreator extends CloudFoundryServiceInfoCreator<MongoServiceInfo> {

	public MongoServiceInfoCreator() {
		super("mongodb");

	}

	public MongoServiceInfo createServiceInfo(Object serviceData) {
		@SuppressWarnings("unchecked")
		Map<String,Object> serviceDataMap = (Map<String,Object>) serviceData;

		@SuppressWarnings("unchecked")
		Map<String,Object> credentials = (Map<String, Object>) serviceDataMap.get("credentials");
		
		String id = (String) serviceDataMap.get("name");
		
		String uri = (String) credentials.get("uri");

		return new MongoServiceInfo(id, uri);
	}

}
