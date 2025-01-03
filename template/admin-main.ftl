<#include "header.ftl">

		<h2>${text.admin_main}</h2>
		
		<h3>${text.admin_single_title}</h3>
		<p>${text.admin_single_body}</p>
		<form action="${baseURL}/admin">
		<input name="image" type="text" size="80" /> <button>Muokkaa</button>
		</form>
		
		<h3>${text.admin_taxon_title}</h3>
		<p>${text.admin_taxon_body}</p>
		<form action="${baseURL}/admin">
		<input name="taxon" type="text" size="50" id="taxon-autocomplete" placeholder="${text.taxon_autocomplete_placeholder}" /> <button>Valitse</button>
		</form>
		
<#include "footer.ftl">
