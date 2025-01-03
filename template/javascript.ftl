$(document).ready(function() {
	$("button").button();
	
	 $("#taxon-autocomplete").autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: "https:/laji.fi/api/taxa/search",
                    dataType: "json",
                    data: {
                        query: request.term,
                    },
                    success: function (data) {
						var suggestions = data.map(function (item) {
    						return {
        						label: item.matchingName + (item.nameType !== "MX.scientificName" ? " (" + item.scientificName + ")": ""),
        						value: item.id,
    						};
						});
                		response(suggestions); // Pass suggestions to autocomplete
                    },
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
	});
}
