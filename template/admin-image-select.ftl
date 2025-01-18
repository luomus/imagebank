<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<li><a href="${baseURL}/admin?imageSearch=${imageSearch?html}">${text.menu_image_select}: ${imageSearch?html}</a></li>
    		</ol>
		</nav>
		
		<h2>${text.admin_main}</h2>
		
<#if results?has_content>

		<h4>${text.admin_select_image} ...</h4>
		
		<div class="image-grid">
		<#list results as image>
			<a href="${baseURL}/admin/${image.id}?imageSearch=${imageSearch?html}"><img class="admin-image" src="${image.largeURL?html}" <@imageData image/> /></a>
		</#list>
		</div>
<#else>

		<h4>${text.no_images}!</h4>

</#if>		

		
<#include "footer.ftl">
