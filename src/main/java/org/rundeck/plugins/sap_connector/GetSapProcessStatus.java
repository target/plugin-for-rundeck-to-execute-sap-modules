package org.rundeck.plugins.sap_connector;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterField;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.ext.DestinationDataProvider;
import org.rundeck.plugins.configurations.PluginConfig;

/*
 * @author z00294j
 * created on Feb 10 2019
 */
public class GetSapProcessStatus {

  private static JCoFunction function = null;
  private final static int sleepTime = 5000;
  private static PluginConfig config = new PluginConfig();

  public static void getSapJobStatus(JCoDestination destination, String jobName, String jobId, String user) throws Exception {
    try {
      JCoContext.begin(destination);
      function = destination.getRepository().getFunctionTemplate("BAPI_XMI_LOGON").getFunction();
      function.getImportParameterList().setValue("EXTCOMPANY", config.getCompany());
      function.getImportParameterList().setValue("EXTPRODUCT", config.getProductName());
      function.getImportParameterList().setValue("INTERFACE", config.getInterfaceName());
      function.getImportParameterList().setValue("VERSION", config.getVersion());
      function.execute(destination);
      function = destination.getRepository().getFunctionTemplate("BAPI_XBP_JOB_STATUS_GET").getFunction();
      function.getImportParameterList().setValue("EXTERNAL_USER_NAME", user);
      function.getImportParameterList().setValue("JOBNAME", jobName);
      function.getImportParameterList().setValue("JOBCOUNT", jobId);
      System.out.println("Copied Job Name: ".concat(jobName));
      System.out.println("Copied Job Id: ".concat(jobId));
      System.out.println("Execution Server: ".concat(destination.getProperties().getProperty(DestinationDataProvider.JCO_ASHOST)));
      Boolean active = true;
      while(active) {
        function.execute(destination);
        JCoParameterFieldIterator iteratorStatus = function.getExportParameterList().getParameterFieldIterator();
        while(iteratorStatus.hasNextField()) {
          JCoParameterField fieldStatus = iteratorStatus.nextParameterField();
          if (fieldStatus.getName().toString().equals("STATUS")) {
            if (fieldStatus.getValue().toString().equals("F")) {
              // 'F' - finished
              System.out.println("\nSummary: \n".concat("Job finished with a status \"".concat(fieldStatus.getValue().toString()).concat("\"")));
              active = false;
            } else if (fieldStatus.getValue().toString().equals("S")) { // 'S' - released
              Thread.sleep(sleepTime);
            } else if (fieldStatus.getValue().toString().equals("R")) { // 'R' - active
              Thread.sleep(sleepTime);
            } else {
              throw new Exception("Job failed to run with a status \"".concat(fieldStatus.getValue().toString())
                .concat("\""));
            }
          }
        }
      }
      function = destination.getRepository().getFunctionTemplate("BAPI_XBP_JOB_JOBLOG_READ").getFunction();
      function.getImportParameterList().setValue("EXTERNAL_USER_NAME", user);
      function.getImportParameterList().setValue("JOBNAME", jobName);
      function.getImportParameterList().setValue("JOBCOUNT", jobId);
      function.execute(destination);
      JCoParameterFieldIterator iteratorLog = function.getTableParameterList().getParameterFieldIterator();
      while(iteratorLog.hasNextField()) {
        JCoParameterField fieldLog = iteratorLog.nextParameterField();
        if (fieldLog.getName().toString().equals("JOB_PROTOCOL")) {
          System.out.println("\nDetails: \n".concat(fieldLog.getTable().toString()));
        }
      }
      JCoContext.end(destination);
    } catch (JCoException ex) {
      System.out.println("JCoException: " + ex.getMessage());
      throw ex;
    } catch (Exception e) {
      if (e.getMessage().contains("is not a member of")) {
        System.out.println("Unable to get the job log entries at this moment, validate via SAP GUI");
      } else {
        throw e;
      }
    }
  }
}
