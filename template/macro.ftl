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
	<span class="scientificName <#if taxon.isCursiveName()>speciesName</#if>">${(taxon.scientificName!taxon.vernacularName.forLocale("en")!taxon.qname)?html}</span>
</#macro>

<#macro printNames taxon>
	<@printScientificName taxon /> <#if taxon.vernacularName.forLocale(locale)?has_content> &mdash; </#if>${(taxon.vernacularName.forLocale(locale)!"")?html}
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

<#macro list seq><#list seq as s>${s?html}<#if s_has_next>, </#if></#list></#macro>
<#macro listEnum seq enum><#list seq as s>${enum[s].forLocale(locale)}<#if s_has_next>, </#if></#list></#macro>
<#macro enumV val enum><#if val?has_content>${enum[val].forLocale(locale)}</#if></#macro>
<#macro mapV val map><#if val?has_content>${map[val]}</#if></#macro>

<#macro label name>
	<label for="${name}">${text["label_"+name]!name}:</label>
</#macro>
		
<#macro select name enum value="">
	<div>
		<@label name />	
		<select id="${name}" name="${name}">
        	<option value="">&nbsp;</option>
        	<#list enum?keys as key>
        		<option value="${key}" <#if value==key>selected="selected"</#if>>${enum[key].forLocale(locale)}</option>
        	</#list>
    	</select>
    </div>
</#macro>

<#macro selectMulti name enum values>
	<div>
		<@label name />	
		<select id="${name}" name="${name}" multiple="multiple" data-placeholder=" ">
        	<#list enum?keys as key>
        		<option value="${key}" <#if values?seq_contains(key)>selected="selected"</#if>>${enum[key].forLocale(locale)}</option>
        	</#list>
    	</select>
    </div>
</#macro>

<#macro selectBool name value>
	<div>
		<@label name />	
		<select id="${name}" name="${name}" class="bool-select">
        	<option value="">&nbsp;</option>
        	<option value="true" <#if value>selected="selected"</#if>>${text.yes}</option>
        	<option value="false" <#if !value>selected="selected"</#if>>${text.no}</option>
    	</select>
    </div>
</#macro>

<#macro input name size value="">
	<div>
		<@label name />
		<input type="text" name="${name}" size="${size}" value="${value?html}" />
	</div>
</#macro>

<#macro inputMulti name size values>
	<div class="multi-input">
		<#if values?has_content>
			<#list values as v>
				<@input name size v/>
			</#list>
		<#else>
			<@input name size />
		</#if>
		<span class="multi-input-add-item">+</span>
	</div><br />
</#macro>

<#macro inputNumber name value="">
	<div>
		<@label name />
		<input type="number" name="${name}" size="8" value="${value?html}" min="0" />
	</div>
</#macro>