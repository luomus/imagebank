<#macro printTaxon taxon>
	<@printScientificName taxon />
	<#if taxon.scientificNameAuthorship?has_content>
		<span class="author">${taxon.scientificNameAuthorship?html}</span>
	</#if>
	<#if taxon.taxonRank?has_content>
		<span class="taxonRank">[${taxonRanks[taxon.taxonRank].forLocale(locale)?html}]</span>
	</#if>
	<#if taxon.informalTaxonGroups?has_content>
		&mdash; ${taxon.informalTaxonGroupNames.forLocale(locale)?html}
	</#if>
</#macro>

<#macro printNameRank taxon>
	<@printScientificName taxon />
	<#if taxon.taxonRank?has_content>
		<span class="taxonRank">[${taxonRanks[taxon.taxonRank].forLocale(locale)?html}]</span>
	</#if>
</#macro>

<#macro printScientificName taxon>
	<span class="scientificName <#if taxon.isCursiveName()>speciesName</#if>">${(taxon.scientificName!taxon.vernacularName.forLocale("en")!taxon.qname)?html}</span>
</#macro>

<#macro printNames taxon>
	<@printScientificName taxon /> <#if taxon.vernacularName.forLocale(locale)?has_content> &mdash; </#if>${(taxon.vernacularName.forLocale(locale)!"")?html}
</#macro>

<#macro printNamesRank taxon>
	<@printScientificName taxon /> <#if taxon.vernacularName.forLocale(locale)?has_content> &mdash; </#if>${(taxon.vernacularName.forLocale(locale)!"")?html} 
	<#if taxon.taxonRank?has_content>
		<span class="taxonRank">[${taxonRanks[taxon.taxonRank].forLocale(locale)?html}]</span>
	</#if>
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
<#macro listEnum seq enum><#list seq as s>${enum[s].forLocale(locale)?html}<#if s_has_next>, </#if></#list></#macro>
<#macro enumV val enum><#if val?has_content>${enum[val].forLocale(locale)?html}</#if></#macro>
<#macro mapV val map><#if val?has_content>${(map[val]!val)?html}</#if></#macro>

<#macro label name>
	<label for="${name?html}">${(text["label_"+name]!name)?html}:</label>
</#macro>
		
<#macro select name enum value="">
	<div>
		<@label name />	
		<select id="${name?html}" name="${name?html}">
        	<option value="">&nbsp;</option>
        	<#list enum?keys as key>
        		<option value="${key?html}" <#if value==key>selected="selected"</#if>>${enum[key].forLocale(locale)?html}</option>
        	</#list>
    	</select>
    </div>
</#macro>

<#macro selectMulti name enum values>
	<div>
		<@label name />	
		<select id="${name?html}" name="${name?html}" multiple="multiple" data-placeholder=" ">
        	<#list enum?keys as key>
        		<option value="${key?html}" <#if values?seq_contains(key)>selected="selected"</#if>>${enum[key].forLocale(locale)?html}</option>
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
		<input type="text" name="${name?html}" size="${size?html}" value="${value?html}" />
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
		<input type="number" name="${name?html}" size="8" value="${value?html}" min="0" />
	</div>
</#macro>

<#macro adminImageUpload multi=false>
	<form class="image-upload-form" id="imageUploadForm" action="${baseURL}/admin/add<#if taxon??>?taxonId=${taxon.id}<#if taxonSearch??>&taxonSearch=${taxonSearch?html}</#if></#if>" method="POST" enctype="multipart/form-data">
		<fieldset id="adminMetaForm">
			<legend><#if multi>${text.admin_add_image_title_multi}<#else>${text.admin_add_image_title}</#if></legend>
			    <div id="dropArea">
      				<input type="file" id="fileInput" name="images" accept="image/*" <#if multi>multiple</#if> hidden />
      				<div id="dropText">
        				<i class="fa fa-camera" aria-hidden="true"></i>
        				<p><#if multi>${text.drop_image_multi}<#else>${text.drop_image}</#if></p>
      				</div>
    			</div>
    			<#if !taxon??>
			    	<div id="secretImageOption">
      					<label for="secretCheckbox">${text.label_secret}</label>
        				<span class="ui-icon ui-icon-info" title="${text.admin_secret_image_help}">?</span>
        				<input type="checkbox" id="secretCheckbox" name="secret">
        			</div>
        		</#if>
        		<#if multi>
        			<@input "capturer" 30 user.fullName />
					<@input "rightsOwner" 30 "Luomus" />
        			<@select "license" licenses defaultLicense />
        		</#if>
        		${defaultLicense!"NOLICEN"}
			<button type="submit" id="saveButton" disabled>${text.save}</button>
		</fieldset>
	</form>
