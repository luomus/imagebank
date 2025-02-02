$(document).ready(function() {

	$("button, .button").button();
	
	$(".ui-icon-info").tooltip({
    	classes: {
        	"ui-tooltip": "custom-tooltip-class"
    	}	
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

function togglePreferences() {
	$("#preferencesBody").toggle();
	$("#preferences").toggleClass("minimized");
}



<#if preferences??>
	let preferences = JSON.parse('${preferences.json}');
	
	localStorage.setItem("userPreferences", JSON.stringify(preferences));
	
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
	
	function getPreference(preference) {
		return preferences[preference] || false;
	}
	function getBooleanPreference(preference) {
		if (preferences[preference] === undefined) return true;
    	return preferences[preference] === true || preferences[preference] === "true";
	}
	function getArrayPreference(preference) {
		let arrPref = preferences[preference]; 
		if (arrPref === undefined) return [];
		if (Array.isArray(arrPref)) return arrPref;
    	return preferences[preference].split(',');
	}
	
<#if page == "browse">

$(document).ready(function() {

	$("#preferencesHeader").click(function() {
		togglePreferences();
		setPreference("showPreferences", !$("#preferences").hasClass("minimized")); 
	});
	
	$("#preferences select, #preferences input[type='radio']").on('change', function() {
		const preference = $(this).attr('name');
		const value = $(this).val();
		setPreference(preference, value);
	});

	if (!getBooleanPreference("showPreferences")) {
		togglePreferences();
	}
	
	if (getPreference("group")) {
		$("#groupSelect").val(getPreference("group"));
	}
	
	if (getPreference("order")) {
		$("#"+getPreference("order")).prop('checked', true);
	}
	
	if (getPreference("taxa")) {
		$("#"+getPreference("taxa")).prop('checked', true);
	}
	
	if (getArrayPreference("taxonRanks")) {
		let selectedRanks = getArrayPreference("taxonRanks");
		if (selectedRanks.length !== 0) {
			$("#taxonRankSelect").val(selectedRanks);
		}
	}
	
	if (getPreference("pageSize")) {
		$("#pageSizeSelect").val(getPreference("pageSize"));
	}
	
	if (getPreference("imageSize")) {
		$("#"+getPreference("imageSize")).prop('checked', true);
	}
	
	if (getPreference("contentCreation")) {
		$("#"+getPreference("contentCreation")).prop('checked', true);
	}
	
	$("#preferences select").not("#groupSelect").chosen({width: "15em"});
	$("#preferences #groupSelect").chosen({width: "25em"});
	
	$("#preferences input[type='checkbox']").checkboxradio();
	$("#preferences input[type='radio']").checkboxradio();
	
    
	$("#preferences").show();
	
});

</#if>
