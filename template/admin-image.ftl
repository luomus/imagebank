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
		
		<h3>${text.edit_image} ${image.id} <#if taxon??> | <@printScientificName taxon/><#elseif image.meta.documentIds?has_content> | <@list image.meta.documentIds/></#if></h3>
		
		<h4><a href="${image.urls.original?html}" target="_blank">${image.urls.original?html}</a></h4>

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
					<button id="cancelButton" onclick="confirm('${text.cancel_confirm}');"><span class="ui-icon ui-icon-cancel"></span>${text.cancel}</button>
				</form>
			</div>
			
			<div class="admin-image-large"><img src="${image.urls.large?html}" alt=""/></div>
		
			<div class="admin-image-all">
				<div><label><a href="${image.urls.thumbnail?html}" target="_blank">Thumbnail</a></label> <a href="${image.urls.thumbnail?html}" target="_blank"><img src="${image.urls.thumbnail?html}" alt="Thumbnail"/></a></div>
				<div><label><a href="${image.urls.square?html}" target="_blank">Square thumbnail</a></label> <a href="${image.urls.square?html}" target="_blank"><img src="${image.urls.square?html}" alt="Square thumbnail"/></a></div>
				<div><label><a href="${image.urls.large?html}" target="_blank">Large</a></label> <a href="${image.urls.large?html}" target="_blank"><img src="${image.urls.large?html}" alt="Large"/></a></div>
				<div><label><a href="${image.urls.full?html}" target="_blank">Full</a></label> <a href="${image.urls.full?html}" target="_blank"><img src="${image.urls.large?html}" alt="Full"/></a></div>
				<div><label><a href="${image.urls.original?html}" target="_blank">Original</a></label> <a href="${image.urls.original?html}" target="_blank"><img src="${image.urls.large?html}" alt="Original"/></a></div>
			</div>

		</div>

		<div class="danger-zone">
			<h4>Danger Zone</h4>
			<button class="ui-state-error"><span class="ui-icon ui-icon-trash"></span> ${text.delete_image}</button>
			<div class="info">${text.delete_image_note}</div>
		</div>
		
<#include "footer.ftl">
