<#include "header.ftl">
<#include "macro.ftl">

<nav class="breadcrumb" aria-label="Breadcrumb">
	<ol>
		<li><a href="${baseURL}" aria-label="Home" title="${text.menu_home}" class="home">⌂</a></li>
		<li>${text.menu_browse}</li>
	</ol>
</nav>
		
<h2>${text.browse_title}</h2>

<p>Rakenteilla. Tässä osiossa selataan kuvia ja luodaan sisältöä.</p>
<br />


<h3 style="display:inline-block;">${group.name.forLocale(locale)?html}</h3> <a class="change" href="${baseURL}/browse?change=true">${text.change}</a>


<@preferences/>

<div style="clear:both;" />

<div id="browse-panel" class="hidden">
	<div id="browse-tree-header" class="box-header">
		<h5>${text.taxon_tree}</h5> <span class="openclose">${text.close}</span><span class="openclose hidden">${text.open}</span><span class="ui-icon ui-icon-caret-2-n-s"></span>
	</div>
	<div id="browse-taxa-header">
		Taxa header
	</div>
	<div id="browse-tree" class="box">
			<select id="browse-tree-select">
				<option value="MX.family" selected>${taxonRanks["MX.family"].forLocale(locale)}</option>
				<option value="MX.subfamily">${taxonRanks["MX.subfamily"].forLocale(locale)}</option>
				<option value="MX.tribe">${taxonRanks["MX.tribe"].forLocale(locale)}</option>
				<option value="MX.genus">${taxonRanks["MX.genus"].forLocale(locale)}</option>
			</select>
			<div id="browse-tree-content">
			</div>
	</div>
	<div id="browse-taxa" class="box">
			Taxa Long Taxa Content is Long
	</div>
</div>

<#include "footer.ftl">
