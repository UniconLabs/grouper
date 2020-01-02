/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperClient.jdbc.tableSync;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.internet2.middleware.grouperClient.util.GrouperClientConfig;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.lang3.StringUtils;


/**
 * an instance of this class focuses on the configuration for table sync
 * create an instance, set the key, and call configure
 */
public class GcTableSyncConfiguration {

  /**
   * database from key
   */
  private String databaseFrom;
  
  /**
   * database from key
   * @return the databaseFrom
   */
  public String getDatabaseFrom() {
    return this.databaseFrom;
  }

  /**
   * database to key
   * @return the databaseTo
   */
  public String getDatabaseTo() {
    return this.databaseTo;
  }

  /**
   * database to key (readonly) if large queries should be performed against a different database
   */
  private String databaseToReadonly;

  /**
   * database to key (readonly) if large queries should be performed against a different database
   * @return the databaseToReadonly
   */
  public String getDatabaseToReadonly() {
    return this.databaseToReadonly;
  }
  
  /**
   * database to key (readonly) if large queries should be performed against a different database
   * @param databaseToReadonly1 the databaseToReadonly to set
   */
  public void setDatabaseToReadonly(String databaseToReadonly1) {
    this.databaseToReadonly = databaseToReadonly1;
  }

  /**
   * database to key
   */
  private String databaseTo;
  /**
   * subtype which also implies which type (full | incremental)
   */
  private GcTableSyncSubtype gcTableSyncSubtype = null;
  
  
  /**
   * subtype which also implies which type (full | incremental)
   * @return the gcTableSyncSubtype
   */
  public GcTableSyncSubtype getGcTableSyncSubtype() {
    return this.gcTableSyncSubtype;
  }

  
  /**
   * subtype which also implies which type (full | incremental)
   * @param gcTableSyncSubtype1 the gcTableSyncSubtype to set
   */
  public void setGcTableSyncSubtype(GcTableSyncSubtype gcTableSyncSubtype1) {
    this.gcTableSyncSubtype = gcTableSyncSubtype1;
  }

  

  /**
   * group column
   */
  private String groupColumnString;
 
  /**
   * batch size when batching data
   */
  private int batchSize;
  
  
  /**
   * batch size when batching data
   * @return the batchSize
   */
  public int getBatchSize() {
    return this.batchSize;
  }

  
  /**
   * batch size when batching data
   * @param batchSize1 the batchSize to set
   */
  public void setBatchSize(int batchSize1) {
    this.batchSize = batchSize1;
  }

  /**
   * how many to group by
   */
  private int groupingSize;

  /**
   * @return the groupSize
   */
  public int getGroupingSize() {
    return this.groupingSize;
  }

  /**
   * key in config that points to this instance of table sync
   */
  private String configKey;

  /**
   * if doing fullSyncChangeFlag (look for a col that says if the rows are equal, e.g. a timestamp or a checksum)
   */
  private String fullSyncChangeFlagColumnString;
  
  /**
   * if doing fullSyncChangeFlag (look for a col that says if the rows are equal, e.g. a timestamp or a checksum)
   * @return col
   */
  public String getFullSyncChangeFlagColumnString() {
    return this.fullSyncChangeFlagColumnString;
  }

  /**
   * if doing fullSyncChangeFlag (look for a col that says if the rows are equal, e.g. a timestamp or a checksum)
   * @param fullSyncChangeFlagColumnString1
   */
  public void setFullSyncChangeFlagColumnString(String fullSyncChangeFlagColumnString1) {
    this.fullSyncChangeFlagColumnString = fullSyncChangeFlagColumnString1;
  }

  /**
   * name of a column that has a sequence or last updated date
   */
  private String incrementalAllColumnsColumnString;

  /**
   * name of a column that has a sequence or last updated date
   * @return incremental col
   */
  public String getIncrementalAllColumnsColumnString() {
    return this.incrementalAllColumnsColumnString;
  }

  /**
   * name of a column that has a sequence or last updated date
   * @param incrementalAllColumnsColumnString1
   */
  public void setIncrementalAllColumnsColumnString(
      String incrementalAllColumnsColumnString1) {
    this.incrementalAllColumnsColumnString = incrementalAllColumnsColumnString1;
  }

  /**
   * realTimeLastUpdatedColumn column
   */
  private String realTimeLastUpdatedColumnString;

  /**
   * realTimeColumnsString comma separated list of column names to select from real time table
   */
  private String realTimeColumnsString;
  
  /**
   * realTimeColumnsString comma separated list of column names to select from real time table
   * @return the realTimeColumnsString
   */
  public String getRealTimeColumnsString() {
    return this.realTimeColumnsString;
  }
  
