function togglePreferences() {
	$("#preferencesBody").toggle();
	$("#preferences").toggleClass("minimized");
}

function preferenceChangeHook(preference, value) {
    // Default: do nothing
}

$(document).ready(function() {

	$("#preferencesHeader").click(function() {
		togglePreferences();
		setPreference("showPreferences", !$("#preferences").hasClass("minimized")); 
	});
	
	$("#preferences select, #preferences input[type='radio']").on('change', function() {
		const preference = $(this).attr('name');
		const value = $(this).val();
		setPreference(preference, value).done(function() {
			if (preference == "group") {
				window.location.href = '${baseURL}/browse/'+value;
			}
			preferenceChangeHook(preference, value);
		});
	});

	$("#preferences input[type='checkbox']").on('change', function() {
		const preference = $(this).attr('name');
		const value = $(this).prop('checked');
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
	
	$.each(preferences, function(key, value) {
    	if (key.startsWith("category_filter_")) {
    		let isChecked = (value === true || value === "true");
        	$("#" + key).prop('checked', isChecked);
    	}
	});

	if (getPreference("pageSize")) {
		$("#pageSizeSelect").val(getPreference("pageSize"));
	}
	
	if (getPreference("imageSize")) {
		$("#"+getPreference("imageSize")).prop('checked', true);
	}
	
	if (getPreference("contentCreation")) {
		$("#"+getPreference("contentCreation")).prop('checked', true);
	}
	
	$("#preferences select").not("#groupSelect").chosen({width: "15em", no_results_text: "${text.no_matches}"});
	$("#preferences #groupSelect").chosen({width: "25em", no_results_text: "${text.no_matches}"});
	
	$("#preferences input[type='checkbox']").checkboxradio();
	$("#preferences input[type='radio']").checkboxradio();
	
	$("#preferences").show();
	
});