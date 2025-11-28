<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">${ipLoginTitle}</#if>
    <#if section = "form">
        <form id="kc-form" class="form-group" action="${url.loginAction}" method="post">
            <h3>${msg("ipLoginIPfound")} '${user.username!}'.</h3>
            <p>${msg("ipLoginDetectedInfo")}</p>
            <p>${msg("ipLoginDetectedInfo2")}</p>
            
            <div id="kc-form-buttons" class="form-group">
                <input type="submit" class="btn btn-primary btn-block" value='${msg("ipLoginSubmitButton")}'/>
            </div>
        </form>

        <hr/>
        
    </#if>
</@layout.registrationLayout>
