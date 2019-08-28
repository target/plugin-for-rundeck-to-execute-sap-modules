package org.rundeck.plugins.sap_connector;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterField;
import com.sap.conn.jco.JCoParameterFieldIterator;
import java.util.HashMap;
import java.util.Map;
import org.rundeck.plugins.configurations.PluginConfig;

/*
 * @author Abhinav.Sinha
 * created on Aug 30 2019
 */
public class CopySapJob {

  private static JCoFunction function = null;
  private static PluginConfig config = new PluginConfig();

  /**
   * Issues job copy BAPI function module to copy a pre-defined SAP job
   * @param destination       The physical destination of a function call. It contains all required properties in order to connect to an SAP system.
   * @param sapSourceJobName  ABAP programme source job name to be copied
   * @param sapSourceJobCount ABAP programme source job id (job count) to be copied
   * @param sapNewJobName     The target job name
   * @param user              The user authorized to release the job
   * @throws java.lang.Exception if an error occurs
   */
  public static Map<String, String> copyJob(JCoDestination destination, String sapSourceJobName, String sapSourceJobCount,
                                            String sapNewJobName, String user) throws Exception {
    Map<String, String> newJob = new HashMap<String, String>();
    try {
      JCoContext.begin(destination);
      function = destination.getRepository().getFunctionTemplate("BAPI_XMI_LOGON").getFunction();
      function.getImportParameterList().setValue("EXTCOMPANY", config.getCompany());
      function.getImportParameterList().setValue("EXTPRODUCT", config.getProductName());
      function.getImportParameterList().setValue("INTERFACE", config.getInterfaceName());
      function.getImportParameterList().setValue("VERSION", config.getVersion());
      function.execute(destination);
      function = destination.getRepository().getFunctionTemplate("BAPI_XBP_JOB_COPY").getFunction();
      function.getImportParameterList().setValue("SOURCE_JOBNAME", sapSourceJobName);
      function.getImportParameterList().setValue("SOURCE_JOBCOUNT", sapSourceJobCount);
      function.getImportParameterList().setValue("TARGET_JOBNAME", sapNewJobName);
      function.getImportParameterList().setValue("EXTERNAL_USER_NAME", user);
      function.execute(destination);
      JCoParameterFieldIterator iterator = function.getExportParameterList().getParameterFieldIterator();
      String copiedJobId = null;
      while(iterator.hasNextField()) {
        JCoParameterField field = iterator.nextParameterField();
        if (field.getName().equals("TARGET_JOBCOUNT")) {
          copiedJobId = field.getValue().toString();
        }
      }
      if (copiedJobId.equals("")) {
        throw new Exception("Unable to copy job: ".concat(sapSourceJobName)
          .concat(" at this time. Please make sure source job exists and job id matches"));
      } else {
        newJob.put("jobName", sapNewJobName);
        newJob.put("jobCount", copiedJobId);
        JCoContext.end(destination);
        return newJob;
      }
    } catch (JCoException ex) {
      System.out.println("JCoException: " + ex.getMessage());
      throw ex;
    } catch (Exception e) {
      throw e;
    }
  }
}
