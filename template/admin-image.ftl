<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<#if imageSearch??>
        			<li><a href="${baseURL}/admin?imageSearch=${imageSearch?html}">${text.menu_image_select}: ${imageSearch?html}</a></li>
        		</#if>
        		<#if taxonSearch??>
        			<li><a href="${baseURL}/admin?taxonSearch=${taxonSearch?html}">${text.menu_taxon_select}: ${taxonSearch?html}</a></li>
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
				<form method="POST" action="${baseURL}/admin/${image.id}?${ref}" id="adminMetaForm">
					
	<#assign meta = image.meta/>	
		
    <fieldset>
        <legend>${text.group_authors}</legend>
		<@inputMulti "capturers" 80 meta.capturers />
		<@input "rightsOwner" 80 meta.rightsOwner />
        <@select "license" licenses meta.license/>
		<@input "captureDateTime" 30 meta.captureDateTime />
    </fieldset>

    <fieldset>
        <legend>${text.group_taxon_images}</legend>
		<@inputMulti "taxonIds" 18 meta.identifications.taxonIds />
		<@inputMulti "verbatim" 80 meta.identifications.verbatim />
		<@inputMulti "primaryForTaxon" 18 meta.primaryForTaxon />
        <@select "type" types meta.type />
        <@selectMulti "sex" sexes meta.sex />
        <@selectMulti "lifeStage" lifeStages meta.lifeStage />
        <@selectMulti "plantLifeStage" plantLifeStages meta.plantLifeStage />
        <@select "side" sides meta.side />
    </fieldset>

    <fieldset>
        <legend>${text.group_captions}</legend>
		<@input "caption" 60 meta.caption />
		<@input "taxonDescriptionCaptionFI" 80 meta.taxonDescriptionCaption["fi"] />
		<@input "taxonDescriptionCaptionSV" 80 meta.taxonDescriptionCaption["sv"] />
		<@input "taxonDescriptionCaptionEN" 80 meta.taxonDescriptionCaption["en"] />
	</fieldset>

    <fieldset>
        <legend>${text.group_misc}</legend>
		<@inputNumber "sortOrder" meta.sortOrder />
		<@inputMulti "documentIds" 40 meta.documentIds />
		<@inputMulti "tags" 40 meta.tags />
		<@selectBool "fullResolutionMediaAvailable" meta.fullResolutionMediaAvailable!false />
    </fieldset>
					
					<input type="submit" class="button" id="saveButton" value="${text.save}" />
					<button id="cancelButton" type="button"><span class="ui-icon ui-icon-cancel"></span>${text.cancel}</button>
			
	 <fieldset id="unmodifieable-meta-fields" class="long-labels">
        <legend>${text.group_unmodifiable}</legend>

        <label for="sourceSystem">${text.label_sourceSystem}:</label>
        <span><@mapV meta.sourceSystem sourceSystems /><br>

        <label for="uploadedBy">${text.label_uploadedBy}:</label>
        <span>${(meta.uploadedBy!"")?html}</span><br>
        
        <label for="uploadDateTime">${text.label_uploadDateTime}:</label>
        <span>${(meta.uploadDateTime!"")?html}</span><br>

        <label for="secret">${text.label_secret}:</label>
        <span>${meta.secret?string(text.yes, text.no)}</span><br>

        <label for="originalFilename">${text.label_originalFilename}:</label>
        <span>${meta.originalFilename?html}</span><br>
    </fieldset>

				</form>

			</div>
			
			<div class="admin-image-large">
				<@imageLink image "original" ""/>
			</div>
		
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
		<#if type == "original">
            <img src="${image.urls['full']?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" alt="${label}"/>
        <#else>
            <img src="${image.urls[type]?html}<#if image.secretKey??>?secret=${image.secretKey}</#if>" alt="${label}"/>
        </#if>
	</a>
</#macro>

<#include "footer.ftl">
