$(document).ready(function() {

	$("button, .button").button();
	
	$(".ui-icon-info").tooltip({
    	classes: {
        	"ui-tooltip": "custom-tooltip-class"
    	}	
	});
	
	$("#preferences select").not("#groupSelect").chosen({width: "8em"});
	$("#preferences #groupSelect").chosen({width: "25em"});
	
	$("#preferences input[type='checkbox']").checkboxradio();
	$("#preferences input[type='radio']").checkboxradio();
	
	$("#preferencesHeader").click(function() {
		$("#preferencesBody").toggle();
	});
	
	$("#preferences select, #preferences input[type='radio']").on('change', function() {
		const preference = $(this).attr('name');
		const value = $(this).val();
		setPreference(preference, value);
	});
	
	$("#taxon-autocomplete").autocomplete({
		source: function (request, response) {
			$.ajax({
				url: "https:/laji.fi/api/taxa/search",
				dataType: "json",
				data: { query: request.term },
				success: function (data) {
					let suggestions = data.map(function (item) {
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
	let selectedLocale = $('#locale-selector').val();
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

<#if preferences??>
	let preferences = JSON.parse('${preferences.json}');

	function setPreference(preference, value) {
		$.ajax({
            	url: '${baseURL}/api/preferences?preference=' +encodeURIComponent(preference)+ '&value=' +encodeURIComponent(value),
            	type: 'POST',
            	success: function() {
                	preferences[preference] = value;
            	},
            	error: function() {
                	// Refresh the page to display the failure reason via flash message
					window.scrollTo(0, 0);
        			window.location.reload();
            	}
		});
	}
<#else>
	let preferences = JSON.parse(localStorage.getItem("userPreferences"));
	if (!preferences) {
    	preferences = {};
	}
	
	function setPreference(preference, value) {
		preferences[preference] = value;
		localStorage.setItem("userPreferences", JSON.stringify(preferences));
	}
</#if>
