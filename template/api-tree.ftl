<#include "macro.ftl">
<#if taxa?has_content>
<ol>
	<#list taxa as taxon>
		<li class="${taxon.taxonRank?replace("MX.","")}"><@printNameRank taxon/></li>
	</#list>
</ol>
<#else>
<p>${text.taxon_tree_no_taxa}</p>
</#if>