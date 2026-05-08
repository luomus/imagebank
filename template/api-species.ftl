<#include "macro.ftl">

<#if taxa?has_content>
	<@pager "false" />
	<div class="taxon-image-container">
		<#list taxa as taxon>
			<@printTaxonImages taxon/>
		</#list>
	</div>
	<@pager "true" />
<#else>
<p>${text.taxon_tree_no_taxa}</p>
</#if>

<#macro printTaxonImages taxon> 
	<div class="taxon-image-card">
		<h4 class="taxon-image-card-header">
			<@printNamesRank taxon/> 
			<a href="${taxon.id.toURI()}" target="_laji">${taxon.id}</a>
		</h4>
		<#list taxon.categorizedMultimedia.categories as category>
			<div class="taxon-image-category taxon-image-category-type-${category.id}">
				<h4>${category.title.forLocale(locale)?html}</h4>
				<#if category.subcategories?has_content>
					<#list category.subcategories as subcategory>
						<h4>${subcategory.title.forLocale(locale)?html}</h4>
						<@printImages subcategory.images subcategory category taxon/>
					</#list>
				<#else>
					<h4>&nbsp;</h4>
					<@printImages category.images category "" taxon/>
				</#if>
			</div>
		</#list>
		<#if taxon.categorizedMultimedia.uncategorizedImages?has_content>
			<div class="taxon-image-category taxon-image-category-type-uncategorized">
				<h4>${text.uncategorized}</h4>
				<h4>&nbsp;</h4>
				<@printImages taxon.categorizedMultimedia.uncategorizedImages "" "" taxon/>
			</div>
		</#if>
		<div class="taxon-info-grid">
			<div>
				${habitatFormatter.format(taxon, locale)}
			</div>
			<#if taxon.typesOfOccurrenceInFinland?has_content>
				<div>
					<ul>
						<#list taxon.typesOfOccurrenceInFinland as o><li>${occurrenceTypes[o].forLocale(locale)}</li></#list>
					</ul>
				</div>
			</#if>
			<div>${(taxon.TypeOfOccurrenceInFinlandNotes!"")?html}</div>
			<div>
				<#if taxon.occurrences.hasOccurrences()>
				<img class="biogeo-map" src="${staticURL}/biogeo.svg" alt="Biogeographical distribution as a map"  data-active-areas="<#list taxon.occurrences.occurrences as occ>${occ.area?replace(".","_")}<#if occ_has_next>,</#if></#list>" />
				</#if>
				<span class="obs-count"><b>${taxon.observationCountFinland}</b> ${text.obs_count}</span> 
			</div>
		</div>		
	</div>
</#macro>

<#macro printImages images category parentCategory taxon>
	<#list images as image>
		<div class="image-wrapper">
			<@galleryLink category parentCategory taxon/>
				<img class="taxon-image taxon-image-lazy" 
					data-src="${image.largeURL?html}" 
					src="${staticURL}/pixel.gif" 
					<@imageData image/>
				/>
			</a>
			<div class="image-info-box"><@imageCopyright image/></div>
		</div>
		<div class="taxon-image-tools">
			<i class="fa fa-camera add-image-icon" aria-hidden="true"></i> &nbsp;
			<@galleryLink category parentCategory taxon/><i class="fa fa-clone" aria-hidden="true"></i> ${images?size}</a>
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

<#macro galleryLink category parentCategory taxon>
	<a href="#" class="taxon-image-gallery-link" 
			data-taxonid="${taxon.id?html}" 
			data-category="<#if category?has_content>${category.id}<#else>uncategorized</#if>" 
			data-header="<@printNamesRankPlain taxon/> - <#if category?has_content><#if parentCategory?has_content>${(parentCategory.title.forLocale(locale)?html)} </#if>${(category.title.forLocale(locale)?html)}<#else>${text.uncategorized}</#if>">
</#macro>

<#macro pager scrollTop>
<#if lastPage != 1>
<nav class="pager">
    <#if currentPage != 1>
        <button data-page="1" data-scrolltop="${scrollTop}" class="pager-btn">« First</button>
    </#if>
    
    <#if prevPage??>
        <button data-page="${prevPage}" data-scrolltop="${scrollTop}" class="pager-btn">‹ Prev</button>
    </#if>

    <#list (currentPage - 3)..(currentPage + 3) as page>
        <#if page gt 0 && page lte lastPage>
            <button data-page="${page}"  data-scrolltop="${scrollTop}"
                class="pager-btn <#if page == currentPage>active</#if>">
                ${page}
            </button>
        </#if>
    </#list>

    <#if nextPage??>
        <button data-page="${nextPage}" data-scrolltop="${scrollTop}" class="pager-btn">Next ›</button>
    </#if>

    <#if currentPage != lastPage>
        <button data-page="${lastPage}" data-scrolltop="${scrollTop}" class="pager-btn">Last »</button>
    </#if>
</nav>
</#if>
</#macro>

