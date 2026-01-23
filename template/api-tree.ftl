<#include "macro.ftl">
<#if taxa?has_content>
<ol>
	<#list taxa as taxon>
		<li class="${taxon.taxonRank?replace("MX.","")}"><a href="#"><@printNameRank taxon/></a></li>
	</#list>
</ol>
<#else>
<p>${text.taxon_tree_no_taxa}</p>
</#if>