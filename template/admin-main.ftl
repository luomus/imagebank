<#include "header.ftl">
<#include "macro.ftl">

<nav class="breadcrumb" aria-label="Breadcrumb">
    <ol>
    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        <li>${text.menu_admin}</li>
    </ol>
</nav>

<h2>${text.admin_main}</h2>

<h3>${text.admin_taxon_title}</h3>
<p>${text.admin_taxon_body}:</p>
<form action="${baseURL}/admin">
	<input name="taxonSearch" type="text" size="50" id="taxon-autocomplete" placeholder="${text.taxon_autocomplete_placeholder}" /> <button class="ui-state-active">Valitse</button>
</form>

<h3>${text.admin_single_title}</h3>
<p>${text.admin_single_body}:</p>
<form action="${baseURL}/admin">
	<input name="imageSearch" type="text" size="80" /> <button class="ui-state-active">Muokkaa</button>
</form>

<h3>${text.admin_add_image_title}</h3>
<@adminImageUpload/>

<br />
<div class="info">
	${text.admin_info}
</div>

<#include "footer.ftl">