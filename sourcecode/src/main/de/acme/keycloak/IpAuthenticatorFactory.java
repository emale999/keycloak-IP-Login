package de.acme.keycloak;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import java.util.List;
import java.util.Collections;

// import org.keycloak.models.UserModel;
// import org.keycloak.models.RealmModel;

public class IpAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "ip-authenticator";

	private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.ALTERNATIVE,
        AuthenticationExecutionModel.Requirement.DISABLED
	    };

    @Override
    public String getDisplayType() {
        return "Login per IP";
    	}

    @Override
    public String getId() {
        return PROVIDER_ID;
    	}
    
    @Override
    public Authenticator create(KeycloakSession session) {
        return new IpAuthenticator();
    	}

    @Override
    public void init(Config.Scope config) { }

    @Override
    public void postInit(KeycloakSessionFactory factory) { }

    @Override
    public void close() { }

    @Override
    public boolean isConfigurable() { return false; }

    @Override
    public boolean isUserSetupAllowed() { return false; }
    
    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
			};
		}

    @Override
    public String getReferenceCategory() {
        return null;
	    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
	    }

    @Override
    public String getHelpText() {
        return "Fügt eine 'Login mit 1 Klick'-Option hinzu, wenn die IP-Adresse bei den Attributen eines Benutzers gefunden wurde.";
	    }

	}
