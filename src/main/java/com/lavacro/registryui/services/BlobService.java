package com.lavacro.registryui.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

@Service
@Slf4j
public class BlobService extends RegistryService {
	public InputStream download(final String image, final String digest) {
		try {
			URI uri = new URI(String.format("%s/v2/%s/blobs/%s", HOST, image, digest));
			URLConnection connection = uri.toURL().openConnection();
			connection.addRequestProperty("Accept", ACCEPT_HEADER);
			connection.setDoOutput(true);

			return connection.getInputStream();
		} catch(Exception e) {
			log.error("Blob error: {}", e.getMessage());
			return null;
		}
	}
}
