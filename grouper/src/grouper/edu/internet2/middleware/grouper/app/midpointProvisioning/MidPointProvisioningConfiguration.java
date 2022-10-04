package edu.internet2.middleware.grouper.app.midpointProvisioning;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningConfigurationAttribute;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningConfigurationAttributeTranslationType;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningConfigurationAttributeType;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningConfigurationAttributeValueType;
import edu.internet2.middleware.grouper.app.sqlProvisioning.SqlGrouperProvisioningConfigurationAttribute;
import edu.internet2.middleware.grouper.app.sqlProvisioning.SqlProvisioningConfiguration;
import edu.internet2.middleware.grouper.util.GrouperUtil;


public class MidPointProvisioningConfiguration extends SqlProvisioningConfiguration {

  private String dbExternalSystemConfigId;
  private String midPointTablesPrefix;
  
  private boolean midPointHasTargetAttribute = true;
  
  // comma separated list of targets
  private String midPointListOfTargets;
  
  public String getDbExternalSystemConfigId() {
    return dbExternalSystemConfigId;
  }
  
  public void setDbExternalSystemConfigId(String dbExternalSystemConfigId) {
    this.dbExternalSystemConfigId = dbExternalSystemConfigId;
  }

  public String getMidPointTablesPrefix() {
    return midPointTablesPrefix;
  }

  public void setMidPointTablesPrefix(String midPointTablesPrefix) {
    this.midPointTablesPrefix = midPointTablesPrefix;
  }
  
  
  public boolean isMidPointHasTargetAttribute() {
    return midPointHasTargetAttribute;
  }

  
  public void setMidPointHasTargetAttribute(boolean midPointHasTargetAttribute) {
    this.midPointHasTargetAttribute = midPointHasTargetAttribute;
  }

  
  public String getMidPointListOfTargets() {
    return midPointListOfTargets;
  }

  
  public void setMidPointListOfTargets(String midPointListOfTargets) {
    this.midPointListOfTargets = midPointListOfTargets;
  }
  

  @Override
  public void configureSpecificSettings() {
    
    this.dbExternalSystemConfigId = this.retrieveConfigString("dbExternalSystemConfigId", true);    
    
    //TODO validate by connecting to midpoint tables with this prefix
    this.midPointTablesPrefix = this.retrieveConfigString("midPointTablesPrefix", false);
    
    this.midPointHasTargetAttribute = GrouperUtil.booleanValue(this.retrieveConfigBoolean("midPointHasTargetAttribute", false), true);
    
    if (this.midPointHasTargetAttribute) {
      this.midPointListOfTargets = this.retrieveConfigString("midPointListOfTargets", false);
    }
    
    setMembershipTableName(this.midPointTablesPrefix + "_mp_memberships");
    
    setSqlLastModifiedColumnType(this.retrieveConfigString("midPointLastModifiedColumnType", false));
    setSqlLastModifiedColumnName(this.retrieveConfigString("midPointLastModifiedColumnName", false));
    setSqlDeletedColumnName(this.retrieveConfigString("midPointDeletedColumnName", false));
    
    setMembershipGroupForeignKeyColumn("group_id_index");
    setMembershipEntityForeignKeyColumn("subject_id_index");
    
    setEntityTableName(this.midPointTablesPrefix + "_mp_subjects");
    
    setEntityTableIdColumn("subject_id_index");

    setGroupTableName(this.midPointTablesPrefix + "_mp_groups");
    
    setGroupAttributesTableName(this.midPointTablesPrefix + "_mp_group_attributes");
    
    setGroupAttributesGroupForeignKeyColumn("group_id_index");
    
    setGroupAttributesAttributeNameColumn("attribute_name");
    
    setGroupAttributesAttributeValueColumn("attribute_value");
    
    setGroupTableIdColumn("id_index");
    
    setEntityAttributesTableName(this.midPointTablesPrefix + "_mp_subject_attributes");
    
    setEntityAttributesEntityForeignKeyColumn("subject_id_index");
    
    setEntityAttributesAttributeNameColumn("attribute_name");
    
    setEntityAttributesAttributeValueColumn("attribute_value");
    
    setUseSeparateTableForEntityAttributes(true);
    setUseSeparateTableForGroupAttributes(true);
    
//    setMembershipMatchingIdExpression("${new('edu.internet2.middleware.grouperClient.collections.MultiKey', targetMembership.retrieveAttributeValueString('"+getMembershipGroupMatchingIdAttribute()+"'), targetMembership.retrieveAttributeValueString('"+getMembershipEntityMatchingIdAttribute()+"'))}");
    setMembershipMatchingIdExpression("${new('edu.internet2.middleware.grouperClient.collections.MultiKey', targetMembership.getAttributes().get('group_id_index').getValue(), targetMembership.getAttributes().get('subject_id_index').getValue())}");
    
    
    // group_name - group attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(0);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
      attributeConfig.setName("group_name");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningGroupField);
      attributeConfig.setTranslateFromGrouperProvisioningGroupField("name");
      attributeConfig.setStorageType("groupTableColumn");
      
      getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
    }
    
    // id_index - group attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(1);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
      attributeConfig.setName("id_index");
      attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningGroupField);
      attributeConfig.setTranslateFromGrouperProvisioningGroupField("idIndex");
      attributeConfig.setStorageType("groupTableColumn");
      
      getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
      List<GrouperProvisioningConfigurationAttribute> groupMatchingAttributes = new ArrayList<>();
      List<GrouperProvisioningConfigurationAttribute> groupSearchAttributes = new ArrayList<>();
      
      groupMatchingAttributes.add(attributeConfig);
      groupSearchAttributes.add(attributeConfig);
      this.setGroupSearchAttributes(groupSearchAttributes);
      this.setGroupMatchingAttributes(groupMatchingAttributes);
      this.setGroupMatchingAttributeSameAsSearchAttribute(true);
      
    }
    
    // display_name - group attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(2);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
      attributeConfig.setName("display_name");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningGroupField);
      attributeConfig.setTranslateFromGrouperProvisioningGroupField("displayName");
      attributeConfig.setStorageType("groupTableColumn");
      
      getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
    }
    
    // description - group attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(3);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
      attributeConfig.setName("description");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningGroupField);
      attributeConfig.setTranslateFromGrouperProvisioningGroupField("description");
      attributeConfig.setStorageType("groupTableColumn");
      
      getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
    }
    
    // last_modified - group attribute
    {
      
      if (StringUtils.isNotBlank(this.getSqlLastModifiedColumnName())) {
        SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
        
        attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
        attributeConfig.setConfigIndex(4);
        attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
        attributeConfig.setName(this.getSqlLastModifiedColumnName());
        attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
        getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      }
      
    }
    
    // deleted - group attribute 
    {
      
      if (StringUtils.isNotBlank(this.getSqlDeletedColumnName())) {
        SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
        
        attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
        attributeConfig.setConfigIndex(5);
        attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
        attributeConfig.setName(this.getSqlDeletedColumnName());
        getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      }
      
    }
    
    
    // provisioning target - group attribute - separate table
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(4);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.group);
      attributeConfig.setName("target");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.translationScript);
      attributeConfig.setTranslateExpression("${grouperProvisioningGroup.retrieveAttributeValue('md_grouper_midPointTarget')}");
      attributeConfig.setStorageType("separateAttributesTable");
      attributeConfig.setMultiValued(true);
      
      getTargetGroupAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
    }
    
    
    // group_id_index - membership attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(0);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.membership);
      attributeConfig.setName("group_id_index");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningGroupField);
      attributeConfig.setTranslateFromGrouperProvisioningGroupField("idIndex");
      attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
      
      getTargetMembershipAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
    }
    
    // subject_id_index - membership attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(1);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.membership);
      attributeConfig.setName("subject_id_index");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningEntityField);
      attributeConfig.setTranslateFromGrouperProvisioningEntityField("idIndex");
      attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
      
      getTargetMembershipAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
    }
    
    // last_modified - membership attribute
    {
      
      if (StringUtils.isNotBlank(this.getSqlLastModifiedColumnName())) {
        SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
        
        attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
        attributeConfig.setConfigIndex(2);
        attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.membership);
        attributeConfig.setName(this.getSqlLastModifiedColumnName());
        attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
        getTargetMembershipAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      }
      
    }
    
    // deleted - membership attribute 
    {
      
      if (StringUtils.isNotBlank(this.getSqlDeletedColumnName())) {
        SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
        
        attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
        attributeConfig.setConfigIndex(3);
        attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.membership);
        attributeConfig.setName(this.getSqlDeletedColumnName());
        getTargetMembershipAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      }
      
    }
    
    
    
    // subject_id_index - subject attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(0);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.entity);
      attributeConfig.setName("subject_id_index");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningEntityField);
      attributeConfig.setTranslateFromGrouperProvisioningEntityField("idIndex");
      attributeConfig.setStorageType("entityTableColumn");
      attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
      
      getTargetEntityAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
      List<GrouperProvisioningConfigurationAttribute> entityMatchingAttributes = new ArrayList<>();
      List<GrouperProvisioningConfigurationAttribute> entitySearchAttributes = new ArrayList<>();
      
      entityMatchingAttributes.add(attributeConfig);
      entitySearchAttributes.add(attributeConfig);
      this.setEntitySearchAttributes(entitySearchAttributes);
      this.setEntityMatchingAttributes(entityMatchingAttributes);
      this.setEntityMatchingAttributeSameAsSearchAttribute(true);
      
    }
    
    // subject_id - subject attribute
    {
      SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
      
      attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
      attributeConfig.setConfigIndex(1);
      attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.entity);
      attributeConfig.setName("subject_id");
      attributeConfig.setTranslateExpressionType(GrouperProvisioningConfigurationAttributeTranslationType.grouperProvisioningEntityField);
      attributeConfig.setTranslateFromGrouperProvisioningEntityField("subjectId");
      attributeConfig.setStorageType("entityTableColumn");
      
      getTargetEntityAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      
    }
    
    // last_modified - subject attribute
    {
      
      if (StringUtils.isNotBlank(this.getSqlLastModifiedColumnName())) {
        SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
        
        attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
        attributeConfig.setConfigIndex(2);
        attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.entity);
        attributeConfig.setName(this.getSqlLastModifiedColumnName());
        attributeConfig.setValueType(GrouperProvisioningConfigurationAttributeValueType.LONG);
        getTargetEntityAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      }
      
    }
    
    // deleted - subject attribute 
    {
      
      if (StringUtils.isNotBlank(this.getSqlDeletedColumnName())) {
        SqlGrouperProvisioningConfigurationAttribute attributeConfig = (SqlGrouperProvisioningConfigurationAttribute) GrouperUtil.newInstance(this.grouperProvisioningConfigurationAttributeClass());
        
        attributeConfig.setGrouperProvisioner(this.getGrouperProvisioner());
        attributeConfig.setConfigIndex(3);
        attributeConfig.setGrouperProvisioningConfigurationAttributeType(GrouperProvisioningConfigurationAttributeType.entity);
        attributeConfig.setName(this.getSqlDeletedColumnName());
        getTargetEntityAttributeNameToConfig().put(attributeConfig.getName(), attributeConfig);
      }
      
    }
    
  }

}
