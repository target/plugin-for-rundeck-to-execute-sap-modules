package org.rundeck.plugins.sap_connector;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterField;
import com.sap.conn.jco.JCoParameterFieldIterator;
import org.rundeck.plugins.configurations.PluginConfig;

/*
 * @author Abhinav.Sinha
 * created on Aug 30 2019
 */
public class ReleaseSapJob {

  private static JCoFunction function = null;
  private static PluginConfig config = new PluginConfig();

  /**
   * Issues job start BAPI function module to start a job immediately
   * @param destination  The physical destination of a function call. It contains all required properties in order to connect to an SAP system.
   * @param jobName      ABAP programme job name to be released
   * @param jobId        ABAP programme job id (job count) to be released
   * @param user         The user authorized to release the job
   * @throws java.lang.Exception if an error occurs
   */
  public static void releaseJob(JCoDestination destination, String jobName, String jobId, String user) throws Exception {
    try {
      JCoContext.begin(destination);
      function = destination.getRepository().getFunctionTemplate("BAPI_XMI_LOGON").getFunction();
      function.getImportParameterList().setValue("EXTCOMPANY", config.getCompany());
      function.getImportParameterList().setValue("EXTPRODUCT", config.getProductName());
      function.getImportParameterList().setValue("INTERFACE", config.getInterfaceName());
      function.getImportParameterList().setValue("VERSION", config.getVersion());
      function.execute(destination);
      function = destination.getRepository().getFunctionTemplate("BAPI_XBP_JOB_START_IMMEDIATELY").getFunction();
      function.getImportParameterList().setValue("EXTERNAL_USER_NAME", user);
      function.getImportParameterList().setValue("JOBNAME", jobName);
      function.getImportParameterList().setValue("JOBCOUNT", jobId);
      function.execute(destination);
      JCoContext.end(destination);
    } catch (JCoException ex) {
      System.out.println("JCoException: " + ex.getMessage());
      throw ex;
    } catch (Exception e) {
      throw e;
    }
  }
}
