    </main>

    <footer>
        <ul>
        	<li><a target="_blank" href="https://laji.fi/<#if !locale??>in<#elseif locale != "fi">${locale?html}</#if>">LAJI.FI</a></li>
        	<li><a target="_blank" href="${text.doc_link}">${text.documentation}</a></li>
        	<li>${text.support}: helpdesk@laji.fi</li>
        </ul>
        <div id="logos">
        	<a target="_blank" href="https://laji.fi/<#if !locale??>in<#elseif locale != "fi">${locale?html}</#if>"><img src="${staticURL}/laji.fi_valk.png" alt="FinBIF logo" style="height: 100px; width: auto;"/></a>
        	<a target="_blank" href="https://luomus.fi/<#if !locale??>in<#elseif locale != "fi">${locale?html}</#if>"><img src="${staticURL}/luomus_fiseen_white_1.png" alt="Luomus logo" style="height: 100px; width: auto;"/></a>
        	<a target="_blank" href="https://helsinki.fi/<#if !locale??>in<#elseif locale != "fi">${locale?html}</#if>"><img src="${staticURL}/HY__LC01_LogoFP_3L_B3__NEGA.png" alt="University of Helsinki logo" style="height: 180px; width: auto;"/></a>
        </div>
        <p id="missionstatement">${text.finbif_mission}</p>
        <ul>
        	<li><a target="_blank" href="${text.privacy_policy_link}">${text.privacy_policy}</a></li>
        	<#if locale == "fi">
        		<li><a target="_blank" href="${text.accessibility_statement_link}">${text.accessibility_statement}</a></li>
        	</#if>
        </ul>
    </footer>
</body>
</html>