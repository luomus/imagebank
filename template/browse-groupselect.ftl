<#include "header.ftl">
<#include "macro.ftl">

<nav class="breadcrumb" aria-label="Breadcrumb">
	<ol>
		<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
		<li>${text.menu_browse}</li>
	</ol>
</nav>
		
<h2>${text.browse_title}</h2>

<h3>${text.group_select}</h3>

<br />
			<select id="groupSelect" name="group" data-placeholder="${text.group_select}">
            	<#assign first = true>
            	<#list taxonGroups as group>
            		<#if !group.hasParents()>
            			<#if first><option value=" ">&nbsp;</option><#assign first = false><#else></optgroup></#if>
            			<optgroup label="${group.name.forLocale(locale)}">
            		</#if>
            		<option value="${group.qname}">${group.name.forLocale(locale)}</option>
				</#list>
				</optgroup>
        	</select>


<script>
$(document).ready(function() {

	$("#groupSelect").chosen({width: "25em", no_results_text: "${text.no_matches}"});
	
	$("#groupSelect").on('change', function() {
		let val = $("#groupSelect").val();
		if (!val) return;
		setPreference("group", val).done(function() {
			window.location.href = '${baseURL}/browse/'+val;
		});
	})
});

</script>


<#include "footer.ftl">
