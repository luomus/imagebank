function toggleTree() {
	$("#browse-tree").toggle();
	$("#browse-taxa").toggleClass("expanded");
	$(".openclose").toggleClass("hidden");
}

function loadTree() {
	const rank = $("#browse-tree-select").val();
	const order = getPreference("order") || "order_taxonomic";
	const taxa = getPreference("taxa") || "taxa_finnish";
	$.ajax({
        url: "${baseURL}/api/tree/${groupId?html}?rank=" + rank + "&order=" + order + "&taxa=" + taxa,
        type: "GET",
        success: function(response) {
			const container = $("#browse-tree-content");
   			container.html(response);
        },
        error: function() {
            // Refresh the page to display the failure reason via flash message
			window.scrollTo(0, 0);
        	window.location.reload();
        }
    });
}

function loadSpecies(page = 1) {
	const order = getPreference("order") || "order_taxonomic";
	const taxa = getPreference("taxa") || "taxa_finnish";
	const taxonRanks = getPreference("taxonRanks") || [];
	const pageSize = getPreference("pageSize") || "100";
	$.ajax({
        url: "${baseURL}/api/species/${groupId?html}?page=" + page + "&order=" + order + "&taxa=" + taxa + "&taxonRanks=" + taxonRanks + "&pageSize=" + pageSize,
        type: "GET",
        success: function(response) {
			const container = $("#browse-taxa");
   			container.html(response);
        },
        error: function() {
            // Refresh the page to display the failure reason via flash message
			window.scrollTo(0, 0);
        	window.location.reload();
        }
    });
}

preferenceChangeHook = function(preference, value) {
    if (preference === "taxa" || preference === "order") {
        loadTree();
        loadSpecies();
    }
    if (preference === "taxonRanks" || preference === "pageSize") {
    	loadSpecies();
    }
};

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
		loadTree();
	});
	
	if (getPreference("browse-ranks")) {
		$("#browse-tree-select").val(getPreference("browse-ranks"));
	}
	
	setPreference("group", "${groupId?html}");
		
	loadTree();
	loadSpecies();
	
	$("#browse-panel").show();
		
});