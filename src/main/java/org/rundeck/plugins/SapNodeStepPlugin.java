package org.rundeck.plugins;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.descriptions.RenderingOption;
import com.dtolabs.rundeck.plugins.descriptions.RenderingOptions;
import com.dtolabs.rundeck.plugins.descriptions.SelectValues;
import com.dtolabs.rundeck.plugins.step.GeneratedScript;
import com.dtolabs.rundeck.plugins.step.GeneratedScriptBuilder;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.dtolabs.rundeck.plugins.step.RemoteScriptNodeStepPlugin;
import java.util.Map;
import org.slf4j.LoggerFactory;

@Plugin(service = ServiceNameConstants.RemoteScriptNodeStep, name = SapNodeStepPlugin.SERVICE_PROVIDER_NAME)
@PluginDescription(title = "SAP Node Step", description = "Connects to SAP systems and executes jobs")
public class SapNodeStepPlugin implements RemoteScriptNodeStepPlugin {

    public static final String SERVICE_PROVIDER_NAME = "SapNodeStepPlugin";
    private static final String connectorAddress = "https://binrepo.target.com/artifactory/ti-dsco/rundeck/artifacts/sap-connector";
    private static final String connectorName = "sap-connector";
    private static final String connectorVersion = "v1.2";
    private String SAP_JCO_PASSWORD = "";

    @PluginProperty(title = "Execution Target", description = "The application server host to conect to", required = true)
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Configure the destination server")
    })
    String sapExecutionTarget;

    @PluginProperty(title = "System Number", description = "The system number of the SAP instance", required = true)
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Configure the destination server")
    })
    String sapSystemNumber;

    @PluginProperty(title = "Client", description = "Specifies the SAP client", defaultValue = "100", required = true)
    @SelectValues(values = {"100", "110"})
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Configure the destination server")
    })
    String sapClient;

    @PluginProperty(title = "User", description = "User authenticating to the SAP destination", required = true)
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Configure the destination server")
    })
    String sapUser;

    @PluginProperty(title = "Source Job Name", description = "Source job name", required = true)
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Copy a pre-defined SAP Job and Run")
    })
    String sapSourceJobName;

    @PluginProperty(title = "Source Job Count", description = "Source job id", required = true)
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Copy a pre-defined SAP Job and Run")
    })
    String sapSourceJobCount;

    @PluginProperty(title = "New Job Name", description = "Name of new copied job", required = true)
    @RenderingOptions({
            @RenderingOption(key = StringRenderingConstants.GROUPING, value = "SECONDARY"),
            @RenderingOption(key = StringRenderingConstants.GROUP_NAME, value = "Copy a pre-defined SAP Job and Run")
    })
    String sapNewJobName;

    /**
     * Here the plugin executes a set of commands on the given remote node based on a validation
     * <p/>
     * The {@link GeneratedScriptBuilder} provides a factory for returning the correct type.
     */
    public GeneratedScript generateScript(final PluginStepContext context, final Map<String, Object> configuration, final INodeEntry entry) {
        if (validateForm(context)) {
            final String connector = connectorName.concat("-").concat(connectorVersion).concat(".tar.gz");
            return GeneratedScriptBuilder.command(
                    "if [ -f ".concat(connector).concat(" ]; then ") +
                            "echo \"Latest connector is already available\"; " +
                            "else rm -f ".concat(connector).concat("-*.tar.gz; ") +
                            "echo \"Latest connector is not found - Downloading...\"; " +
                            "curl -sk ".concat(connectorAddress).concat("/").concat(connector).concat(" -o ").concat(connector).concat("; ") +
                            "fi",
                    "&&", "tar -zxf ".concat(connector),
                    "&&", "echo Preparing environment",
                    "&&", "export ENV_SAP_HOST=".concat(sapExecutionTarget)
                            .concat(" && export ENV_SAP_USER=").concat(sapUser)
                            .concat(" && export ENV_SAP_SYSNR=").concat(sapSystemNumber)
                            .concat(" && export ENV_SAP_CLIENT=").concat(sapClient)
                            .concat(" && export ENV_SAP_JCO_PATH=").concat("`pwd`/binary/sapjco3")
                            .concat(" && export ENV_SAP_PASSWORD=").concat(SAP_JCO_PASSWORD),
                    "&&", "./binary/".concat(connectorName).concat("/bin/").concat(connectorName).concat(" R3 ").concat(sapSourceJobName).concat(" ").concat(sapSourceJobCount).concat(" ").concat(sapNewJobName)
            );
        }
        else
            return GeneratedScriptBuilder.command("exit 1");
    }

    /**
     * Here the plugin validates the inputs from the user
     */
    public Boolean validateForm(final PluginStepContext context) {
        if (context.getExecutionContext().getDataContext().get("secureOption") == null) {
            System.out.println("No required options defined in rundeck");
            LoggerFactory.getLogger(SERVICE_PROVIDER_NAME).error("No required options defined in rundeck - "
                    .concat(context.getExecutionContext().getDataContext().get("job").get("project")).concat(" - ")
                    .concat(context.getExecutionContext().getDataContext().get("job").get("name")));
            return false;
        }
        else {
            if (context.getExecutionContext().getDataContext().get("secureOption").entrySet().iterator().next().getValue() == null) {
                System.out.println("No password found in rundeck options");
                LoggerFactory.getLogger(SERVICE_PROVIDER_NAME).error("No password found in rundeck options - "
                        .concat(context.getExecutionContext().getDataContext().get("job").get("project")).concat(" - ")
                        .concat(context.getExecutionContext().getDataContext().get("job").get("name")));
                return false;
            }
        }
        SAP_JCO_PASSWORD = context.getExecutionContext().getDataContext().get("secureOption").entrySet().iterator().next().getValue();
        return true;
    }



}
