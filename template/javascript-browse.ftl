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
   			initTaxonImages();
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

function initTaxonImages() {
	let size = getPreference('imageSize');
	$(".taxon-image-card").removeClass('large_image');
	$(".taxon-image-card").removeClass('small_image');
	$(".taxon-image-card").addClass(size);
	$(".taxon-image").hover(
			function() { $(this).closest(".image-wrapper").find(".image-info-box").stop(true, true).fadeIn(100); },
			function() { $(this).closest(".image-wrapper").find(".image-info-box").stop(true, true).fadeOut(100); }
        );
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
    	initTaxonImages();
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
    const container = $("#browse-taxa");
    if (container.length) {
        $("html, body").animate({
            scrollTop: container.offset().top
        }, 500);
    }
});

let galleryViewer = null;
let lastOpenedImageIndex = null;
let galleryImageMeta = {};

$(document).on("click", ".taxon-image-gallery-link", function (e) {
    e.preventDefault();
    const taxonId = $(this).data("taxonid");
    const category = $(this).data("category");
    const header = $(this).data("header");
    
    const h = Math.floor($(window).height() * 0.97);
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
        
        buildGalleryImageMeta(gallery);
        
        if (galleryViewer) {
      		galleryViewer.destroy();
      		galleryViewer = null;
    	}
    	gallery.viewer({
      		navbar: false,
      		title: [true, buildViewerTitle],
      		toolbar: true,
      		movable: true,
      		zoomable: true,
      		keyboard: true,
      		transition: true,
      		zoomRatio: 2,
      		interval: 1000
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

function buildGalleryImageMeta(gallery) {
    galleryImageMeta = {}; // reset
    gallery.find("img").each(function() {
        const img = $(this);
        const src = img.attr("src");
        const meta = {};
        $.each(this.dataset, function(key, value) {
            meta[key.toLowerCase()] = value || "";
        });
        galleryImageMeta[src] = meta;
    });
}

const fields = [
    { key: "authors", label: "${text.label_capturer}" },
    { key: "copyrightowner", label: "${text.label_rightsOwner}" },
    { key: "licenseabbreviation", label: "${text.label_license}" },
    { key: "type", label: "${text.label_type}" },
    { key: "taxondescriptioncaption", label: "${text.label_caption}" },
    { key: "capturedatetime", label: "${text.label_captureDateTime}" },
    { key: "uploaddatetime", label: "${text.label_uploadDateTime}" }
];

function buildViewerTitle(image) {
    const src = image.src;
    const meta = galleryImageMeta[src] || {};
    const infoHtml = fields
        .filter(f => meta[f.key] && String(meta[f.key]).trim() !== "")
        .map(f => f.label + ": " + meta[f.key])
        .join(" | ");
    return infoHtml;
}

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

