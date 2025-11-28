# Goal of this project
We wanted to move from a proprietary OpenID system to keycloak but one big point on the specification sheet was "login via IP". 
If you are redirected to the keycloak login page, keycloak should recognize the IP address for some special users and log then in without the need for an user name and password.

Unfortunately I didn't find a project like this on the whole world wide web so I created my own.

# 1st step: Compile provider plugin
(or use the `.jar`-file from the releases-page - but I only have one compiled for keycloak 26.2.4)
- clone the `sourcecode` folder, make your changes and compile with maven: `mvn clean install`. The needed `.jar` file will be in `sourcecode/target` if everything went fine

# 2nd step: Install plugin
- Place the `.jar`-file in the keycloak providers folder `/opt/keycloak/.../providers/`
- clone the `theme` folder, make your changes and place it in the keycloak theme folder `/opt/keycloak/.../themes/` (you have to place the whole acme folder there, not just its content! You can rename "acme" to everything you like)

# 3rd step: keycloak config check (optional)
In my case keycloak wasn’t able to determine the IP of the client that's requesting the login page. I had to make a change in `/opt/keycloak/.../conf/keycloak.env`

Check the line `KC_PROXY_HEADERS=xforwarded` if it's really `xforwarded` and not just `forwarded` without `x`

This is only necessary if installation went fine but at the end keycloak doesn't recognize your IP address and the common login mask appears.

# 4th step: Reboot needed

Reboot keycloak. Was
```
systemctl stop sso.acmekeycloak.de.service
systemctl start sso.acmekeycloak.de.service
```
in my case, depends on your installation.

If keycloak comes up again after some seconds, it seems to like your new provider plugin.

# 5th step: Settings

- Choos the theme "acme" (or whatever you renamed it to) under "Realm Settings" > "Themes" > "Login Theme"
- Under "Authentification" open the "browser" flow
- Choos Action > Duplicate at the top right and give your new flow a name like "browser-ip"
- Add Execution and search for "IP". "Login per IP" (login via ip) should apper if the installation of the provider plugin was successful
- Add it and move it behind "Cookie". Requirement is "Alternative"
- Again in the Action menu at the top right now choose "Bind flow" and bind it to the "browser flow". Now "login via ip" will also be a possibility to login when trying to login via browser interface
- In "Realm Settings" > "General" at the bottom "Unmanaged Attributes" should be enabled. We need this at the next step

# 6th step: Use it

- Edit the user that should be able to login via IP
- There should be a tab "Attributes" after the "Details" tab. Open it
- Add at least this two attributes
  - key: `ipLogin` value: `1`
  - key: `ipAddresses` value: `<ip>`

Only users with attribute `ipLogin=1` will have the possibility to login via IP. So there's a fast way to activate and de-activate this feature on per-user-basis.
You can add as many lines with key `ipAddresses` as you like, if there are very much IP adresses to add (the value field has a restricted char length). You can also add several IPs comma seperated in one field, eg. `127.0.0.1,192.168.178.2` or a IP range like `127.0.0.1-127.0.0.10`

# 7th step: Try it

I keep fingers crossed that if you now add the attributes `ipLogin=1` and `ipAddresses=<your public IP>` to your user attributes and try to login in a new incognito browser window you should hopefully just have to click a button "Yes, I want to login" instead of enter your credentials.

There's a link to the common login  mask on this page as well if you don't want to login with your IP.
