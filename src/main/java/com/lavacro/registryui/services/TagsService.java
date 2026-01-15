package com.lavacro.registryui.services;

import com.lavacro.registryui.model.GenericResponse;
import com.lavacro.registryui.model.Tags;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagsService extends RegistryService {
	private final TagsInterface tagsInterface;

	TagsService(TagsInterface tagsInterface) {
		this.tagsInterface = tagsInterface;
	}

	/**
	 * Retrieves all the tags for a given image
	 * Images are normally versioned. If not versioned, images will default to a "latest" version. If a subsequent
	 * build is uploaded as the new "latest", container orchestrators such as Kubernetes will not realize that it
	 * is different than previously, and a pull policy of "always" would be required to effectuate the change. The
	 * registry API responds with a JSON object that contains a "tags" attribute, which is a list of the tags:
	 * {"name":"finances","tags":["1.8.1"]}
	 * 	The format of the request is - http://myregistry:port/v2/{reponame}/tags/list and pagination has the standard
	 * 	?n={number} appended to the URL.
	 *
	 * @param image A string representing the image (repo) name
	 * @return A Tags object with a list of strings as a member field
	 */
	public Tags tags(final String image) {
		log.info("image: {}", image);

		List<String> tagsList = new ArrayList<>();

		Tags tags = new Tags();
		tags.setName(image);

		try {
			ResponseEntity<Tags> firstTag = tagsInterface.getTags(ACCEPT_HEADER, image);

			HttpHeaders headers = firstTag.getHeaders();
			String linkHeader = headers.getFirst("Link");

			Tags oneTag = firstTag.getBody();
			if(oneTag != null && oneTag.getTags() != null) {
				tagsList.addAll(oneTag.getTags());
			}

			while(linkHeader != null) {
				ResponseEntity<Tags> moreTags = tagsInterface.getTagsCont(ACCEPT_HEADER, linkHeader);
				headers = moreTags.getHeaders();
				linkHeader = headers.getFirst("Link");
				tagsList.addAll(moreTags.getBody().getTags());
			}

			tags.setCode(0);
			tags.setTags(tagsList.stream().sorted().collect(Collectors.toList())); // results may not be sorted, so sort
		} catch(FeignException fe) {
			log.error("{}", fe.getMessage());
			tags.setCode(1);
			tags.setMessage(fe.getMessage());
		}
		return tags;
	}

	/**
	 * Deletes a tag from a repository
	 *
	 * @param image Name of the image/repository
	 * @param tag Version of the tag to delete
	 * @return An object with a success/fail response
	 */
	public GenericResponse deleteTag(final String image, final String tag) {
		GenericResponse response = new GenericResponse();
		String sha = manifest(image, tag);
		assert sha != null;

		String endpoint = String.format("%s/v2/%s/manifests/%s", HOST, image, sha);
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.ACCEPT, ACCEPT_HEADER);
		HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(reqHeaders);
		try {
			ResponseEntity<String> result = new RestTemplate()
					.exchange(endpoint, HttpMethod.DELETE, httpEntity, String.class);
			if(result.getStatusCode() == HttpStatus.ACCEPTED) {
				response.setCode(0);
				response.setMessage("ok");
			} else {
				response.setCode(1);
				response.setMessage(String.format("Response code: %s", result.getStatusCode()));
			}
		} catch(Exception e) {
			response.setCode(1);
			response.setMessage(e.getMessage());
			log.error(e.getMessage());
		}
		return response;
	}
}

@FeignClient(value = "tags-api", url = "${registry.host}")
interface TagsInterface {

	@GetMapping(value = "/v2/{image}/tags/list")
	ResponseEntity<Tags> getTags(
			@RequestHeader(value = HttpHeaders.ACCEPT) final String acceptHeader,
			@PathVariable(value = "image") final String image
	);

	@GetMapping(value = "/{link}")
	ResponseEntity<Tags> getTagsCont(
			@RequestHeader(value = HttpHeaders.ACCEPT) final String acceptHeader,
			@PathVariable(value = "link") final String link
	);

/*
	@GetMapping(value = "/repos/{owner}/{repo}/git/blobs/{sha}")
	GitBlob getBlob(
			@RequestHeader("Authorization") final String auth,
			@RequestHeader(value = "X-GitHub-Api-Version", defaultValue = API_VERSION, required = false) final String apiVersion,
			@PathVariable(value = "owner") final String owner,
			@PathVariable(value = "repo") final String repo,
			@PathVariable(value = "sha") final String sha
	);*/
}
