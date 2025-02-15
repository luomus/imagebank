function toggleTree() {
	$("#browse-tree").toggle();
	$("#browse-taxa").toggleClass("expanded");
	$(".openclose").toggleClass("hidden");
}

$(document).ready(function() {

	$("#browse-tree-header").click(function() {
		toggleTree();
		setPreference("showTree", $("#browse-tree").is(":visible"));
	});
	
	if (!getBooleanPreference("showTree")) {
		toggleTree();
	}
	
	$("#browse-tree-select").change(function() {
		setPreference("browse-ranks", $(this).val());
	});
	
	if (getPreference("browse-ranks")) {
		$("#browse-tree-select").val(getPreference("browse-ranks"));
	}
	
	$("#browse-panel").show();
	
});