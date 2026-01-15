package com.lavacro.registryui.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Repositories extends GenericResponse {
	private List<String> repositories;
}