  /**
   * realTimeColumnsString comma separated list of column names to select from real time table
   * @param realTimeColumnsString1 the realTimeColumnsString to set
   */
  public void setRealTimeColumnsString(String realTimeColumnsString1) {
    this.realTimeColumnsString = realTimeColumnsString1;
  }

  /**
   * table where real time primary key and last_updated col is
   */
  private String incrementalPrimaryKeyTable;
  /**
   * grouperClient.syncTable.personSource.statusDatabase = awsDev
   */
  private String statusDatabase;
  /**
   * grouperClient.syncTable.personSource.statusTable = 
   */
  private String statusTable;
  /**
   * table name from
   */
  private String tableFrom;
  /**
   * table name to
   */
  private String tableTo;

  /**
   * 
   */
  public GcTableSyncConfiguration() {
  }

  /**
   * get a config name for this or dependency
   * @param configName
   * @param required 
   * @return the config
   */
  public Integer retrieveConfigInt(String configName, boolean required) {
    String configValueString = retrieveConfigString(configName, required);
    return GrouperClientUtils.intObjectValue(configValueString, true);
  }

  /**
   * get a config name for this or dependency
   * @param configName
   * @param required 
   * @return the config
   */
  public Boolean retrieveConfigBoolean(String configName, boolean required) {
    String configValueString = retrieveConfigString(configName, required);
    return GrouperClientUtils.booleanObjectValue(configValueString);
  }

  /**
   * get a config name for this or dependency
   * @param configName
   * @param required 
   * @return the config
   */
  public String retrieveConfigString(String configName, boolean required) {
    //  grouperClient.syncTable.personSource.configName = pcom
    
    change this to part of config
    
    String value = GrouperClientConfig.retrieveConfig().propertyValueString("grouperClient.syncTable." + this.configKey + "." + configName);
    if (!StringUtils.isBlank(value)) {
      return value;
    }
    value = GrouperClientConfig.retrieveConfig().propertyValueString("grouperClient.syncTableDefault." + configName);
    if (!StringUtils.isBlank(value)) {
      return value;
    }
    if (required) {
      throw new RuntimeException("Cant find config for syncTable: " + this.configKey + ": " + configName);
    }
    return null;

  }
  
  /** 
   * primary key columns, * means all columns, or list of comma separated values, default to *
   */
  private String primaryKeyColumnsString;
  
  /**
   * all columns, could be * which means all, or list of comma separated values, default to *
   */
  private String columnsString;
  
  
  
  
  /**
   * primary key columns, * means all columns, or list of comma separated values, default to *
   * @return the primaryKeyColumnsString
   */
  public String getPrimaryKeyColumnsString() {
    return this.primaryKeyColumnsString;
  }

  
  /**
   * primary key columns, * means all columns, or list of comma separated values, default to *
   * @param primaryKeyColumnsString1 the primaryKeyColumnsString to set
   */
  public void setPrimaryKeyColumnsString(String primaryKeyColumnsString1) {
    this.primaryKeyColumnsString = primaryKeyColumnsString1;
  }

  
  /**
   * all columns, could be * which means all, or list of comma separated values, default to *
   * @return the columnsString
   */
  public String getColumnsString() {
    return this.columnsString;
  }

  
  /**
   * all columns, could be * which means all, or list of comma separated values, default to *
   * @param columnsString1 the columnsString to set
   */
  public void setColumnsString(String columnsString1) {
    this.columnsString = columnsString1;
  }

