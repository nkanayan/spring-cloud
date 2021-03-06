package org.springframework.cloud.service;

import org.springframework.cloud.util.UriInfo;

/**
 * Common class for all {@link ServiceInfo}s
 * 
 * @author Ramnivas Laddad
 *
 */
public abstract class BaseServiceInfo implements ServiceInfo {
	private String id;
	private UriInfo uriInfo; 

	public BaseServiceInfo(String id, String scheme, String host, int port, String username, String password, String path) {
		this(id, new UriInfo(scheme, host, port, username, password, path));
	}
	
	public BaseServiceInfo(String id, String uriString) {
		this(id, new UriInfo(uriString));
	}
	
	private BaseServiceInfo(String id, UriInfo uriInfo) {
		this.id = id;
		this.uriInfo = validateAndCleanUriInfo(uriInfo);
	}
	
	@ServiceProperty
	public String getId() {
		return id;
	}
	
	@ServiceProperty(category="connection")
	public String getUri() {
		return uriInfo.getUri().toString();
	}
	
	@ServiceProperty(category="connection")
	public String getUserName() {
		return uriInfo.getUserName();
	}
	
	@ServiceProperty(category="connection")
	public String getPassword() {
		return uriInfo.getPassword();
	}

	@ServiceProperty(category="connection")
	public String getHost() {
		return uriInfo.getHost();
	}

	@ServiceProperty(category="connection")
	public int getPort() {
		return uriInfo.getPort();
	}
	
	/**
	 * Validate the URI and clean it up by using defaults for any missing information, if possible.
	 * 
	 * @param uriInfo
	 * @return
	 */
	protected UriInfo validateAndCleanUriInfo(UriInfo uriInfo) {
		return uriInfo;
	}
	
	protected UriInfo getUriInfo() {
		return uriInfo;
	}
}
