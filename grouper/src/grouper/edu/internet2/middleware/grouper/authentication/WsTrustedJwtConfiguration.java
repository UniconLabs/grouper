package edu.internet2.middleware.grouper.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.internet2.middleware.grouper.app.config.GrouperConfigurationModuleAttribute;
import edu.internet2.middleware.grouper.app.config.GrouperConfigurationModuleBase;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigFileMetadata;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigFileName;
import edu.internet2.middleware.grouper.cfg.dbConfig.DbConfigEngine;
import edu.internet2.middleware.grouperClient.config.ConfigPropertiesCascadeBase;

public class WsTrustedJwtConfiguration extends GrouperConfigurationModuleBase {

  @Override
  public ConfigFileName getConfigFileName() {
    return ConfigFileName.GROUPER_PROPERTIES;
  }

  @Override
  public String getConfigItemPrefix() {
    if (StringUtils.isBlank(this.getConfigId())) {
      throw new RuntimeException("Must have configId!");
    }
    return "grouper.jwt.trusted." + this.getConfigId() + ".";
  }

  @Override
  public String getConfigIdRegex() {
    return "^(grouper\\.jwt\\.trusted)\\.([^.]+)\\.(.*)$";
  }

  @Override
  protected String getConfigurationTypePrefix() {
    return "grouper.jwt.trusted";
  }
  
  @Override
  public String getConfigIdThatIdentifiesThisConfig() {
    return "testConfigId";
  }
  
  /**
   * list of configured ws trusted jwt configs
   * @return
   */
  public static List<WsTrustedJwtConfiguration> retrieveAllWsTrustedJwtConfigs() {
   Set<String> classNames = new HashSet<String>();
   classNames.add(WsTrustedJwtConfiguration.class.getName());
   return (List<WsTrustedJwtConfiguration>) (Object) retrieveAllConfigurations(classNames);
  }
  
  /**
   * change status of config to disable/enable
   * @param enable
   * @param message
   * @param errorsToDisplay
   * @param validationErrorsToDisplay
   */
  public void changeStatus(boolean enable, StringBuilder message, List<String> errorsToDisplay, Map<String, String> validationErrorsToDisplay) {
    
    GrouperConfigurationModuleAttribute enabledAttribute = this.retrieveAttributes().get("enabled");
    enabledAttribute.setValue(enable? "true": "false");
    
    ConfigFileName configFileName = this.getConfigFileName();
    ConfigFileMetadata configFileMetadata = configFileName.configFileMetadata();
    List<String> actionsPerformed = new ArrayList<String>();

    DbConfigEngine.configurationFileAddEditHelper2(configFileName, this.getConfigFileName().getConfigFileName(), configFileMetadata,
        enabledAttribute.getFullPropertyName(), 
        enabledAttribute.isExpressionLanguage() ? "true" : "false", 
        enabledAttribute.isExpressionLanguage() ? enabledAttribute.getExpressionLanguageScript() : enabledAttribute.getValue(),
        enabledAttribute.isPassword(), message, new Boolean[] {false},
        new Boolean[] {false}, true, "Ws trusted JWT config status changed", errorsToDisplay, validationErrorsToDisplay, false, actionsPerformed);    
    ConfigPropertiesCascadeBase.clearCache();
  }

}
