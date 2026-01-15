function createNamespace(ns) {
	var parent = window;
	ns.split('.').forEach(function(item, idx) {
		parent[item] = {};
		parent = parent[item];
	});
}
