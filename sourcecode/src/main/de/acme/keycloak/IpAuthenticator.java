package de.acme.keycloak;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.apache.commons.net.util.SubnetUtils;
import java.util.Optional;
import java.util.stream.Stream;
import org.keycloak.authentication.AuthenticationFlowError;

import java.net.InetAddress;  
import java.net.UnknownHostException;  


// import org.keycloak.sessions.AuthenticationSessionModel;
// import org.keycloak.storage.UserStorageManager;  
// import org.keycloak.credential.CredentialModel;
// import org.keycloak.storage.UserStorageManager;
// import org.keycloak.models.utils.KeycloakModelUtils;
// import org.keycloak.models.UserCredentialModel;
// import org.keycloak.credential.UserCredentialStore;
// import org.keycloak.authentication.AuthenticationProcessor;  
// import org.keycloak.authentication.AuthenticationFlowError;
// import org.keycloak.authentication.CredentialValidator;
// import org.keycloak.credential.CredentialProvider;
// import org.keycloak.models.KeycloakSession;
// import org.keycloak.models.RealmModel;
// import org.keycloak.models.UserCredentialModel;
// import org.keycloak.models.UserModel;
// import org.keycloak.models.utils.FormMessage;
// import org.keycloak.sessions.AuthenticationSessionModel;
// import java.util.List;


public class IpAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // SCHRITT 1: Prüfen, ob der Benutzer den IP-Login überspringen will.
        String skip = context.getHttpRequest().getUri().getQueryParameters().getFirst("skip_ip_login");
        if ("true".equals(skip)) {
            context.attempted();
            return;
        	}

        // SCHRITT 2: IP-Prüfung wie gehabt
        UserModel user = findUserByIp(context);
        if (user != null) {
            // Benutzer gefunden -> Zeige unsere spezielle Login-Seite an.
            Response challenge = context.form()
                    .setAttribute("user", user)
                    .createForm("ip-login-page.ftl");
            context.challenge(challenge);
        } else {
            // Kein passender Benutzer gefunden -> Fahre mit dem normalen Flow fort.
            context.attempted();
        	}
    	}

    @Override
    public void action(AuthenticationFlowContext context) {
		UserModel user = findUserByIp(context);
        if (user != null) {
            context.setUser(user);
            context.success();
        } else {
            // Sicherheitshalber, falls die IP sich zwischenzeitlich ändert.
            context.failure(AuthenticationFlowError.UNKNOWN_USER);
        	}
    	}

    private UserModel findUserByIp(AuthenticationFlowContext context) {
        RealmModel realm = context.getRealm();
        KeycloakSession session = context.getSession();
		String clientIp = context.getConnection().getRemoteAddr();
		
        try (Stream<UserModel> userStream = session.users().searchForUserByUserAttributeStream(realm, "ipLogin", "1")) {
            Optional<UserModel> matchingUser = userStream.filter(user -> {
                Optional<String> ipChecker = user.getAttributeStream("ipAddresses").filter(ipBlock -> { 
                	String[] ipBlocks = ipBlock.split(",");
					for (String ipOrCidr : ipBlocks) {
	                	//System.out.println(ipOrCidr); 
	                	if (isIpMatch(clientIp, ipOrCidr.trim())) {
							//System.out.println(clientIp + " match!"); 
							return true;
	                		}
	                	}
	                return false;
                	}).findFirst();
				if (ipChecker.isPresent()) {
	                //System.out.println("Check: "+ipChecker); 
	                return true;
	            } else {
	                return false;
	                }
                }).findFirst();

            if (matchingUser.isPresent()) {
            	// Speichere den gefundenen Benutzer für den nächsten Schritt (action)
            	UserModel user = matchingUser.get();
            	return user;
            	}

            }
		return null;
		}
	
	private boolean isIpMatch(String ip, String ipOrCidr) {
        try {
            if (ipOrCidr.contains("-")) { // Mehrere IPs nach Schreibweise 192.168.1.1-192.168.1.5
				String[] parts = ipOrCidr.split("-");  
				String startIp = parts[0].trim();  
				String endIp = parts[1].trim();  
				try {  
					long ipToCheckLong = ipToLong(ip);  
					long startIpLong = ipToLong(startIp);  
					long endIpLong = ipToLong(endIp);  
					return ipToCheckLong >= startIpLong && ipToCheckLong <= endIpLong;  
				} catch (UnknownHostException e) {  
					e.printStackTrace();  
					return false;  
				}
            } else { // Davon ausgehend, dass es sich nur um eine Adresse handelt
                return ipOrCidr.equals(ip);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
	
	private static long ipToLong(String ip) throws UnknownHostException {  
        InetAddress inetAddress = InetAddress.getByName(ip);  
        byte[] ipBytes = inetAddress.getAddress();  
        long result = 0;  
        for (byte b : ipBytes) {  
            result = (result << 8) | (b & 0xFF);  
	        }
        return result;  
	    }  

    // Leere Methoden
    @Override public boolean requiresUser() { return false; }
    @Override public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) { return true; }
    @Override public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}
    @Override public void close() {}
}
