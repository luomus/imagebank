<#include "header.ftl">

		<h2>${text.admin_main}</h2>
		
		<h3>${text.admin_select_image}...</h3>
		
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
