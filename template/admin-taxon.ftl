<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<li><@printScientificName taxon/></li>
    		</ol>
		</nav>
		
		<h2>${text.admin_main}</h2>
		
		<h3><@printNames taxon/></h3>
		
		<h4>${text.admin_select_image}...</h4>
		
<#if taxon.multimedia?has_content>
		<div class="image-grid">
		<#list taxon.multimedia as image>
			<img src="${image.thumbnailURL}" />
		</#list>
		</div>
<#else>
		NO EI IMAGE KUVAAA TÄL TAXONIL
</#if>		

<#include "footer.ftl">