  /**
   * @param debugMap
   */
  public void configureTableSync(Map<String, Object> debugMap) {

    // must have key
    if (StringUtils.isBlank(this.configKey)) {
      throw new RuntimeException("Why is config key blank?");
    }

    if (debugMap == null) {
      debugMap = new LinkedHashMap();
    }
    
    //  grouperClient.syncTable.personSource.databaseFrom = pcom
    this.databaseFrom = GrouperClientUtils.defaultIfBlank(this.retrieveConfigString("databaseFrom", false), "grouper");
    debugMap.put("configDatabaseFrom", this.databaseFrom);

    //  grouperClient.syncTable.personSource.databaseTo = awsDev
    this.databaseTo = GrouperClientUtils.defaultIfBlank(this.retrieveConfigString("databaseTo", false), "grouper");
    debugMap.put("configDatabaseTo", this.databaseTo);
    
    // grouper client or loader database key where copying data to, large queries go against different database
    this.databaseToReadonly = this.retrieveConfigString("databaseToReadonly", false);
    if (!StringUtils.isBlank(this.databaseToReadonly)) {
      debugMap.put("configDatabaseToReadonly", this.databaseToReadonly);
    }

    //  grouperClient.syncTable.personSource.tableFrom = PERSON_SOURCE_TEMP
    this.tableFrom = this.retrieveConfigString("tableFrom", true);
    debugMap.put("configTableFrom", this.tableFrom);

    //  grouperClient.syncTable.personSource.tableTo = PERSON_SOURCE_TEMP
    this.tableTo = GrouperClientUtils.defaultIfBlank(this.retrieveConfigString("tableTo", false), this.tableFrom);
    debugMap.put("configTableTo", this.tableTo);
    
    {
      // note this is not inherited from other configs, each config needs a subtype
      String tableSyncSubType = GrouperClientConfig.retrieveConfig().propertyValueStringRequired("grouperClient.syncTable." + this.configKey + ".tableSyncSubtype");
      this.gcTableSyncSubtype = GcTableSyncSubtype.valueOfIgnoreCase(tableSyncSubType, true);
    }

    //  grouperClient.syncTable.personSource.columns = *
    this.columnsString = GrouperClientUtils.defaultIfBlank(this.retrieveConfigString("columns", false), "*");
    debugMap.put("configColumns", this.columnsString);

    //  grouperClient.syncTable.personSource.primaryKeyColumns = penn_id
    this.primaryKeyColumnsString = GrouperClientUtils.defaultIfBlank(this.retrieveConfigString("primaryKeyColumns", false), "*");
    debugMap.put("configPrimaryKeyColumns", this.primaryKeyColumnsString);

    // grouperClient.syncTable.personSource.fullSyncChangeFlagColumn = check_sum
    this.fullSyncChangeFlagColumnString = this.retrieveConfigString("fullSyncChangeFlagColumn", this.gcTableSyncSubtype == GcTableSyncSubtype.fullSyncChangeFlag);
    if (!GrouperClientUtils.isBlank(this.fullSyncChangeFlagColumnString)) {
      debugMap.put("fullSyncChangeFlagColumn", this.fullSyncChangeFlagColumnString);
    }
    
    // grouperClient.syncTable.personSource.incrementalAllColumnsColumn = last_updated
    this.incrementalAllColumnsColumnString = this.retrieveConfigString("incrementalAllColumnsColumn", this.gcTableSyncSubtype == GcTableSyncSubtype.incrementalAllColumns);
    if (!GrouperClientUtils.isBlank(this.incrementalAllColumnsColumnString)) {
      debugMap.put("incrementalAllColumnsColumn", this.incrementalAllColumnsColumnString);
    }
    
    //  grouperClient.syncTable.personSource.groupColumn = penn_id
    this.groupColumnString = this.retrieveConfigString("groupColumn", false);
    if (this.gcTableSyncSubtype.isNeedsGroupColumn() ) {
      if (GrouperClientUtils.isBlank(this.groupColumnString)) {
        this.groupColumnString = this.primaryKeyColumnsString;
      }
    }
    if (!GrouperClientUtils.isBlank(this.groupColumnString)) {
      debugMap.put("configGroupColumn", this.groupColumnString);
    }
    
    // grouperClient.syncTable.personSource.batchSize = 50
    // size of jdbc batches
    this.batchSize = GrouperClientUtils.defaultIfNull(this.retrieveConfigInt("batchSize", false), 1000);
    if (this.batchSize != 1000) {
      debugMap.put("configBatchSize", this.batchSize);
    }
    
    // grouperClient.syncTable.personSource.groupingSize = 10000
    // the group column is what is uniquely selected, and then batched through to get data, defaults to 10000
    // size of jdbc batches
    this.groupingSize = GrouperClientUtils.defaultIfNull(this.retrieveConfigInt("groupingSize", false), 10000);
    if (this.groupingSize != 10000) {
      debugMap.put("configGroupingSize", this.groupingSize);
    }

    // grouperClient.syncTable.personSource.statusDatabase = awsDev
    this.statusDatabase = GrouperClientUtils.defaultIfNull(this.retrieveConfigString("statusDatabase", false), "grouper");
    if (!StringUtils.equals("grouper", this.statusDatabase)) {
      debugMap.put("configStatusDatabase", this.statusDatabase);
    }
    
    // grouperClient.syncTable.personSource.statusTable = awsDev
    this.statusTable = GrouperClientUtils.defaultIfNull(this.retrieveConfigString("statusTable", false), "grouper_sync_status");
    if (!StringUtils.equals("grouper_sync_status", this.statusTable)) {
      debugMap.put("configStatusTable", this.statusTable);
    }

    // grouperClient.syncTable.personSource.incrementalPrimaryKeyTable = real_time_table
    this.incrementalPrimaryKeyTable = GrouperClientUtils.defaultIfNull(this.retrieveConfigString("incrementalPrimaryKeyTable", false), this.tableFrom);
    if (!GrouperClientUtils.isBlank(this.incrementalPrimaryKeyTable)) {
      debugMap.put("incrementalPrimaryKeyTable", this.incrementalPrimaryKeyTable);
    }

    // grouperClient.syncTable.personSource.realTimeLastUpdatedColumn = last_updated
    this.realTimeLastUpdatedColumnString = this.retrieveConfigString("realTimeLastUpdatedColumn", false);

    // realTimeColumnsString comma separated list of column names to select from real time table
    this.realTimeColumnsString = GrouperClientUtils.defaultIfNull(this.retrieveConfigString("realTimeColumnsString", false), "*");
    if (!StringUtils.equals(this.realTimeColumnsString, "*")) {
      debugMap.put("realTimeColumnsString", this.realTimeColumnsString);
    }

    
  }

