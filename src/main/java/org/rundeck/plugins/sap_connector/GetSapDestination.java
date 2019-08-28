package org.rundeck.plugins.sap_connector;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
/*
 * @author z00294j
 * created on Nov 30 2018
 */
public class GetSapDestination {

  static JCoDestination destination = null;

  public static JCoDestination getDestination(String executionTarget, String systemNumber, String client, String user,
                                              String password, String clientLanguage, String poolCapacity,
                                              String peakLimit) throws Exception {
    System.setProperty("java.library.path", "/Users/z00294j/gitrepo/dsco/rundeck/rundeck-sap-plugin/darwin/sapjco3");
    String DEST = "RFC";
    try {
      Properties connectProperties = new Properties() {{
        setProperty(DestinationDataProvider.JCO_ASHOST, executionTarget);
        setProperty(DestinationDataProvider.JCO_SYSNR, systemNumber);
        setProperty(DestinationDataProvider.JCO_CLIENT, client);
        setProperty(DestinationDataProvider.JCO_USER, user);
        setProperty(DestinationDataProvider.JCO_PASSWD, password);
        setProperty(DestinationDataProvider.JCO_LANG, "en");
        setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");
        setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");
      }};
      createDataFile(DEST, "jcoDestination", connectProperties, executionTarget);
      destination = JCoDestinationManager.getDestination(DEST);
      return destination;
    } catch (JCoException ex) {
      System.out.println("JCoException: " + ex.getMessage());
      throw ex;
    } catch (Exception e) {
      throw e;
    }
  }

  public static void createDataFile(String name, String suffix, Properties properties, String executionTarget) throws Exception {
    File cfg = new File(name + "." + suffix);
    try {
      FileOutputStream fos = new FileOutputStream(cfg, false);
      properties.store(fos, executionTarget);
      fos.close();
    } catch (Exception e) {
      throw e;
    }
  }
}
