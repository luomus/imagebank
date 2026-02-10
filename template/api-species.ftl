<#include "macro.ftl">

<#if taxa?has_content>
	<@pager/>
	<div class="taxon-image-container">
		<#list taxa as taxon>
			<@printTaxonImages taxon/>
		</#list>
	</div>
	<@pager/>
<#else>
<p>${text.taxon_tree_no_taxa}</p>
</#if>

<#macro printTaxonImages taxon> 
	<div class="taxon-image-card">
		<h4 class="taxon-image-card-header"><@printNamesRank taxon/></h4>
		<#list taxon.categorizedMultimedia.categories as category>
			<div class="taxon-image-category taxon-image-category-type-${category.id}">
				<h4>${category.title.forLocale(locale)?html}</h4>
				<#if category.subcategories?has_content>
					<#list category.subcategories as subcategory>
						<h4>${subcategory.title.forLocale(locale)?html}</h4>
						<@printImages subcategory.images subcategory taxon/>
					</#list>
				<#else>
					<h4>&nbsp;</h4>
					<@printImages category.images category taxon/>
				</#if>
			</div>
		</#list>
		<#if taxon.categorizedMultimedia.uncategorizedImages?has_content>
			<div class="taxon-image-category taxon-image-category-type-uncategorized">
				<h4>${text.uncategorized}</h4>
				<h4>&nbsp;</h4>
				<@printImages taxon.categorizedMultimedia.uncategorizedImages "" taxon/>
			</div>
		</#if>		
	</div>
</#macro>

<#macro printImages images category taxon>
	<#list images as image>
		<@galleryLink category taxon/><img class="taxon-image taxon-image-lazy" data-src="${image.largeURL?html}" src="${staticURL}/pixel.gif" /></a>
		<div class="taxon-image-tools">
			<i class="fa fa-camera add-image-icon" aria-hidden="true"></i> &nbsp;
			<@galleryLink category taxon/><i class="fa fa-clone" aria-hidden="true"></i> ${images?size}</a>
		</div>
		<#break>
	</#list>
	<#if !images?has_content>
		<div class="dropArea">
      		<div class="dropText">
	    		<i class="fa fa-camera" aria-hidden="true"></i>
	    	</div>
    	</div>
	</#if>
</#macro>

<#macro galleryLink category taxon>
	<a href="#" class="taxon-image-gallery-link" 
			data-taxonid="${taxon.id?html}" 
			data-category="<#if category?has_content>${category.id}<#else>uncategorized</#if>" 
			data-header="<@printNamesRankPlain taxon/> - <#if category?has_content>${(category.title.forLocale(locale)?html)}<#else>${text.uncategorized}</#if>">
</#macro>

<#macro pager>
<#if lastPage != 1>
<nav class="pager">
    <#if currentPage != 1>
        <button onclick="loadSpecies(1)" class="pager-btn">« First</button>
    </#if>
    
    <#if prevPage??>
        <button onclick="loadSpecies(${prevPage})" class="pager-btn">‹ Prev</button>
    </#if>

    <#list (currentPage - 3)..(currentPage + 3) as page>
        <#if page gt 0 && page lte lastPage>
            <button onclick="loadSpecies(${page})"
                class="pager-btn <#if page == currentPage>active</#if>">
                ${page}
            </button>
        </#if>
    </#list>

    <#if nextPage??>
        <button onclick="loadSpecies(${nextPage})" class="pager-btn">Next ›</button>
    </#if>

    <#if currentPage != lastPage>
        <button onclick="loadSpecies(${lastPage})" class="pager-btn">Last »</button>
    </#if>
</nav>
</#if>
</#macro>

