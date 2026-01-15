package com.lavacro.registryui.services;

import com.lavacro.registryui.model.Manifest;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@Slf4j
public class ManifestService extends RegistryService {
	private final ManifestInterface manifestInterface;

	ManifestService(ManifestInterface manifestInterface) {
		this.manifestInterface = manifestInterface;
	}

	public Manifest getLayers(final String image, final String tag) {
		Manifest manifest;

		try {
			manifest = manifestInterface.getTags(ACCEPT_HEADER, image, tag);
			manifest.setCode(0);
		} catch(FeignException fe) {
			manifest = new Manifest();
			manifest.setCode(1);
			manifest.setMessage(fe.getMessage());
		}
		return manifest;
	}
}

@FeignClient(value = "manifest-api", url = "${registry.host}")
interface ManifestInterface {

	@GetMapping(value = "/v2/{image}/manifests/{tag}")
	Manifest getTags(
			@RequestHeader(value = HttpHeaders.ACCEPT) final String acceptHeader,
			@PathVariable(value = "image") final String image,
			@PathVariable(value = "tag") final String tag
	);
}
