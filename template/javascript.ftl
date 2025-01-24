$(document).ready(function() {

	$("button, .button").button();
	
	$(".ui-icon-info").tooltip({
    	classes: {
        	"ui-tooltip": "custom-tooltip-class"
    	}	
	});
	
	$("#preferences select").chosen({width: "8em"});
	
	$("#preferences input[type='checkbox']").checkboxradio();
	$("#preferences input[type='radio']").checkboxradio();
	
	$("#preferencesHeader").click(function() {
		$("#preferencesBody").toggle();
	});
	
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
    
});

function changeLocale() {
	var selectedLocale = $('#locale-selector').val();
	$.post('${baseURL}/api/set-locale?locale='+selectedLocale)
	.done(function() {
    	 window.location.reload();
	})
	.fail(function() {
		// Refresh the page to display the failure reason via flash message
		window.scrollTo(0, 0);
        window.location.reload();
	});
}
