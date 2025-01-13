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
		
		<h3>${text.admin_edit_image} ${image.id} <#if taxon??> | <@printScientificName taxon/><#elseif image.meta.documentIds?has_content> | <@list image.meta.documentIds/></#if></h3>
		
		<h4><a href="${image.urls.original?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" target="_blank">${image.urls.original?html}</a></h4>

		<div class="admin-image-edit">
		
			<div class="admin-image-meta">
				<form method="POST" action="${baseURL}/admin/${image.id}?${ref}">
					Meta here</br>
					Meta here</br>
					Meta here</br>
					Meta here</br>
					Meta here</br>
					Meta here</br>
					
					<input type="submit" class="button" id="saveButton" value="${text.save}" />
					<button id="cancelButton""><span class="ui-icon ui-icon-cancel"></span>${text.cancel}</button>
				</form>
			</div>
			
			<div class="admin-image-large"><img src="${image.urls.large?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" alt=""/></div>
		
			<div class="admin-image-all">
				<div><@imageLink image "thumbnail" "Thumbnail"/></div>
				<div><@imageLink image "square" "Square thumbnail"/></div>
				<div><@imageLink image "large" "Large thumbnail"/></div>
				<div><@imageLink image "full" "Full JPEG"/></div>
				<div><@imageLink image "original" "Original image"/></div>
			</div>

		</div>

		<div class="danger-zone">
			<h4>Danger Zone</h4>
			<button id="deleteButton" class="ui-state-error"><span class="ui-icon ui-icon-trash"></span> ${text.admin_delete_image}</button>
			<div class="info">${text.admin_delete_image_note}</div>
		</div>

<#macro imageLink image type label>
	<label><a href="${image.urls[type]?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" target="_blank">${label}</a></label> 
	<a href="${image.urls[type]?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" target="_blank">
		<#if type == "full" || type == "original">
            <img src="${image.urls['large']?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" alt="${label}"/>
        <#else>
            <img src="${image.urls[type]?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" alt="${label}"/>
        </#if>
	</a>
</#macro>

<#include "footer.ftl">
