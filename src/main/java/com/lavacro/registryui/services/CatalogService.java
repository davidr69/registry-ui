package com.lavacro.registryui.services;

import com.lavacro.registryui.model.Repositories;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CatalogService extends RegistryService {
	/**
	 * Obtain a list of the repository names in the registry
	 * The registry responds with a string in this format:
	 * {"repositories":["adoptopenjdk","berean","finances","songlist"]}
	 * It is simply a list of strings with no details. For actual versions of images within a given
	 * repository, the @tags method must be invokved.
	 * The format of the request is - <a href="http://myregistry:port/v2/_catalog?n=">...</a>{page_size}
	 * @return A Repository object with a list of strings as a member field
	 */
	public Repositories catalog() {
		Repositories repositories = new Repositories();
		String endpoint = String.format("%s/v2/_catalog", HOST);
		boolean needMore = true;
		List<String> repoList = new ArrayList<>();
		try {
			while(needMore) {
				ResponseEntity<Repositories> result = new RestTemplate()
						.exchange(endpoint, HttpMethod.GET, null, Repositories.class);
				repositories = result.getBody();
				assert repositories != null;
				repoList.addAll(repositories.getRepositories());

				// although we request all repos, the server may decide to send a partial list
				String link = getLink(result);
				if (link == null) {
					needMore = false;
				} else {
					endpoint = String.format("%s%s", HOST, link);
				}
			}
			repositories.setCode(0);
			repositories.setRepositories(repoList.stream().sorted().collect(Collectors.toList()));
		} catch(Exception e) {
			repositories = new Repositories();
			repositories.setCode(1);
			repositories.setMessage(e.getMessage());
		}
		return repositories;
	}
}