</#macro>

<#macro preferences>
<section id="preferences" class="box preferences" style="display: none;">
    <div id="preferencesHeader" class="box-header"><span class="ui-icon ui-icon-gear"></span>  &nbsp; <h5>${text.preferences}</h5> &nbsp; <button class="button" style="float: right;" >${text.close}</button></div>
	<div id="preferencesBody" class="box-body">
    <div class="preferences-group">
        <fieldset>
        	<legend>${text.group}</legend>
        	<select id="groupSelect" name="group" data-placeholder="${text.group_select}">
            	<#assign first = true>
            	<#list taxonGroups as group>
            		<#if !group.hasParents()>
            			<#if first><#assign first = false><#else></optgroup></#if>
            			<optgroup label="${group.name.forLocale(locale)?html}">
            		</#if>
            		<option value="${group.qname}">${group.name.forLocale(locale)?html}</option>
				</#list>
				</optgroup>
        	</select>
        </fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
    		<legend>${text.order}</legend>
    		<label for="order_taxonomic">${text.taxonomic}</label>
    		<input type="radio" name="order" id="order_taxonomic" value="order_taxonomic" checked>
    		<label for="order_alphabetic">${text.alphabetic}</label>
    		<input type="radio" name="order" id="order_alphabetic" value="order_alphabetic">
    	</fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
    		<legend>${text.taxa_preference}</legend>
    		<label for="taxa_finnish">${text.taxa_finnish}</label>
    		<input type="radio" name="taxa" id="taxa_finnish" value="taxa_finnish" checked>
    		<label for="taxa_all">${text.taxa_all}</label>
    		<input type="radio" name="taxa" id="taxa_all" value="taxa_all">
    	</fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
    		<legend>${text.taxon_ranks_preference}</legend>
			<select id="taxonRankSelect" name="taxonRanks" multiple data-placeholder=" ">
				<#list speciesTaxonRanks?keys as rank>
        	   	<option value="${rank?html}" <#if defaultTaxonRanks?seq_contains(rank)>selected="selected"</#if>>${(speciesTaxonRanks[rank].forLocale(locale)!rank)?html}</option>
        	   	</#list>
        	</select>
        </fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
    		<legend>${text.category_filter}</legend>
    		<#list defs as def>
    			<label for="category_filter_${def.id}">${def.title.forLocale(locale)}</label>
    			<input type="checkbox" name="category_filter_${def.id}" id="category_filter_${def.id}" checked>
    		</#list>
        </fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
        	<legend>${text.page_size}</legend>
        	<select id="pageSizeSelect" name="pageSize">
            	<option value="10">10</option>
            	<option value="100" selected>100</option>
            	<option value="1000">1000</option>
        	</select>
        </fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
    		<legend>${text.image_size}</legend>
    		<label title="${text.image_size_large}" for="large_image"><i class="fa fa-image fa-image-largeImage" aria-hidden="true"></i></label> 
    		<input type="radio" name="imageSize" id="large_image" value="large_image" checked>
    		<label title="${text.image_size_small}" for="small_image"><i class="fa fa-image fa-image-smallImage" aria-hidden="true"></i></label>
    		<input type="radio" name="imageSize" id="small_image" value="small_image">
    	</fieldset>
    </div>

    <div class="preferences-group">
        <fieldset>
            <legend>${text.content_creation_preference}</legend>
            <label for="cc_on">${text.on}</label>
    		<input type="radio" name="contentCreation" id="cc_on" value="cc_on" checked>
    		<label for="cc_off">${text.off}</label>
    		<input type="radio" name="contentCreation" id="cc_off" value="cc_off">
        </fieldset>
    </div>

   	</div>
</section>
</#macro>
