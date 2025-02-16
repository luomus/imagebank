$(document).ready(function() {

    $("#cancelButton").click(function() {
    	if (confirm('${text.cancel_confirm}')) {
    		window.location.reload(); 
    		return false;
    	}
    	return false; 
    });
    
	$(".admin-image").each(function () {
		const image = $(this);
		const wrapper = $("<div class='image-wrapper'></div>");
		image.wrap(wrapper);
		
        const tagsContainer = $("<div class='image-tags'></div>");
        const tagData = {
			primary: image.data("primary") ? "primary" : null,
			type: image.data("type"),
			side: image.data("side"),
			sex: image.data("sex"),
			lifestage: image.data("lifestage"),
			plantlifestage: image.data("plantlifestage"),
		};

        for (const [key, value] of Object.entries(tagData)) {
			if (value) tagsContainer.append('<span class="tag" data-type="'+key+'">'+value+'</span>');
		}
		image.after(tagsContainer);

		const infoBox = $("<div class='image-info-box'></div>");
		const fields = [
			{ key: "authors", label: "Authors" },
    		{ key: "copyrightowner", label: "Copyright Owner" },
    		{ key: "licenseabbreviation", label: "License" },
			{ key: "caption", label: "Caption" },
			{ key: "taxondescriptioncaption", label: "Taxon Description" },
			{ key: "capturedatetime", label: "Capture Date" },
			{ key: "uploaddatetime", label: "Upload Date" },
			{ key: "keywords", label: "Keywords" }
		];
		let infoHtml = fields.map(field => {
        	const value = image.data(field.key);
        	return value ? '<p><strong>' + field.label + ':</strong> ' + value + '</p>' : '';
    	})
    	.join('');
		infoBox.html(infoHtml);
		image.after(infoBox);
        image.hover(
			function () { infoBox.stop(true, true).fadeIn(100); },
			function () { infoBox.stop(true, true).fadeOut(100); }
        );
	});
    
    <#if image??>
	$("#deleteButton").click(function(event) {
    	event.preventDefault(); // Prevent default button behavior
	    if (confirm('${text.admin_delete_confirm}')) {
        	$.ajax({
            	url: '${baseURL}/admin/delete/${image.id?html}',
            	type: 'DELETE',
            	success: function() {
                	window.location.href = '${baseURL}/admin?taxonSearch=${(taxonSearch!"")?html}&taxonId=${(taxonId!"")?html}&imageSearch=${(imageSearch!"")?html}';
            	},
            	error: function() {
                	// Refresh the page to display the failure reason via flash message
					window.scrollTo(0, 0);
        			window.location.reload();
            	}
        	});
    	}
	});
    </#if>
    
    $(".multi-input-add-item").click(function() {
    	const parent = $(this).parent(".multi-input");
    	const firstChild = parent.find("div").first();
    	let clonedChild = firstChild.clone();
    	clonedChild.find('input').val('');
    	parent.append(clonedChild);
    });
    
    $('.admin-image-edit input[name="captureDateTime"], .admin-image-edit input[name="uploadDateTime"]')
    	.filter(function() {return $(this).val() === "";})
    	.after($("<div class='time-format-help'><span class='date-part'>YYYY-MM-DD</span><b>T</b><span class='time-part'>hh:mm:ss</span>.000+0Z:00</div>"))
    	.attr('placeholder', 'YYYY-MM-DDThh:mm:ss.000+0Z:00');
    
	$(".admin-image-edit select").not(".bool-select").chosen({width: "30em", no_results_text: "${text.no_matches}"}); 
	$(".admin-image-edit select.bool-select").chosen({width: "10em", no_results_text: "${text.no_matches}"});
     
	$("#mass-tag-select-header").click(function() {
		$("#mass-tag-select-body").toggle();
	});
     
	$("#mass-tag-select select").each(function() {
        $(this).val(''); // Clear the value
    });
    
	const activeTags = {};
     
	$("#mass-tag-select select").change(function() {
     	const name = $(this).attr("name");
    	const value = $(this).val(); // The select's value
    	const label = $(this).find("option:selected").text(); // The human-readable label
   		let has = false;
		if (value) {
			activeTags[name] = { value, label };
			has = true;
    	} else {
			delete activeTags[name];
    	}
   		$(".tag-hover").remove();
    	if (has) {
    		let hoverElement = $('<div class="tag-hover">${text.admin_click_to_add_tags}: </div>');
        	Object.entries(activeTags).forEach(([name, tag]) => {
            	hoverElement.append('<span class="tag" data-type="' + name + '">' + tag.label + '</span> ');
        	});
        	$("body").append(hoverElement);
    	}
	});
	
	$(document).on("mousemove", function (event) {
		$(".tag-hover").css({
			top: event.pageY + 10 + "px",
			left: event.pageX + 10 + "px"
		});
	});
	
	$(".taxon-image").click(function() {
    	if (Object.keys(activeTags).length === 0) return true;
		const image = $(this);
	    const imageId = image.find('img').first().data("id");
	    $.ajax({
        	url: "${baseURL}/admin/tag/"+imageId,
        	method: "POST",
        	contentType: "application/json",
        	data: JSON.stringify(activeTags),
        	success: function(response) {
            	image.addClass('tagged-saved');
            	$("#mass-tag-done-button").show('fast');
        	},
        	error: function(xhr, status, error) {
            	// Refresh the page to display the failure reason via flash message
				window.scrollTo(0, 0);
        		window.location.reload();
        	}
    	});
    	return false;
	});
    
    $("#mass-tag-clear-button").click(function() {
    	 $("#mass-tag-select select").val('').change();
    });
    	
	$("#mass-tag-done-button").click(function() { window.location.reload(); });
    
	<#if taxon??>
     	const makePrimaryButton = $('<button id="makePrimary" type="button">${text.admin_make_primary}</button>').button();
     	$("input[name='primaryForTaxon']").first().parent().before(makePrimaryButton);
     	
     	makePrimaryButton.click(function() {
     		$(this).parent().find(".multi-input-add-item").first().click();
     		$("input[name='primaryForTaxon']").filter(function() {return $(this).val() === "";}).first().val('${taxon.id}');
     	});
	</#if>
    
    $("#dropArea").on("dragover", function(event) {
        event.preventDefault();
        $(this).addClass("hover");
    }).on("dragleave", function() {
        $(this).removeClass("hover");
    }).on("drop", function(event) {
        event.preventDefault();
        $(this).removeClass("hover");
        let files = event.originalEvent.dataTransfer.files;
        $("#fileInput")[0].files = files;
        $("#fileInput").trigger("change");
    }).on("click", function() {
    	 $("#fileInput").click();
    });
    
    $("#fileInput").on("click", function(event) {
    	event.stopPropagation();
    });
    
    $("#fileInput").on("change", function() {
        if (this.files.length > 0) {
            $("#dropText p").text(this.files[0].name);
            $("#saveButton").button("enable");
        }
    });

    $("#imageUploadForm").submit(function(event) {
        event.preventDefault(); 
        if ($("#fileInput").prop('files').length < 1) return false;
        const formData = new FormData(this);
        $.ajax({
            url: $(this).attr("action"),
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
            	let id = response;
                window.location.href = '${baseURL}/admin/'+id+'?taxonSearch=${(taxonSearch!"")?html}&taxonId=<#if taxon??>${taxon.id}</#if>';
            },
            error: function(xhr, status, error) {
            	window.scrollTo(0, 0);
                window.location.reload();
            }
        });
    });
    

});
