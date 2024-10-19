$(document).ready(function() {
	$("button").button();
});
        
function changeLocale() {
	var selectedLocale = $('#locale-selector').val();
	$.post('${baseURL}/api/set-locale?locale='+selectedLocale)
	.done(function() {
                    window.location.reload();
                });
}
