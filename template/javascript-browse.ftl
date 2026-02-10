function toggleTree() {
	$("#browse-tree").toggle();
	$("#browse-taxa").toggleClass("expanded");
	$("#browse-tree-header").toggleClass("shrunken");
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

let taxonFilter = null;

function setTaxonFilter(taxonId) {
	taxonFilter = taxonId;
}

function loadSpecies(page = 1) {
	const params = {
        taxonFilter: taxonFilter,
        order: getPreference("order") || "order_taxonomic",
        taxa: getPreference("taxa") || "taxa_finnish",
        taxonRanks: getPreference("taxonRanks") || [],
        page: page,
        pageSize: getPreference("pageSize") || "100"
    };
    
	$.ajax({
        url: "${baseURL}/api/species/${groupId?html}",
        type: "GET",
        data: params,
        success: function(response) {
			const container = $("#browse-taxa");
   			container.html(response);
   			filterImageCategories();
   			defineImageSize();
        },
        error: function() {
            // Refresh the page to display the failure reason via flash message
			window.scrollTo(0, 0);
        	window.location.reload();
        }
    });
}

function filterImageCategories() {
	const categories = [<#list defs as def>'${def.id}',</#list>'uncategorized'];
	for (const category of categories) {
		let show = getBooleanPreference('category_filter_'+category);
		if (!show) {
			$(".taxon-image-category-type-"+category).hide();
		} else {
			$(".taxon-image-category-type-"+category+ " .taxon-image-lazy").each(function() {
				const $img = $(this);
       			$img.attr('src', $img.data('src'));
       			$img.removeClass('taxon-image-lazy');
			});
			$(".taxon-image-category-type-"+category).show();
		}
	} 
}

function defineImageSize() {
	let size = getPreference('imageSize');
	$(".taxon-image").removeClass('large_image');
	$(".taxon-image").removeClass('small_image');
	$(".taxon-image").addClass(size);
}

preferenceChangeHook = function(preference, value) {
    if (preference === "taxa" || preference === "order") {
        loadTree();
        loadSpecies();
    }
    if (preference === "taxonRanks" || preference === "pageSize") {
    	loadSpecies();
    }
    if (preference.startsWith("category_filter_")) {
    	filterImageCategories();
    };
    if (preference === "imageSize") {
    	defineImageSize();
    };
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

$(document).on("click", ".browse-tree-taxon-selector", function (e) {
    e.preventDefault();
    const taxonId = $(this).data("taxonid");
    setTaxonFilter(taxonId);
    loadSpecies();
});

let galleryViewer = null;
let lastOpenedImageIndex = null;

$(document).on("click", ".taxon-image-gallery-link", function (e) {
    e.preventDefault();
    const taxonId = $(this).data("taxonid");
    const category = $(this).data("category");
    const header = $(this).data("header");
    
    const h = Math.floor($(window).height() * 0.93);
    const w = Math.floor($(window).width());
    
    const modal = $("#gallery-modal");
    const content = $("#gallery-modal-content");
    content.html("<p>${text.loading}...</p>");
    modal.dialog("option", {
        height: h,
        width: w,
        title: header
    }).dialog("open");

    $.get("${baseURL}/api/gallery", { taxonId: taxonId, category: category })
    .done(function (html) {
        content.html(html);
        const gallery = $("#taxon-image-gallery");
        if (galleryViewer) {
      		galleryViewer.destroy();
      		galleryViewer = null;
    	}
    	gallery.viewer({
      		navbar: false,
      		title: false,
      		toolbar: true,
      		movable: true,
      		zoomable: true,
      		keyboard: true,
      		transition: true,
      		zoomRatio: 2,
      		interval: 100
    	});
    	galleryViewer = gallery.data("viewer");
    	galleryViewer.options.viewed = function () {
  	  		lastOpenedImageIndex = galleryViewer.index;
  	  		galleryViewer.zoomTo(1, false);
		};
    }).fail(function () {
        content.html("<p>Error loading images.</p>");
    });
});

$(document).on("click", ".ui-widget-overlay", function () {
    $("#gallery-modal").dialog("close");
});

$(document).on("keydown", function (e) {
    if (e.key === "ArrowRight" || e.key === "ArrowLeft") {

        // if viewer is already open, do nothing (Viewer handles arrows)
        if (galleryViewer && galleryViewer.isShown) return;

        const indexToOpen = lastOpenedImageIndex !== null ? lastOpenedImageIndex : 0;
        galleryViewer.show();
        galleryViewer.view(indexToOpen);
    }
});

$(document).ready(function() {

	$("#gallery-modal").dialog({
    	autoOpen: false,
    	modal: true,
    	draggable: false,
    	resizable: false,
    	closeOnEscape: true,
    	closeText: "${text.close}",
    	open: function () {
        	$("body").addClass("ui-dialog-open");
    	},
    	close: function () {
        	if (galleryViewer) {
      			galleryViewer.destroy();
      			galleryViewer = null;
      			lastOpenedImageIndex = null;
    		}
        	$("#gallery-modal-content").empty();
        	$("body").removeClass("ui-dialog-open");
    	}
	});

});
