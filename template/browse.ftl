<#include "header.ftl">
<#include "macro.ftl">

<nav class="breadcrumb" aria-label="Breadcrumb">
	<ol>
		<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
		<li>${text.menu_browse}</li>
	</ol>
</nav>
		
<h2>${text.browse_title}</h2>

<@preferences/>

<div style="clear:both;" />

<div id="browse-panel">
	<div id="browse-tree-header" class="box-header">
		<h5>Heimot</h5> <span class="ui-icon ui-icon-caret-2-n-s"></span>
	</div>
	<div id="browse-taxa-header">
		Taxa header
	</div>
	<div id="browse-tree" class="box">
			Tree</br>
			Some here</br>
	</div>
	<div id="browse-taxa" class="box">
			Taxa Long Taxa Content is Long
	</div>
</div>

<#include "footer.ftl">
