<#macro printTaxon taxon>
	<@printScientificName taxon />
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

<#macro printScientificName taxon>
	<span class="scientificName <#if taxon.isCursiveName()>speciesName</#if>">${taxon.scientificName!taxon.vernacularName.forLocale("en")!taxon.qname}</span>
</#macro>

<#macro printNames taxon>
	<@printScientificName taxon /> <#if taxon.vernacularName.forLocale(locale)?has_content> &mdash; </#if>${taxon.vernacularName.forLocale(locale)!""}
</#macro>

<#macro imageData image>
	alt=""
	data-id="${image.id}"
	data-authors="${(image.author!"")?html}"
	data-licenseabbreviation="${(image.licenseAbbreviation!"")?html}"
	data-copyrightowner="${(image.copyrightOwner!"")?html}"
	data-caption="${(image.caption!"")?html}"
	data-taxondescriptioncaption="${(image.taxonDescriptionCaption.forLocale("fi")!"")?html}"
	data-primary="${((image.isPrimaryForTaxon()?string("true", "false"))!"false")?html}"
	data-keywords="<@list image.keywords/>"
	data-type="<@enumV image.type!"" types/>"
	data-side="<@enumV image.side!"" sides/>"
	data-sex="<@listEnum image.sex sexes/>"
	data-lifestage="<@listEnum image.lifeStage lifeStages/>"
	data-plantlifestage="<@listEnum image.plantLifeStage plantLifeStages/>"
	data-uploaddatetime="${(image.uploadDateTime!"")?html}"
	data-capturedatetime="${(image.captureDateTime!"")?html}"
</#macro>

<#macro list seq><#list seq as s>${s}<#if s_has_next>, </#if></#list></#macro>
<#macro listEnum seq enum><#list seq as s>${enum[s].forLocale(locale)}<#if s_has_next>, </#if></#list></#macro>
<#macro enumV val enum><#if val?has_content>${enum[val].forLocale(locale)}</#if></#macro>
		
		
		
		
		
		
		
