package com.lavacro.registryui.controllers;

import com.lavacro.registryui.services.CatalogService;
import com.lavacro.registryui.services.TagsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SuppressWarnings("unused")
public class RegistryWeb {
	private static final Logger logger = LoggerFactory.getLogger(RegistryWeb.class);

	private final CatalogService catalogService;
	private final TagsService tagsService;

	public RegistryWeb(CatalogService catalogService, TagsService tagsService) {
		this.catalogService = catalogService;
		this.tagsService = tagsService;
	}

	@GetMapping(value = "/")
	public String spa(Model model) {
		List<String> repos = catalogService.catalog().getRepositories();
		model.addAttribute("repos", repos);

		Map<String, List<String>> tags = new HashMap<>();
		repos.forEach( it -> tags.put(it, tagsService.tags(it).getTags()) );
		model.addAttribute("tags", tags);
		return "home";
	}
}
