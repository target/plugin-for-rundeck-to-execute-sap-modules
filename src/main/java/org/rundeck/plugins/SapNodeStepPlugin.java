package org.rundeck.plugins;

import com.dtolabs.rundeck.core.execution.workflow.steps.FailureReason;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.Describable;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.dtolabs.rundeck.plugins.step.StepPlugin;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;
import com.dtolabs.rundeck.plugins.util.PropertyBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.rundeck.plugins.sap_connector.SapManager;

@Plugin(name = SapNodeStepPlugin.SERVICE_PROVIDER_NAME, service = ServiceNameConstants.WorkflowStep)
@PluginDescription(title = SapNodeStepPlugin.PLUGIN_NAME, description = SapNodeStepPlugin.PLUGIN_DESCRIPTION)
public class SapNodeStepPlugin implements StepPlugin, Describable {
  public static final String SERVICE_PROVIDER_NAME = "SapNodeStepPlugin";
  public static final String PLUGIN_NAME = "SAP Node Step";
  public static final String PLUGIN_DESCRIPTION = "Connects to SAP systems and executes jobs";

  public Description getDescription() {
    return DescriptionBuilder.builder()
      .name(SERVICE_PROVIDER_NAME)
      .title(PLUGIN_NAME)
      .description(PLUGIN_DESCRIPTION)
      .property(PropertyBuilder.builder()
        .name("sapExecutionTarget")
        .string("sapExecutionTarget")
        .title("Execution Target")
        .description("The application server host to connect to")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapSystemNumber")
        .string("sapSystemNumber")
        .title("System Number")
        .description("The system number of the SAP instance")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapClient")
        .string("sapClient")
        .title("Client")
        .description("Specifies the SAP client")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapUser")
        .string("sapUser")
        .title("User")
        .description("User authenticating to the SAP destination")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapClientLanguage")
        .string("sapClientLanguage")
        .title("Client Language")
        .description("Client Language f.e. en, fr")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapPoolCapacity")
        .string("sapPoolCapacity")
        .title("Pool Capacity")
        .description("Pool Capacity of the connection f.e. 3")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapPeakLimit")
        .string("sapPeakLimit")
        .title("Peak Limit")
        .description("Peak limit of the connection f.e. 10")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Configure the destination server")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapSourceJobName")
        .string("sapSourceJobName")
        .title("Source Job Name")
        .description("Source job name")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Copy a pre-defined SAP Job and Run")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapSourceJobCount")
        .string("sapSourceJobCount")
        .title("Source Job Count")
        .description("Source job id")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Copy a pre-defined SAP Job and Run")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .property(PropertyBuilder.builder()
        .name("sapNewJobName")
        .string("sapNewJobName")
        .title("New Job Name")
        .description("Name of new copied job")
        .renderingOption(StringRenderingConstants.GROUPING, "SECONDARY")
        .renderingOption(StringRenderingConstants.GROUP_NAME, "Copy a pre-defined SAP Job and Run")
        .renderingOption("displayType", StringRenderingConstants.DisplayType.SINGLE_LINE)
        .required(true)
        .build()
      )
      .build();
  }

  public void executeStep(PluginStepContext context, final Map<String, Object> configuration) throws StepException {
    SapManager sapManager = new SapManager();
    if (context.getDataContextObject().getData().get("option").isEmpty()
    || context.getDataContextObject().getData().get("option") == null) {
      throw new StepException("Missing connection password", Reason.BadRequest);
    }
    try {
      configuration.put("sapJcoPassword", context.getDataContextObject().getData().get("option").entrySet().iterator().next().getValue());
      sapManager.SapConnector(configuration);
    } catch (Exception e) {
      throw new StepException(e.getMessage(), Reason.InternalServerError);
    }
  }

  static enum Reason implements FailureReason {
    BadRequest, InternalServerError
  }
}
