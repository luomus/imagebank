<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<#if taxonSearch??>
        			<li><a href="${baseURL}/admin?taxon=${taxonSearch?html}">${text.select_taxon}: ${taxonSearch?html}</a></li>
        		</#if>
        		<li><@printScientificName taxon/></li>
    		</ol>
		</nav>
		
		<h2>${text.admin_main}</h2>
		
		<h3><@printNames taxon/> | ${taxon.id}</h3>

		<#if multiPrimary><div class="info warning"><p>MULTIPLE PRIMARY IMAGES</p></div></#if>
		
<#if taxon.multimedia?has_content>

		<h4>${text.admin_select_image} ...</h4>
		
		<div class="image-grid">
		<#list taxon.multimedia as image>
			<a href="${baseURL}/admin/${image.id}?<#if taxonSearch??>taxon=${taxonSearch?html}</#if>"><img class="admin-image" src="${image.largeURL}" <@imageData image/> /></a>
		</#list>
		</div>
<#else>

		<h4>${text.no_images}!</h4>

</#if>		

<#include "footer.ftl">
