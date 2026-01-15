package com.lavacro.registryui.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Manifest extends GenericResponse {
	private Integer schemaVersion;
	private String mediaType;
	private Layer config;
	private List<Layer> layers;

	@Getter
	@Setter
	public static class Layer {
		private String mediaType;
		private Long size;
		private String digest;
	}
}

/*
{
   "schemaVersion": 2,
   "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
   "config": {
      "mediaType": "application/vnd.docker.container.image.v1+json",
      "size": 8230,
      "digest": "sha256:5d3ea4a1ee733902a2313b369393854218a5013aa4d48700e5aa1adefddb069c"
   },
   "layers": [
      {
         "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
         "size": 28570074,
         "digest": "sha256:35807b77a593c1147d13dc926a91dcc3015616ff7307cc30442c5a8e07546283"
      },
      {
         "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
         "size": 16033698,
         "digest": "sha256:93d71b8f96bb4f1c00b7375be1090b56f03343a56b15fdcdf40d5ac4a207e217"
      },
      {
         "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
         "size": 44109568,
         "digest": "sha256:97f92b4edc8e1e6878f277bebfbd6a69ac98082f9de34d32f3556ce00bca8cc7"
      },
      {
         "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
         "size": 4307305,
         "digest": "sha256:9e50a80ad244c287c0404d522e97a03cb586ad0051a83e6e769723e678fa3996"
      },
      {
         "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
         "size": 41894304,
         "digest": "sha256:6a8436a059134fc3de6e4e6bc09319150cd63f5b45dd7735bc55e29ec52f067e"
      }
   ]
}
 */