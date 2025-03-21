<#include "header.ftl">
<#include "macro.ftl">

<nav class="breadcrumb" aria-label="Breadcrumb">
    <ol>
    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        <li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        <#if taxonSearch?has_content>
        	<li><a href="${baseURL}/admin?taxonSearch=${taxonSearch?html}">${text.menu_taxon_select}: ${taxonSearch?html}</a></li>
        </#if>
        <li><@printScientificName taxon/></li>
    </ol>
    
</nav>

<div id="mass-tag-select" class="box">
	<div id="mass-tag-select-header" class="box-header"><h5>${text.admin_tag_images}</h5> <span class="ui-icon ui-icon-caret-2-n-s"></span></div>
	<div id="mass-tag-select-body" class="box-body">
		<@select "type" types />
        <@select "sex" sexes />
        <@select "lifeStage" lifeStages />
        <@select "plantLifeStage" plantLifeStages />
        <@select "side" sides />
        <div>
        	<button id="mass-tag-clear-button" type="button">${text.clear_all}</button>
        	<button id="mass-tag-done-button" style="display:none" type="button">${text.admin_tag_done}</button>
        </div>
	</div>
</div>

<h2>${text.admin_main}</h2>


<#if prevTaxon??>
	<p><a href="${baseURL}/admin/${prevTaxon.id}">&larr; <@printNames prevTaxon/></a></p> 
</#if>
<#if nextTaxon??>
	<p>&nbsp;&nbsp;&nbsp;<a href="${baseURL}/admin/${nextTaxon.id}"><@printNames nextTaxon/> &rarr;</a></p>
</#if>

<h3><@printNames taxon/> | ${taxon.id}</h3>

<#if multiPrimary><div class="info warning"><p>WARNING: Taxon has MULTIPLE PRIMARY images</p></div></#if>



<#if taxon.multimedia?has_content>
<h4>${text.admin_select_image} ...</h4>
<#else>
<h4>${text.no_images}!</h4>
</#if>

<div class="image-grid">
	<#if taxon.multimedia?has_content>
		<#list taxon.categorizedMultimedia.groupedFlatImages as image>
			<a class="taxon-image <@addNewImageClass image/>" href="${baseURL}/admin/${image.id}?taxonId=${taxon.id}<#if taxonSearch??>&taxonSearch=${taxonSearch?html}</#if>"><img class="admin-image" src="${image.largeURL?html}" <@imageData image/> /></a>
		</#list>
	</#if>
	<@adminImageUpload true/>	
</div>

<#macro addNewImageClass image><#if newImages?? && newImages?seq_contains(image.id)>new-image</#if></#macro>

<#include "footer.ftl">