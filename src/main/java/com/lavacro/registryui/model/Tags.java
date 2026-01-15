package com.lavacro.registryui.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class Tags extends GenericResponse {
	private String name;

	private List<String> tags;

	public void setTags(List<String> tags) {
		this.tags = tags.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Tags{" +
				"name='" + name + '\'' +
				", tags=" + tags +
				'}';
	}
}