  /**
   * key in config that points to this instance of table sync
   * @return the key
   */
  public String getConfigKey() {
    return this.configKey;
  }

  /**
   * table where real time primary key and last_updated col is
   * @return the realTimeTable
   */
  public String getIncrementalPrimaryKeyTable() {
    return this.incrementalPrimaryKeyTable;
  }

  /**
   * @return the statusDatabase
   */
  public String getStatusDatabase() {
    return this.statusDatabase;
  }

  /**
   * grouperClient.syncTable.personSource.statusTable = grouper_chance_log_consumer
   * @return the statusTable
   */
  public String getStatusTable() {
    return this.statusTable;
  }

  /**
   * table name from
   * @return the tableName
   */
  public String getTableFrom() {
    return this.tableFrom;
  }

  /**
   * table name to
   * @return the tableNameTo
   */
  public String getTableTo() {
    return this.tableTo;
  }

  /**
   * group column
   * @return the groupColumn
   */
  public String getGroupColumnString() {
    return this.groupColumnString;
  }

  /**
   * real time last updated column
   * @return if real time last updated
   */
  public String getRealTimeLastUpdatedColumnString() {
    return this.realTimeLastUpdatedColumnString;
  }

  /**
   * database from key
   * @param databaseFrom1 the databaseFrom to set
   */
  public void setDatabaseFrom(String databaseFrom1) {
    this.databaseFrom = databaseFrom1;
  }

  /**
   * database to key
   * @param databaseTo1 the databaseTo to set
   */
  public void setDatabaseTo(String databaseTo1) {
    this.databaseTo = databaseTo1;
  }

  /**
   * @param groupColumn1 the groupColumn to set
   */
  public void setGroupColumnString(String groupColumn1) {
    this.groupColumnString = groupColumn1;
  }

  /**
   * @param groupSize1 the groupSize to set
   */
  public void setGroupingSize(int groupSize1) {
    this.groupingSize = groupSize1;
  }

  /**
   * key in config that points to this instance of table sync
   * @param key1 the key to set
   */
  public void setConfigKey(String key1) {
    this.configKey = key1;
  }

  /**
   * @param b
   */
  public void setRealTimeLastUpdatedColumnString(String b) {
    this.realTimeLastUpdatedColumnString = b;
  }

  /**
   * table where real time primary key and last_updated col is
   * @param realTimeTable1 the realTimeTable to set
   */
  public void setIncrementalPrimaryKeyTable(String realTimeTable1) {
    this.incrementalPrimaryKeyTable = realTimeTable1;
  }

  /**
   * grouperClient.syncTable.personSource.statusDatabase = awsDev
   * @param statusDatabase1 the statusDatabase to set
   */
  public void setStatusDatabase(String statusDatabase1) {
    this.statusDatabase = statusDatabase1;
  }

  /**
   * grouperClient.syncTable.personSource.statusTable = grouper_chance_log_consumer
   * @param statusTable1 the statusTable to set
   */
  public void setStatusTable(String statusTable1) {
    this.statusTable = statusTable1;
  }

  /**
   * table name from
   * @param tableName1 the tableName to set
   */
  public void setTableFrom(String tableName1) {
    this.tableFrom = tableName1;
  }

  /**
   * table name to
   * @param tableNameTo1 the tableNameTo to set
   */
  public void setTableTo(String tableNameTo1) {
    this.tableTo = tableNameTo1;
  }


}
