<#include "header.ftl">
<#include "macro.ftl">

		<nav class="breadcrumb" aria-label="Breadcrumb">
		    <ol>
		    	<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
        		<li><a href="${baseURL}/admin">${text.menu_admin}</a></li>
        		<#if taxonSearch??>
        			<li><a href="${baseURL}/admin?taxonSearch=${taxonSearch?html}">${text.menu_taxon_select}: ${taxonSearch?html}</a></li>
        		</#if>
        		<li><@printScientificName taxon/></li>
    		</ol>
		</nav>
		
		<h2>${text.admin_main}</h2>
		
		<h3><@printNames taxon/> | ${taxon.id}</h3>

		<#if multiPrimary><div class="info warning"><p>WARNING: Taxon has MULTIPLE PRIMARY images</p></div></#if>

		<div class="mass-tag-select">
			<div class="mass-tag-select-header"><h5>${text.admin_tag_images}</h5> <span class="ui-icon ui-icon-caret-2-n-s"></span></div>
			<div class="mass-tag-select-body">
				<@select "type" types />
        		<@select "sex" sexes />
        		<@select "lifeStage" lifeStages />
        		<@select "plantLifeStage" plantLifeStages />
        		<@select "side" sides />
        		<div>
        			<button id="mass-tag-clear-button" type="button">${text.clear_all}</button>
        			<button id="mass-tag-done-button" style="display:none" type="button">${text.admin_tag_done}</button>
        		</div>
        	</div>
		</div>
		
<#if taxon.multimedia?has_content>
		<h4>${text.admin_select_image} ...</h4>
<#else>
		<h4>${text.no_images}!</h4>
</#if>


		<div class="image-grid">
		<#list taxon.orderedMultimedia as image>
			<a class="taxon-image" href="${baseURL}/admin/${image.id}?taxonId=${taxon.id}<#if taxonSearch??>&taxonSearch=${taxonSearch?html}</#if>"><img class="admin-image" src="${image.largeURL?html}" <@imageData image/> /></a>
		</#list>
			<form class="image-upload-form" id="imageUploadForm" action="/admin/add" method="POST" enctype="multipart/form-data">
  				<fieldset>
    				<legend>Upload Image</legend>
					    <div id="dropArea">
      						<input type="file" id="fileInput" name="image" accept="image/*" hidden>
      						<div id="dropText">
        						<i class="fa fa-camera" aria-hidden="true"></i>
        						<p>Drag & drop an image here, or click to select one</p>
      						</div>
    					</div>
					    <div id="hiddenImageOption">
      						<label for="hiddenCheckbox">Hidden image</label>
        					<span class="info-icon" title="A hidden image is only visible to administrators and will not appear publicly.">?</span>
        					<input type="checkbox" id="hiddenCheckbox" name="hidden">
        				</div>
					    <button type="submit" class="ui-priority-primary">Submit</button>
  				</fieldset>
			</form>
			
		</div>

<#include "footer.ftl">
