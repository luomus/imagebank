<#include "macro.ftl">
<#include "header.ftl">
		
		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<li>${text.menu_taxon_select}</li>
    		</ol>
		</nav>

		<h2>${text.admin_main}</h2>
		
		<#if results.hasMatches()>
			<h3>${text.select_taxon}...</h3>
		<#else>
			<h3>${text.no_taxon_matches}!</h3>
		</#if>
		
		<#if results.exactMatches?has_content>
			<h4>${text.exact_matches}</h4>
			<ul>
			<#list results.exactMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}?${ref}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<#if results.partialMatches?has_content>
			<h4>${text.partial_matches}</h4>
			<ul>
			<#list results.partialMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}?${ref}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<#if results.likelyMatches?has_content>
			<h4>${text.likely_matches}</h4>
			<ul>
			<#list results.likelyMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}?${ref}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
				
<#include "footer.ftl">
