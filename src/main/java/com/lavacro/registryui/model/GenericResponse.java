package com.lavacro.registryui.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponse {
	private Integer code;
	private String message;

	@Override
	public String toString() {
		return "GenericResponse{" +
				"code=" + code +
				", message='" + message + '\'' +
				'}';
	}
}
