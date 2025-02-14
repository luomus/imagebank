function toggleTree() {
	$("#browse-tree").toggle();
	$("#browse-taxa").toggleClass("expanded");
}

$(document).ready(function() {

	$("#browse-tree-header").click(function() {
		toggleTree();
		setPreference("showTree", $("#browse-tree").is(":visible"));
	});
	
	if (!getBooleanPreference("showTree")) {
		toggleTree();
	}
});