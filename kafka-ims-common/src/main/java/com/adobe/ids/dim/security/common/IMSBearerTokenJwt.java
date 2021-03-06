/*
 * ADOBE CONFIDENTIAL. Copyright 2019 Adobe Systems Incorporated. All Rights Reserved. NOTICE: All information contained
 * herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems Incorporated and its suppliers and are protected
 * by all applicable intellectual property laws, including trade secret and copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

package com.adobe.ids.dim.security.common;

import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class IMSBearerTokenJwt implements OAuthBearerToken {

    private final Logger log = LoggerFactory.getLogger(IMSBearerTokenJwt.class);

    private String value;
    private String principalName;
    private Long startTimeMs;
    private long lifetimeMs;
    private Set<String> scope;

    public IMSBearerTokenJwt(String accessToken, long lifeTime, long startTime) {
        super();
        this.value = accessToken;
        this.principalName = null;
        this.startTimeMs = startTime;
        this.lifetimeMs = startTimeMs + lifeTime;
    }

    public IMSBearerTokenJwt(Map jwtToken, String accessToken) {
        super();
        this.value = accessToken;
        this.principalName = (String) jwtToken.get("client_id");

        if (this.scope == null) {
            this.scope = new TreeSet<>();
        }

        if (jwtToken.get("scope") instanceof String) {
            // IMS scopes come in the form of a comma separated string
            List<String> scopesList = Arrays.asList(jwtToken.get("scope").toString().split("\\s*,\\s*"));
            for (String s : (List<String>) scopesList) {
                this.scope.add(s);
            }
        } else if (jwtToken.get("scope") instanceof List) {
            List<?> scopes = (List<?>) jwtToken.get("scope");
            for (Object s : scopes) {
                this.scope.add((String) s);
            }
        }

        long expiresInMs = Long.parseLong((String) jwtToken.get("expires_in"));
        long creationTimeMs = Long.parseLong((String) jwtToken.get("created_at"));

        this.startTimeMs = creationTimeMs;
        this.lifetimeMs = creationTimeMs + expiresInMs;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Set<String> scope() {
        return scope;
    }

    @Override
    public long lifetimeMs() {
        return lifetimeMs;
    }

    @Override
    public String principalName() {
        return principalName;
    }

    @Override
    public Long startTimeMs() {
        return startTimeMs != null ? startTimeMs : 0;
    }

    @Override
    public String toString() {
        return "IMSBearerTokenJwt{" +
               "value='" + value + '\'' +
               ", lifetimeMs=" + lifetimeMs +
               ", principalName='" + principalName + '\'' +
               ", startTimeMs=" + startTimeMs +
               ", scope=" + scope() +
               '}';
    }
}
