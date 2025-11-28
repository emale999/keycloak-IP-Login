<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">Anmeldung bestätigen</#if>
    <#if section = "form">
        <form id="kc-form" class="form-group" action="${url.loginAction}" method="post">
            <h3>Hallo ${user.username!}, willkommen zurück!</h3>
            <p>Ihre IP-Adresse wurde erkannt. Bitte bestätigen Sie Ihre Anmeldung.</p>
            
            <div class="form-group">
                <input type="submit" class="btn btn-primary btn-block" value="Anmeldung abschließen" />
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
