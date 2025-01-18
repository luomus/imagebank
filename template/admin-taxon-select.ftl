<#include "macro.ftl">
<#include "header.ftl">
		
		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<li>${text.menu_taxon_select}: ${taxonSearch?html}</li>
    		</ol>
		</nav>

		<h2>${text.admin_main}</h2>
		
		<h3>${text.taxon_search_term} "${taxonSearch?html}"</h3>
		
		<#if results.hasMatches()>
			<h4>${text.select_taxon} ...</h4>
		<#else>
			<h4>${text.no_taxon_matches}!</h4>
		</#if>
		
		<#if results.exactMatches?has_content>
			<h5>${text.exact_matches}</h5>
			<ul>
			<#list results.exactMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}?${ref?html}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<#if results.partialMatches?has_content>
			<h5>${text.partial_matches}</h5>
			<ul>
			<#list results.partialMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}?${ref?html}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<#if results.likelyMatches?has_content>
			<h5>${text.likely_matches}</h5>
			<ul>
			<#list results.likelyMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}?${ref?html}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<br />
		<form action="${baseURL}/admin">
			<input name="taxonSearch" type="text" size="50" id="taxon-autocomplete" placeholder="${text.taxon_autocomplete_placeholder}" /> <button>Valitse</button>
		</form>
			
<#include "footer.ftl">
