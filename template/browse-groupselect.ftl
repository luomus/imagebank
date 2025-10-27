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

<div id="groupBrowser" class="group-browser"></div>

<script>
const groups = ${taxonGroupsJson}; // Pre-generate JSON in controller
const baseURL = '${baseURL}';

function renderGroups(parentQname = null) {
	const container = $("#groupBrowser");
    container.empty();

    const children = groups.filter(g => {
        if (!g.parentQnames || g.parentQnames.length === 0)
            return parentQname === null;
        return g.parentQnames.includes(parentQname);
    });

	// Render group cards
	groups.forEach(group => {
		const img = '${staticURL}/group-icons/' + group.qname + '.png';
		const card = $('' +
			'<div class="group-card" data-qname="' + group.qname + '">' +
			'<img src="' + img + '" alt="' + group.name + '" />' +
    		'<div class="group-name">' + group.name + ': ' + group.qname +'</div>' +
			'</div>');
		card.on('click', () => {
			//const hasChildren = groups.some(g => g.parentQname === group.qname);
			//if (hasChildren) {
			//	renderGroups(group.qname);
			//} else {
				window.location.href = baseURL + '/browse/' + group.qname;
			//}
		});
		container.append(card);
	});
}

$(document).ready(() => {
	renderGroups(null);
});
</script>

<style>
.group-browser {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
	gap: 1.2em;
	margin-top: 1.5em;
}
.group-card {
	border: 1px solid #ddd;
	border-radius: 0.8em;
	padding: 1em;
	text-align: center;
	cursor: pointer;
	box-shadow: 0 2px 4px rgba(0,0,0,0.1);
	transition: transform 0.15s ease, box-shadow 0.15s ease;
	background-color: #fff;
}
.group-card:hover {
	transform: translateY(-3px);
	box-shadow: 0 4px 8px rgba(0,0,0,0.15);
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
.back-btn {
	margin-bottom: 1em;
	padding: 0.4em 0.8em;
	background: #eee;
	border: none;
	border-radius: 0.5em;
	cursor: pointer;
}
</style>

<#include "footer.ftl">
