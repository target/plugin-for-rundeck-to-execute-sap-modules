package org.rundeck.plugins.sap_connector;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import org.rundeck.plugins.configurations.PluginConfig;

/*
 * @author Abhinav.Sinha
 * created on Aug 30 2019
 */
public class GetSapDestination {

  private static JCoDestination destination = null;
  private static PluginConfig config = new PluginConfig();
  private static final String DEST = "RFC";

  /**
   * Creates a connection pool to an SAP system
   * @param executionTarget The physical destination of a function call. It contains all required properties in order to connect to an SAP system
   * @param systemNumber    Client system number
   * @param client          Client id
   * @param user            An authorized user to issue the connection
   * @param password        An authorized user's password
   * @param clientLanguage  Client language f.e. en, fr
   * @param poolCapacity    Pool capacity of the connection f.e. 3
   * @param peakLimit       The peak limit of the connection pool f.e. 10
   * @throws java.lang.Exception if an error occurs
   */
  public static JCoDestination getDestination(String executionTarget, String systemNumber, String client, String user,
                                              String password, String clientLanguage, String poolCapacity,
                                              String peakLimit) throws Exception {
    // System.setProperty("java.library.path", config.getJcoPath());
    try {
      Properties connectProperties = new Properties() {{
        setProperty(DestinationDataProvider.JCO_ASHOST, executionTarget);
        setProperty(DestinationDataProvider.JCO_SYSNR, systemNumber);
        setProperty(DestinationDataProvider.JCO_CLIENT, client);
        setProperty(DestinationDataProvider.JCO_USER, user);
        setProperty(DestinationDataProvider.JCO_PASSWD, password);
        setProperty(DestinationDataProvider.JCO_LANG, clientLanguage);
        setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, poolCapacity);
        setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, peakLimit);
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

  /**
   * Creates the data file to establish a RFC connection
   * @param name            The data file name
   * @param suffix          Suffix for the data file name
   * @param properties      Connection properties
   * @param executionTarget The physical destination of a function call. It contains all required properties in order to connect to an SAP system
   * @throws java.lang.Exception if an error occurs
   */
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
