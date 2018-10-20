package com.lxgy.analysis.track.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Gryant
 */
@Configuration
@ConfigurationProperties(prefix="tracker.sdk")
@PropertySource(value= "classpath:/sdk.properties")
public class SdkConfig {

	private String accessUrl;
	private String platformName;
	private String sdkName;
	private String version;

	public String getAccessUrl() {
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getSdkName() {
		return sdkName;
	}

	public void setSdkName(String sdkName) {
		this.sdkName = sdkName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public SdkConfig() {
	}


	public SdkConfig(String accessUrl, String platformName, String sdkName, String version) {
		this.accessUrl = accessUrl;
		this.platformName = platformName;
		this.sdkName = sdkName;
		this.version = version;
	}
}
