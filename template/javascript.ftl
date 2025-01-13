$(document).ready(function() {

	$("button, .button").button();
	
	$("#taxon-autocomplete").autocomplete({
		source: function (request, response) {
			$.ajax({
				url: "https:/laji.fi/api/taxa/search",
				dataType: "json",
				data: { query: request.term },
				success: function (data) {
					var suggestions = data.map(function (item) {
						return {
							label: item.matchingName + (item.nameType !== "MX.scientificName" ? " (" + item.scientificName + ")": ""),
							value: item.id
						};
					});
					response(suggestions); // Pass suggestions to autocomplete
				}
			});
		},
		minLength: 3,
		select: function (event, ui) {
			$(this).val(ui.item.value);
			$(this).closest("form").submit();
			return false; // Prevent default autocomplete behavior
		}
	});
        
	$(".admin-image").each(function () {
		const image = $(this);
		const wrapper = $("<div class='admin-image-wrapper'></div>");
		image.wrap(wrapper);
		
        const tagsContainer = $("<div class='admin-image-tags'></div>");
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
    
    $("#cancelButton").click(function() { window.location.reload(); return false; });
});

function changeLocale() {
	var selectedLocale = $('#locale-selector').val();
	$.post('${baseURL}/api/set-locale?locale='+selectedLocale)
	.done(function() {
    	 window.location.reload();
	});
}
