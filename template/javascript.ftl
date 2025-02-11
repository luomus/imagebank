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
				url: "https://laji.fi/api/taxa/search",
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
   
    if (getPreference("group")) {
    	let group = getPreference("group");
    	let browse = $("#menu_browse");
    	let curate =  $("#menu_curate");
		$(browse).attr('href', browse.attr('href')+'/'+group);
		$(curate).attr('href', curate.attr('href')+'/'+group);
	}
            
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
		return $.ajax({
        	url: '${baseURL}/api/preferences',
        	type: 'POST',
        	data: { preference, value }
    	}).done(function() {
        	preferences[preference] = value;
    	}).fail(function() {
        	// Refresh the page to display the failure reason via flash message
        	window.scrollTo(0, 0);
        	window.location.reload();
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
		return $.Deferred().resolve();
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
	<#include "javascript-preferences.ftl">
</#if>
