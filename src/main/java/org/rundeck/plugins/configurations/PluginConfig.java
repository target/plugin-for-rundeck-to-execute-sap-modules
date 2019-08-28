package org.rundeck.plugins.configurations;

public class PluginConfig {
  private String company = "COMPANY";
  private String interfaceName = "XBP";
  private String productName = "Rundeck";
  private String version = "3.0";

  public PluginConfig() {
  }

  public String getCompany() {
    return company;
  }

  public String getInterfaceName() {
    return interfaceName;
  }

  public String getProductName() {
    return productName;
  }

  public String getVersion() {
    return version;
  }
}
