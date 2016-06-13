package org.gluu.credmgr.service;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.service.error.OPException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@IntegrationTest
public class OxauthServiceIntTest {

    private final Logger log = LoggerFactory.getLogger(OxauthServiceIntTest.class);

    @Value("${credmgr.gluuIdpOrg.host}")
    private String host;

    @Inject
    private OxauthService oxauthService;

    @Test
    public void getOpenIdConfigurationTest() {
        try {
            oxauthService.getOpenIdConfiguration(host);
        } catch (OPException e) {
            Assert.fail();
        }
        try {
            oxauthService.getOpenIdConfiguration(host + ".com");
            Assert.fail();
        } catch (OPException e) {
            log.info("passed");
        }
    }


    @Test
    public void getAuthorizationUriTest() {
        try {
            String authorizationUri = oxauthService.getAuthorizationUri(host, null, null, null, null);
            Assert.assertNotNull(authorizationUri);
        } catch (OPException e) {
            Assert.fail();
        }
    }

    @Test
    public void getLogoutUriTest() {
        try {
            String logoutUri = oxauthService.getLogoutUri(host, null, null);
            Assert.assertNotNull(logoutUri);
        } catch (OPException e) {
            Assert.fail();
        }
    }
}
