package com.lavacro.registryui.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RegistryService {
	private static final String DOCKER_HEADER = "application/vnd.docker.distribution.manifest.v2+json";
	private static final String OCI_HEADER = "application/vnd.oci.image.manifest.v1+json";
	protected static final String ACCEPT_HEADER = String.format("%s,%s", DOCKER_HEADER, OCI_HEADER);
	protected static final String RESP_HEADER = "Docker-Content-Digest";

	@Value("${registry.host}")
	protected String HOST;

	/**
	 *
	 * The format of the request is - http://myregistry:port/v2/manifests/{reponame}
	 *
	 * @param image A string representing the image (repo) name
	 * @param tag A string of the version of the repo
	 * @return The sha256 header which identifies the image+tag
	 */
	protected String manifest(final String image, final String tag) {
		String endpoint = String.format("%s/v2/%s/manifests/%s", HOST, image, tag);
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add("Accept", ACCEPT_HEADER);
		reqHeaders.add("Accept", OCI_HEADER);
		HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(reqHeaders);
		try {
			ResponseEntity<String> result = new RestTemplate()
					.exchange(endpoint, HttpMethod.HEAD, httpEntity, String.class);
			HttpHeaders respHeaders = result.getHeaders();
			List<String> headerList = respHeaders.get(RESP_HEADER);
			if(headerList == null || headerList.isEmpty()) {
				return null;
			}
			return headerList.get(0);
		} catch(Exception e) {
			return e.getMessage();
		}
	}

	protected String getLink(final ResponseEntity result) {
		List<String> link = result.getHeaders().get("Link");
		if(link == null || link.isEmpty()) {
			return null;
		}
		String next = link.get(0);
		// link looks like: </v2/_catalog?last=berean&n=2>; rel="next"
		if(next.charAt(0) == '<' && next.indexOf('>') > 0) {
			return next.substring(1, next.indexOf('>'));
		} else {
			return null;
		}
	}
}
