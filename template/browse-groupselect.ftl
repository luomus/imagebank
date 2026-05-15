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

<div id="recent-groups" class="hidden">
	<h4>${text.group_history}</h4>
	<div id="recent-groups-section" class="group-browser">
	</div>
</div>

	<input type="text" id="group-filter" placeholder="${text.group_filter}" />
	
	<#assign first = true>
	<#list taxonGroups as group>
    	<#if !group.hasParents()>
			<#if first><#assign first = false><#else></div></#if>
				<h4 class="group-header" id="${group.qname}">${group.name.forLocale(locale)?html}</h4>
				<div class="group-browser" id="group-browser-main">
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
        .done(function() {
            window.location.href = targetUrl;
        });
});

$(document).ready(function() {
	$(".group-card img").each(function(index) {
        const bg = colorParts(index, 6, 1);
        $(this).css({
      		"--card-h": bg.h + "deg",
      		"--card-s": bg.s + "%",
      		"--card-l": bg.l + "%"
    	});
    });
});

function color(index, totalColors = 20, lightness = 50) {
	const hueStep = 360 / totalColors;
    const hue = (index % totalColors) * hueStep;
    const saturation = 60 + ((index * 7) % 40); // 60-99%
	return "hsl(" + hue + "," + saturation + "%," + lightness + "%)";
}

function colorParts(index, totalColors = 20, lightness = 50) {
  const hueStep = 360 / totalColors;
  const h = (index % totalColors) * hueStep;
  const s = 60 + ((index * 7) % 40); // 60–99%
  return { h, s, l: lightness };
}

$(document).on("input", "#group-filter", function () {
    var query = $(this).val().toLowerCase().trim();
    $("#group-browser-main").each(function () {
        var $browser = $(this);
        var $cards = $browser.find(".group-card");

        var visibleCount = 0;

        $cards.each(function () {
            var $card = $(this);
            var name = $card.find(".group-name").text().toLowerCase();
            var match = name.indexOf(query) !== -1;
            $card.toggle(match);
            if (match) visibleCount++;
        });

        // hide empty sections
        var $header = $browser.prev(".group-header");
        if (visibleCount === 0) {
            $browser.hide();
            $header.hide();
        } else {
            $browser.show();
            $header.show();
        }
    });
});

function renderRecentGroups() {
    var recent = getArrayPreference("groupHistory");
    if (!recent || recent.length === 0) return;

    var $container = $("#recent-groups");
    var $section = $("#recent-groups-section");
    
    for (var i = 0; i < recent.length; i++) {
        var groupId = recent[i];
        var $card = $(".group-card[data-group='" + groupId + "']").first();
        if ($card.length) {
            $section.append($card.clone(true));
        }
    }
    if ($section.children().length > 0) {
        $container.show();
    }
}

$(document).ready(function () {
    renderRecentGroups();
});

</script>

<style>

#group-filter {
	padding: 3px;
	margin-top: 1em;
	border: 1px solid #ddd;
	border-radius: 3px;
	width: 30em;
	font-size: 120%;
}

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
	background: white;
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
	filter: drop-shadow(16px 16px 12px) saturate(var(--card-s)) hue-rotate(var(--card-h)) sepia(50%);
}
.group-name {
	font-size: 1.1em;
	font-weight: 500;
	color: #333;
}

</style>

<#include "footer.ftl">
