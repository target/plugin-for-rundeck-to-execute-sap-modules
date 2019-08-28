package org.rundeck.plugins.sap_connector;

import com.sap.conn.jco.JCoDestination;
import java.util.HashMap;
import java.util.Map;

public class SapManager {

  public void SapConnector(Map<String, Object> configuration) throws Exception {
    Map<String, String> newJob = new HashMap<String, String>();
    GetSapDestination dest = new GetSapDestination();
    CopySapJob copy = new CopySapJob();
    ReleaseSapJob release = new ReleaseSapJob();
    GetSapProcessStatus status = new GetSapProcessStatus();
    try {
      JCoDestination destination = dest.getDestination(
        configuration.get("sapExecutionTarget").toString(),
        configuration.get("sapSystemNumber").toString(),
        configuration.get("sapClient").toString(),
        configuration.get("sapUser").toString(),
        configuration.get("sapJcoPassword").toString(),
        configuration.get("sapClientLanguage").toString(),
        configuration.get("sapPoolCapacity").toString(),
        configuration.get("sapPeakLimit").toString()
      );
      newJob = copy.copyJob(
        destination,
        configuration.get("sapSourceJobName").toString(),
        configuration.get("sapSourceJobCount").toString(),
        configuration.get("sapNewJobName").toString(),
        configuration.get("sapUser").toString()
      );
      release.releaseJob(
        destination,
        newJob.get("jobName"),
        newJob.get("jobCount"),
        configuration.get("sapUser").toString()
      );
      status.getSapJobStatus(
        destination,
        newJob.get("jobName"),
        newJob.get("jobCount"),
        configuration.get("sapUser").toString()
      );
    } catch (Exception e) {
      throw e;
    }
  }
}
