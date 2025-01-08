<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<#if ref??>
        			<li><a href="${baseURL}/admin?taxon=${ref}">${text.select_taxon}: ${ref}</a></li>
        		</#if>
        		<li><@printScientificName taxon/></li>
    		</ol>
		</nav>
		
		<h2>${text.admin_main}</h2>
		
		<h3><@printNames taxon/> | ${taxon.id}</h3>
		
<#if taxon.multimedia?has_content>

		<h4>${text.admin_select_image} ...</h4>
		
		<div class="image-grid">
		<#list taxon.multimedia as image>
			<img class="admin-image" src="${image.largeURL}" <@imageData image/> />
		</#list>
		</div>
<#else>

		<h4>${text.no_images}!</h4>

</#if>		

<#include "footer.ftl">
