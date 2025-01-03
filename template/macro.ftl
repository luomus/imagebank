<#macro printTaxon taxon>
	<span class="scientificName <#if taxon.isCursiveName()>speciesName</#if>">${taxon.scientificName!taxon.vernacularName.forLocale("en")!taxon.qname}</span>
	<#if taxon.scientificNameAuthorship?has_content>
		<span class="author">${taxon.scientificNameAuthorship?html}</span>
	</#if>
	<#if taxon.taxonRank?has_content>
		<span class="taxonRank">[${taxonRanks[taxon.taxonRank].forLocale(locale)}]</span>
	</#if>
	<#if taxon.informalTaxonGroups?has_content>
		&mdash; ${taxon.informalTaxonGroupNames.forLocale(locale)}
	</#if>
</#macro>

