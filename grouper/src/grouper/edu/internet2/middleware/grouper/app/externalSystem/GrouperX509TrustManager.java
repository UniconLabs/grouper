/****
 * Copyright 2022 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/

package edu.internet2.middleware.grouper.app.externalSystem;

import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extend the default SSL truststore with entries listed in grouper.properties. Example:
 *    truststore.otherHttp.cert = ....
 *    truststore.otherLdaps.cert = ....
 */
public class GrouperX509TrustManager implements X509TrustManager {

  // matches truststore.<non-whitespace>.cert
  private static final Pattern TRUSTSTORE_CERT_PATTERN = Pattern.compile("^truststore\\.(\\S+)\\.cert$");
  private static final Log LOG = GrouperUtil.getLog(GrouperX509TrustManager.class);

  private static X509TrustManager defaultTrustManager;

  private static X509TrustManager grouperCertStoreTrustManager;

  private static boolean initialized = false;
  private static boolean customized = false;


  // Singleton constructor initialized at class startup
  private static final GrouperX509TrustManager ourInstance = new GrouperX509TrustManager();

  /**
   * gets the singleton instance
   */
  public static GrouperX509TrustManager getInstance() {
    return ourInstance;
  }

  public GrouperX509TrustManager() {
    // Normally a singleton pattern has a private constructor; but the Ldaptive property-based trust manager setup expects
    // a public constructor for instantiation, not getInstance()

    if (!initialized) {
      try {
        // Set up this custom truststore if there are any "truststore.*.cert" entries in grouper.properties. Also
        // sets isCustomized() to true, so client classes can decide whether they should use this trust manager
        customizeDefaultSslContext();

        if (this.customized) {
          // this may not be doing much; this TLS context is only working on a copy. Client classes tend to get their
          // own copy of the "TLS" context; and not the "default" context. So client classes will need to be modified
          // to customize their sslContext with this custom trust manager
          SSLContext sslContext = SSLContext.getInstance("TLS");
          sslContext.init(null, new TrustManager[]{this}, null);
          SSLContext.setDefault(sslContext);
        }
      } catch (Exception e) {
        LOG.error("Failed to initialize custom truststore");
      }
      initialized = true;
    }
  }

  /**
   * If there are trusted certs stored in grouper.properties, set up a custom TrustManager using those as trusted
   * certificates during SSL negotiation. Fall back on the default use these fall back to the default TrustManager
   */
  private static void customizeDefaultSslContext() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
    customized = false;

    CertificateFactory cf = CertificateFactory.getInstance("X.509");

    // Scan properties for any defined certs. If nothing is configured, we don't need to do anything
    Map<String, Certificate> aliasCertMap = new HashMap<>();
    for (String key: GrouperConfig.retrieveConfig().propertyNames()) {
      Matcher matcher = TRUSTSTORE_CERT_PATTERN.matcher(key);
      if (matcher.matches()) {
        String alias = matcher.group(1);
        String certString = GrouperConfig.retrieveConfig().propertyValueString(key);

        if (GrouperUtil.isBlank(certString)) {
          LOG.error("Blank certificate in grouper.property value for " + key);
        } else {
          Certificate cert;
          try {
            cert = cf.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(certString)));
            aliasCertMap.put(alias, cert);
            LOG.debug("Added certificate from property " + key + " into Grouper custom truststore");
          } catch (CertificateException e) {
            LOG.error("Failed to convert property value " + key + " into a certificate; skipping this entry", e);
          }
        }
      }
    }

    if (aliasCertMap.isEmpty()) {
      return;
    }

    customized = true;
    LOG.debug("Successfully parsed " + aliasCertMap.size() + " certificates from grouper.properties");

    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    // Using null here initialises the TMF with the default trust store.
    tmf.init((KeyStore) null);

    // get the default trust manager
    for (TrustManager tm : tmf.getTrustManagers()) {
      if (tm instanceof X509TrustManager) {
        defaultTrustManager = (X509TrustManager) tm;
        break;
      }
    }

    // Set up custom truststore, and add the certs to it
    KeyStore myTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    myTrustStore.load(null);
    for (Map.Entry<String, Certificate> entry: aliasCertMap.entrySet()) {
      myTrustStore.setCertificateEntry(entry.getKey(), entry.getValue());
    }

    // Set a trustManager factory with our certs
    tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(myTrustStore);

    // Get the default trust manager from the custom factory
    for (TrustManager tm : tmf.getTrustManagers()) {
      if (tm instanceof X509TrustManager) {
        grouperCertStoreTrustManager = (X509TrustManager) tm;
        break;
      }
    }
  }

  /**
   * true if there were any certificates defined in grouper.properties; if false, caller should not need to customize ssl handling
   * @return true if contains custom trusted certificates
   */
  public boolean isCustomized() {
    return customized;
  }

  /**
   *
   * @see X509TrustManager#checkClientTrusted(X509Certificate[], String)
   */
  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    defaultTrustManager.checkClientTrusted(chain, authType);
  }

  /**
   * @see X509TrustManager#checkServerTrusted(X509Certificate[], String)
   */
  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    try {
      grouperCertStoreTrustManager.checkServerTrusted(chain, authType);
    } catch (CertificateException | NullPointerException e) {
      // This will throw another CertificateException if this fails too.
      defaultTrustManager.checkServerTrusted(chain, authType);
    }
  }

  /**
   * @see X509TrustManager#getAcceptedIssuers()
   */
  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return defaultTrustManager.getAcceptedIssuers();
  }
}
