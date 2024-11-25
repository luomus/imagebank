<!DOCTYPE html>
<html lang="${locale}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${text.imagebank} - ${text.tagline} | ${text.finbif}</title>
    <link rel="stylesheet" href="${staticURL}/main.css?${staticContentTimestamp}">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.3/themes/ui-lightness/jquery-ui.css">
    <link rel="icon" href="${staticURL}/laji.ico?${staticContentTimestamp}" type="image/x-icon">
    <script
			  src="https://code.jquery.com/jquery-3.7.1.min.js"
			  integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
			  crossorigin="anonymous"></script>
    <script
			  src="https://code.jquery.com/ui/1.14.0/jquery-ui.min.js"
			  integrity="sha256-Fb0zP4jE3JHqu+IBB9YktLcSjI1Zc6J2b6gTjB0LpoM="
			  crossorigin="anonymous"></script>

    <script>
    	<#include "javascript.ftl">
    </script>
</head>
<body>
    <header>
        <h1><a href="${baseURL}">${text.finbif_short} ${text.imagebank}</a></h1>
        <p><a href="${baseURL}">${text.tagline}</a></p>
    </header>

    <nav>
        <ul>
            <li><a href="${baseURL}/browse">${text.menu_browse}</a></li>
            <li><a href="${baseURL}/curate">${text.menu_curate}</a></li>
            <#if user?? && user.admin><li><a href="${baseURL}/admin">${text.menu_admin}</a></li></#if>
            <li>🌍
                <select id="locale-selector" onchange="changeLocale()">
                    <option value="en" <#if locale == "en">selected="selected"</#if> >English</option>
                    <option value="fi" <#if locale == "fi">selected="selected"</#if> >Suomi</option>
                    <option value="sv" <#if locale == "sv">selected="selected"</#if> >Svenska</option>
                </select>
            </li>
            <li class="login-info" id="user-info">
            	<#if user??>
            		${text.logged_in_as} <span id="username">${user.fullName?html} </span>
            		<#if user.admin><span id="userrole" class="admin">${user.type}</span></#if>
            		<a href="${baseURL}/logout" class="ui-button" id="logout">${text.logout}</a>
            	<#else>
            		<a href="${baseURL}/login" class="ui-button" id="login">${text.login}</a>
            	</#if>
            </li>
        </ul>
    </nav>

    <main>
    	<#if errorMessage??>
    		<div class="error">
    			<h3>${text.error_header}</h3>
    			<p>${errorMessage?html}</p>
    		</div>
    	</#if>
        <h2>Main Content Area</h2>
        <p>This is the main content section.</p>
    </main>

    <footer>
        <ul>
        	<li><a href="https://laji.fi/in">LAJI.FI</a></li>
        	<li><a href="https://info.laji.fi/TODO">${text.documentation}</a></li>
        	<li>${text.support}: helpdesk@laji.fi</li>
        </ul>
    </footer>
</body>
</html>