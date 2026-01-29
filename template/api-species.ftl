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
		<h4 class="taxon-image-card-header"><@printNameRank taxon/></h4>
		<#list taxon.categorizedMultimedia.categories as category>
			<div class="taxon-image-category taxon-image-category-type-${category.id}">
				<h4>${category.title.forLocale(locale)?html} ${category.id}</h4>
				
				<#if category.subcategories?has_content>
					<#list category.subcategories as subcategory>
						<h4>${subcategory.title.forLocale(locale)?html} ${subcategory.id}</h4>
						<#list subcategory.images as image>
							<img class="taxon-image" src="${image.largeURL?html}" />
							<#break>
						</#list>
						<#if !subcategory.images?has_content>
							<div class="dropArea">
      							<div class="dropText">
	        						<i class="fa fa-camera" aria-hidden="true"></i>
	      						</div>
    						</div>
						</#if>
					</#list>
				<#else>
					<#list category.images as image>
						<img class="taxon-image" src="${image.largeURL?html}" />
						<#break>
					</#list>
					<#if !category.images?has_content>
						<div class="dropArea">
      						<div class="dropText">
	        					<i class="fa fa-camera" aria-hidden="true"></i>
	      					</div>
    					</div>
					</#if>	
				</#if>
			</div>
		</#list> 
	</div>
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

