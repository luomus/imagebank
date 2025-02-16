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
        url: "${baseURL}/api/tree/${groupId}?rank=" + rank + "&order=" + order + "&taxa=" + taxa,
        type: "GET",
        success: function(response) {
			const container = $("#browse-tree-content");
   			container.html(response);
    		const items = container.find("ol").find("li");
			if (items.length > 50) {
        		items.slice(50).hide();
				const showMoreBtn = $('<a href="#">')
					.text("${text.show_more}")
					.addClass("showMore")
					.on("click", function (e) {
						e.preventDefault();
						items.show();
						$(this).remove();
						return false;
					});
		        container.append(showMoreBtn);
    		}
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
	
	loadTree();
	
	$("#browse-panel").show();
		
});