package org.springframework.cloud.cloudfoundry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.AbstractCloudConnector;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.ServiceInfoCreator;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.service.ServiceInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Ramnivas Laddad
 *
 */
public class CloudFoundryConnector extends AbstractCloudConnector {

	private ObjectMapper objectMapper = new ObjectMapper();
	private EnvironmentAccessor environment = new EnvironmentAccessor();
	
	private ApplicationInstanceInfoCreator applicationInstanceInfoCreator = new ApplicationInstanceInfoCreator();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CloudFoundryConnector() {
		super((Class) CloudFoundryServiceInfoCreator.class);
	}

	@Override
	public boolean isInMatchingCloud() {
		return environment.getValue("VCAP_APPLICATION") != null;
	}
	
	@Override
	public ApplicationInstanceInfo getApplicationInstanceInfo() {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> rawApplicationInstanceInfo = objectMapper.readValue(environment.getValue("VCAP_APPLICATION"), Map.class);
			return applicationInstanceInfoCreator.createApplicationInstanceInfo(rawApplicationInstanceInfo);
		} catch (Exception e) {
			throw new CloudException(e);
		} 
	}
	
	@Override
	public List<ServiceInfo> getServiceInfos() {
		List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
		for (Map<String,Object> serviceData : getServicesData()) {
			serviceInfos.add(getServiceInfo(serviceData));
		}
		
		return serviceInfos;
	}

	private ServiceInfo getServiceInfo(Map<String,Object> serviceData) {
		for (ServiceInfoCreator<?> serviceInfoCreator : serviceInfoCreators) {
			if (serviceInfoCreator.accept(serviceData)) {
				return serviceInfoCreator.createServiceInfo(serviceData);
			}
		}
		
		throw new CloudException("No suitable service info creator found");
	}

	
	/* package for testing purpose */
	void setCloudEnvironment(EnvironmentAccessor environment) {
		this.environment = environment;
	}

	
	/**
	 * Return object representation of the VCAP_SERIVCES environment variable
	 * <p>
	 * Returns a list whose element is a map with service attributes. 
	 * </p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> getServicesData() {
		String servicesString = environment.getValue("VCAP_SERVICES");
		Map<String, List<Map<String,Object>>> rawServices = new HashMap<String, List<Map<String,Object>>>();
		
		if (servicesString == null || servicesString.length() == 0) {
			rawServices = new HashMap<String, List<Map<String,Object>>>();
		}
		try {
			rawServices = objectMapper.readValue(servicesString, Map.class);
		} catch (Exception e) {
			throw new CloudException(e);
		} 
		
		List<Map<String,Object>> flatServices = new ArrayList<Map<String,Object>>();
		for (Map.Entry<String, List<Map<String,Object>>> entry : rawServices.entrySet()) {
			flatServices.addAll(entry.getValue());
		}
		return flatServices;
	}
	
	/**
	 * Environment available to the deployed app.
	 * 
	 * The main purpose of this class is to allow unit-testing of {@link CloudFoundryConnector}
	 *
	 */
	public static class EnvironmentAccessor {
		public String getValue(String key) {
			return System.getenv(key);
		}
	}
}
