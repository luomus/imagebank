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
		<i class="fa fa-bars" aria-hidden="true"></i>
		<h5 class="browse-taxa-headertext">${text.taxon_tree}</h5>
	</div>
	<div id="browse-tree" class="box">
			<select id="browse-tree-select">
				<option value="MX.family" selected>${taxonRanks["MX.family"].forLocale(locale)?html}</option>
				<option value="MX.subfamily">${taxonRanks["MX.subfamily"].forLocale(locale)?html}</option>
				<option value="MX.tribe">${taxonRanks["MX.tribe"].forLocale(locale)?html}</option>
				<option value="MX.genus">${taxonRanks["MX.genus"].forLocale(locale)?html}</option>
			</select>
			<div id="browse-tree-content">
			</div>
	</div>
	<div id="browse-taxa" class="box"></div>
</div>

<#include "footer.ftl">
