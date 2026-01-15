package com.lavacro.registryui.controllers.api.v1;

import com.lavacro.registryui.model.GenericResponse;
import com.lavacro.registryui.model.Manifest;
import com.lavacro.registryui.services.BlobService;
import com.lavacro.registryui.services.ManifestService;

import com.lavacro.registryui.services.TagsService;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
@SuppressWarnings("unused")
public class ManifestRestController {
	private static final int BUFFER_SIZE = 8192;

	private final ManifestService manifestService;
	private final TagsService tagsService;
	private final BlobService blobService;

	ManifestRestController(ManifestService manifestService, TagsService tagsService, BlobService blobService) {
		this.manifestService = manifestService;
		this.tagsService = tagsService;
		this.blobService = blobService;
	}

	@DeleteMapping(value = "/{image}/{tag}")
	public ResponseEntity<GenericResponse> deleteImage(
			@PathVariable(value = "image") String image,
			@PathVariable(value = "tag") String tag
	) {
		log.info("<<<<< DELETE >>>>> image: {}, tag: {}", image, tag);
		GenericResponse response = tagsService.deleteTag(image, tag);
		HttpStatus status = response.getCode() == 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(response, null, status);
	}

	@GetMapping(value = "/manifest/{image}/{tag}")
	public ResponseEntity<Manifest> getManifst(
			@PathVariable(value = "image") String image,
			@PathVariable(value = "tag") String tag
	) {
		Manifest manifest = manifestService.getLayers(image, tag);
		HttpStatus status = manifest.getCode() == 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(manifest, null, status);
	}

	@GetMapping(value = "/download/{image}/{digest}")
	public void download(
			HttpServletResponse resp,
			@PathVariable(value = "image") final String image,
			@PathVariable(value = "digest") final String digest
	) {
		try(
			InputStream is = new BufferedInputStream(blobService.download(image, digest));
			OutputStream os = new BufferedOutputStream(resp.getOutputStream())
		) {
			resp.setHeader("Content-Disposition", "attachment;filename=layer.tgz");
			byte[] buffer = new byte[BUFFER_SIZE];
			while(is.read(buffer, 0, BUFFER_SIZE) != -1) {
				os.write(buffer, 0, BUFFER_SIZE);
			}
		} catch(IOException e) {
			log.error("IO exception: {}", e.getMessage());
		}
	}
}
