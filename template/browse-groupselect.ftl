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

	<ol class="parent-quicklinks">
	<#list taxonGroups as group>
    	<#if !group.hasParents()>
			<li><a href="#${group.qname}">${group.name.forLocale(locale)?html}</a></li>
		</#if>
	</#list>
	<ol>
	
	<#assign first = true>
	<#list taxonGroups as group>
    	<#if !group.hasParents()>
			<#if first><#assign first = false><#else></div></#if>
				<h4 class="group-header" id="${group.qname}">${group.name.forLocale(locale)?html}</h4>
				<div class="group-browser">
		</#if>
		<a class="group-card" href="${baseURL}/browse/${group.qname}" data-group="${group.qname}">
			<img src="${staticURL}/group-icons/${group.qname}.png" alt="${group.name.forLocale(locale)?html}" />
   			<div class="group-name">${group.name.forLocale(locale)?html}</div>
   		</a>
	</#list>
	</div>

<div style="height: 400px;"></div>

<script>
$(document).on("click", ".group-card", function (e) {
    e.preventDefault();

    const $link = $(this);
    const group = $link.data("group");
    const targetUrl = $link.attr("href");

    if ($link.data("busy")) return;
    $link.data("busy", true).addClass("is-loading");

    setPreference("group", group)
        .done(function () {
            window.location.href = targetUrl;
        });
});

$(document).ready(function() {
	$(".group-card img").each(function(index) {
        var bgColor = color(index, 20, 99);
        var shadowColor = color(index, 20, 30);
        $(this).parent().css("--card-bg", bgColor);
		$(this).parent().css("--card-shadow", shadowColor);
    });
});

function color(index, totalColors = 20, lightness = 50) {
	const hueStep = 360 / totalColors;
    const hue = (index % totalColors) * hueStep;
    const saturation = 60 + ((index * 7) % 40); // 60-99%
	return "hsl(" + hue + "," + saturation + "%," + lightness + "%)";
}

</script>

<style>
.group-header {
	margin-top: 2em;
	margin-bottom: 0.5em;
	width: 100%;
	border-bottom: 1px solid rgb(18, 65, 107);
}

.group-browser {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
	gap: 1.5em;
}
.group-card {
	border: 1px solid white;
	border-radius: 0.8em;
	padding: 1em;
	text-align: center;
	cursor: pointer;
	box-shadow: 1px 1px 5px 1px var(--card-shadow);
	background-color: var(--card-bg);
	transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.3s, background-color 0.3s;
}
.group-card:hover {
	transform: translateY(-3px);
	box-shadow: 2px 4px 8px rgba(0,0,0,0.2);
	background-color: white;
	border: 1px solid #ddd;
}
.group-card img {
	width: 64px;
	height: 64px;
	object-fit: contain;
	margin-bottom: 0.5em;
}
.group-name {
	font-size: 1.1em;
	font-weight: 500;
	color: #333;
}
.parent-quicklinks {
	margin-top: 1em;
}
.parent-quicklinks li {
	display: inline-block;
	padding: 0.3em;
	margin: 0.5em;
}
.parent-quicklinks li a:hover {
	text-underline-offset: 5px;
	text-decoration-thickness: 3px;
}
.parent-quicklinks li a {
	text-decoration: underline;
	text-decoration-thickness: auto;
	text-underline-offset: 3px;
	text-decoration-thickness: 1px;
}

</style>

<#include "footer.ftl">
