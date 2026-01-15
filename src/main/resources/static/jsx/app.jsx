export default class App {
	constructor() {
		//
	}

	init = async() => {
		const resp = await fetch('api/v1/catalog');
		const data = await resp.json();

		console.log(data);
	}
}

createNamespace("registry.app");

registry.app.deleteTags = function(repo) {
	let boxes = Array.from(document.querySelectorAll(`input[name^=${repo}]`)).
		map(function(obj, idx) { return obj.checked ? obj.name : null; }).filter(item => item != null);
	for(let box of boxes) {
		let parts = box.split(':');
		fetch(`api/v1/${repo}/${parts[1]}`, {
			method:	'POST'
		}).then(resp => {
			resp.json().then(data => {
				if(data.code == 0) {
					let node = document.querySelector(`input[name="${repo}:${parts[1]}"]`);
					node.parentElement.parentElement.remove();
				} else {
					alert(resp);
				}
			})
		});
	}
}

registry.app.manifest = async(tag) => {
	let parts = tag.split(':');
	const resp = await fetch(`api/v1/manifest/${parts[0]}/${parts[1]}`);
	const data = await resp.json();

	let td = document.getElementById(`layers_${parts[0]}:${parts[1]}`);
	// if there's anything there, clear it out
	while(td.childNodes.length > 0) {
		td.removeChild(td.childNodes[0]);
	}

	let first = true;
	for(let entry of data.layers) {
		if(first) {
			first = false;
		} else {
			let br = document.createElement('br');
			td.appendChild(br);
		}
		let span = document.createElement('span');
		span.setAttribute('class', 'ref');
		span.setAttribute('onclick', `registry.app.download('${parts[0]}', '${entry.digest}')`);
		let text = document.createTextNode(`size: ${entry.size}, type: ${entry.mediaType}`);
		span.appendChild(text);
		td.appendChild(span);
	}
};

registry.app.download = function(repo, digest) {
	window.location = `api/v1/download/${repo}/${digest}`;
}
