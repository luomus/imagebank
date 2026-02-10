<ul class="keyboard-shortcuts">
	<li><span class="keycap">Esc</span> ${text.esc_closes}</li>
	<li><span class="keycap">&larr;</span> <span class="keycap">&rarr;</span> ${text.arrows_browse}</li>
</ul>

<div id="taxon-image-gallery" class="image-grid">

<#list images as image>
	<div>
		<img class="taxon-image" src="${image.fullURL?html}" />
		<a href="${image.fullURL?html}" target="full"><i class="fa fa-download" aria-hidden="true"></i> ${text.download_image}</a>
	</div>
</#list>

</div>

<ul class="keyboard-shortcuts">
	<li><span class="keycap">Esc</span> ${text.esc_closes}</li>
	<li><span class="keycap">&larr;</span> <span class="keycap">&rarr;</span> ${text.arrows_browse}</li>
</ul>
