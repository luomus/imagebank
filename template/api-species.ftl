<#include "macro.ftl">

<#if taxa?has_content>
	<@pager/>
	<#list taxa as taxon>
		<@printNameRank taxon/>
	</#list>
	<@pager/>
<#else>
<p>TODO EII OOO</p>
</#if>

<#macro pager>
<#if lastPage != 1>
<nav class="pager">
    <#if currentPage != 1>
        <button onclick="loadSpecies(1)" class="pager-btn">« First</button>
    </#if>
    
    <#if prevPage??>
        <button onclick="loadSpecies(${prevPage})" class="pager-btn">‹ Prev</button>
    </#if>

    <#list (currentPage - 3)..(currentPage + 3) as page>
        <#if page gt 0 && page lte lastPage>
            <button onclick="loadSpecies(${page})"
                class="pager-btn <#if page == currentPage>active</#if>">
                ${page}
            </button>
        </#if>
    </#list>

    <#if nextPage??>
        <button onclick="loadSpecies(${nextPage})" class="pager-btn">Next ›</button>
    </#if>

    <#if currentPage != lastPage>
        <button onclick="loadSpecies(${lastPage})" class="pager-btn">Last »</button>
    </#if>
</nav>
</#if>
</#macro>

