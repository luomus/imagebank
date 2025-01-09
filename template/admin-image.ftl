<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<#if taxonSearch??>
        			<li><a href="${baseURL}/admin?taxonSearch=${taxonSearch?html}">${text.select_taxon}: ${taxonSearch?html}</a></li>
        		</#if>
        		<#if taxon??>
        			<li><a href="${baseURL}/admin/${taxon.id?html}?<#if taxonSearch??>taxonSearch=${taxonSearch?html}"</#if>><@printScientificName taxon/></a></li>
        		</#if>
        		<li>${image.id}</li>
    		</ol>
		</nav>
		
		<h2>${text.admin_main}</h2>
		
		<h3>${text.edit_image} ${image.id}</h3>
		
		<div class="admin-image-meta">Meta here</div>
		<div class="admin-image-thumb"><img src="${image.urls.large?html}" alt=""/></div>
		<div class="admin-image-all">
			<label>Thumbnail</label> <img src="${image.urls.thumbnail?html}" alt=""/>
			<label>Square thumbnail</label> <img src="${image.urls.square?html}" alt=""/>
			<label>Large</label> <img src="${image.urls.large?html}" alt=""/>
			<label>Full</label> <img src="${image.urls.large?html}" alt=""/>
			<label>Original</label> <img src="${image.urls.large?html}" alt=""/>
		</div>
		
		
		
		
<#include "footer.ftl">
