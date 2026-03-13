<#include "macro.ftl">
<#if taxa?has_content>
<ol>
	<#list taxa as taxon>
		<li class="${taxon.taxonRank?replace("MX.","")}">
			
			<a class="browse-tree-taxon-selector" href="#" data-taxonid="${taxon.id}"><@printNameRank taxon/></a>
			<#if finnish>
				<span class="speciesCount">${taxon.countOfFinnishSpecies}</span>
			<#else>
				<span class="speciesCount">${taxon.countOfSpecies}</span>
			</#if>
		</li>
	</#list>
</ol>
<#else>
<p>${text.taxon_tree_no_taxa}</p>
</#if>