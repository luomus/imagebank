<#include "header.ftl">
<#include "macro.ftl">

<nav class="breadcrumb" aria-label="Breadcrumb">
	<ol>
		<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
		<li>${text.menu_browse}</li>
	</ol>
</nav>
		
<h2>${text.browse_title}</h2>

<section id="preferences" class="preferences" style="display: none;">
    <div id="preferencesHeader"><span class="ui-icon ui-icon-gear"></span>  &nbsp; ${text.preferences} &nbsp; <button class="button" style="float: right;" >${text.close}</button></div>
	<div id="preferencesBody">
    <div class="preferences-group">
        <fieldset>
        	<legend>${text.group_select}</legend>
        	<select id="groupSelect" name="group">
            	<#assign first = true>
            	<#list taxonGroups as group>
            		<#if !group.hasParents()>
            			<#if first><#assign first = false><#else></optgroup></#if>
            			<optgroup label="${group.name.forLocale(locale)}">
            		</#if>
            		<option value="${group.qname}">${group.name.forLocale(locale)}</option>
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
			<select id="taxonRankSelect" name="taxonRanks" multiple>
				<#list speciesTaxonRanks?keys as rank>
        	   	<option value="${rank}" <#if defaultTaxonRanks?seq_contains(rank)>selected="selected"</#if>>${speciesTaxonRanks[rank].forLocale(locale)?html}</option>
        	   	</#list>
        	</select>
        </fieldset>
    </div>

    <div class="preferences-group">
    	<fieldset>
    		<legend>${text.category_filter}</legend>
    		
    		<label for="category_filter_primary">Pääkuva</label>
    		<input type="checkbox" name="category_filter_primary" id="category_filter_primary" checked>
    		
    		<label for="category_filter_adult">Aikuiset</label>
    		<input type="checkbox" name="category_filter_adult" id="category_filter_adult" checked>
    		
    		<label for="category_filter_larva">Toukat</label>
    		<input type="checkbox" name="category_filter_larva" id="category_filter_larva" checked>
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
    		<label title="${text.image_size_large}" for="largeImage"><i class="fa fa-image fa-image-largeImage" aria-hidden="true"></i></label> 
    		<input type="radio" name="imageSize" id="largeImage" value="large_image" checked>
    		<label title="${text.image_size_small}" for="smallImage"><i class="fa fa-image fa-image-smallImage" aria-hidden="true"></i></label>
    		<input type="radio" name="imageSize" id="smallImage" value="small_image">
    	</fieldset>
    </div>

    <div class="preferences-group">
        <fieldset>
            <legend>${text.content_creation_preference}</legend>
            <label for="contentCreationOn">${text.on}</label>
    		<input type="radio" name="contentCreation" id="contentCreationOn" value="on" checked>
    		<label for="contentCreationOff">${text.off}</label>
    		<input type="radio" name="contentCreation" id="contentCreationOff" value="off">
        </fieldset>
    </div>
   	</div>
</section>



<#include "footer.ftl">
