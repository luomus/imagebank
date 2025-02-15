    </main>

    <footer>
        <ul>
        	<li><a href="https://laji.fi/<#if !locale??>in<#elseif locale != "fi">${locale}</#if>">LAJI.FI</a></li>
        	<li><a href="https://info.laji.fi/TODO">${text.documentation}</a></li>
        	<li>${text.support}: helpdesk@laji.fi</li>
        	<li><a href="${text.privacy_policy_link}">${text.privacy_policy}</a></li>
        </ul>
        <div id="logos">
        	<a href="https://laji.fi/<#if !locale??>in<#elseif locale != "fi">${locale}</#if>"><img src="${staticURL}/laji.fi_valk.png" alt="FinBIF logo" style="height: 120px; width: auto;"/></a>
        	<a href="https://luomus.fi/<#if !locale??>in<#elseif locale != "fi">${locale}</#if>"><img src="${staticURL}/luomus_fiseen_white_1.png" alt="Luomus logo" style="height: 120px; width: auto;"/></a>
        	<a href="https://helsinki.fi/<#if !locale??>in<#elseif locale != "fi">${locale}</#if>"><img src="${staticURL}/HY__LC01_LogoFP_3L_B3__NEGA.png" alt="University of Helsinki logo" style="height: 220px; width: auto;"/></a>
        </div>
        <p id="missionstatement">${text.finbif_mission}</p>
    </footer>
</body>
</html>