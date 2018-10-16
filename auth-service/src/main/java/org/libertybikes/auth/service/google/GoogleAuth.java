package org.libertybikes.auth.service.google;

import java.net.URI;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.libertybikes.auth.service.ConfigBean;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@Path("/GoogleAuth")
@ApplicationScoped
public class GoogleAuth {

    @Inject
    ConfigBean config;

    @GET
    public Response getGoogleCallbackURL(@Context HttpServletRequest request) {

        JsonFactory jsonFactory = new JacksonFactory();
        HttpTransport httpTransport = new NetHttpTransport();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow(httpTransport, jsonFactory, config.google_key, config.google_secret, Arrays
                        .asList("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email"));

        try {
            // google will tell the users browser to go to this address once
            // they are done authing.
            String callbackURL = config.authUrl + "/GoogleCallback";
            request.getSession().setAttribute("google", flow);

            String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(callbackURL).build();

            // send the user to google to be authenticated.
            return Response.temporaryRedirect(new URI(authorizationUrl)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

}
