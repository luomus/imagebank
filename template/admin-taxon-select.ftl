<#include "macro.ftl">
<#include "header.ftl">

		<h2>${text.admin_main}</h2>
		
		<h3>${text.select_taxon}...</h3>
		
		<#if results.exactMatches?has_content>
			<h4>${text.exact_matches}</h4>
			<ul>
			<#list results.exactMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<#if results.partialMatches?has_content>
			<h4>${text.partial_matches}</h4>
			<ul>
			<#list results.partialMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
		
		<#if results.likelyMatches?has_content>
			<h4>${text.likely_matches}</h4>
			<ul>
			<#list results.likelyMatches as match>
				<li><a href="${baseURL}/admin/${match.taxon.id}">${match.matchingName?html}</a> &mdash; <@printTaxon match.taxon /></li>
			</#list>
			</ul>
		</#if>
				
<#include "footer.ftl">
