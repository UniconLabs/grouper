/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperClient.jdbc.tableSync;

import java.sql.Timestamp;
import java.util.Set;

import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import edu.internet2.middleware.grouperClient.jdbc.GcDbVersionable;
import edu.internet2.middleware.grouperClient.jdbc.GcPersist;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableClass;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableField;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableHelper;
import edu.internet2.middleware.grouperClient.jdbc.GcSqlAssignPrimaryKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.lang3.builder.EqualsBuilder;


/**
 * data about groups being synced
 */
@GcPersistableClass(tableName="grouper_sync_group", defaultFieldPersist=GcPersist.doPersist)
public class GcGrouperSyncGroup implements GcSqlAssignPrimaryKey, GcDbVersionable {

  //########## START GENERATED BY GcDbVersionableGenerate.java ###########
  /** save the state when retrieving from DB */
  @GcPersistableField(persist = GcPersist.dontPersist)
  private GcGrouperSyncGroup dbVersion = null;

  /**
   * take a snapshot of the data since this is what is in the db
   */
  @Override
  public void dbVersionReset() {
    //lets get the state from the db so we know what has changed
    this.dbVersion = this.clone();
  }

  /**
   * if we need to update this object
   * @return if needs to update this object
   */
  @Override
  public boolean dbVersionDifferent() {
    return !this.equalsDeep(this.dbVersion);
  }

  /**
   * db version
   */
  @Override
  public void dbVersionDelete() {
    this.dbVersion = null;
  }

  /**
   * deep clone the fields in this object
   */
  @Override
  public GcGrouperSyncGroup clone() {

    GcGrouperSyncGroup gcGrouperSyncGroup = new GcGrouperSyncGroup();
    //connectionName  DONT CLONE
  
    //dbVersion  DONT CLONE

    gcGrouperSyncGroup.errorCodeDb = this.errorCodeDb;
    gcGrouperSyncGroup.errorMessage = this.errorMessage;
    gcGrouperSyncGroup.errorTimestamp = this.errorTimestamp;
    gcGrouperSyncGroup.groupFromId2 = this.groupFromId2;
    gcGrouperSyncGroup.groupFromId3 = this.groupFromId3;
    gcGrouperSyncGroup.groupId = this.groupId;
    gcGrouperSyncGroup.groupIdIndex = this.groupIdIndex;
    gcGrouperSyncGroup.groupName = this.groupName;
    gcGrouperSyncGroup.groupToId2 = this.groupToId2;
    gcGrouperSyncGroup.groupToId3 = this.groupToId3;
    //grouperSync  DONT CLONE
  
    gcGrouperSyncGroup.grouperSyncId = this.grouperSyncId;
    gcGrouperSyncGroup.id = this.id;
//    gcGrouperSyncGroup.inGrouperDb = this.inGrouperDb;
//    gcGrouperSyncGroup.inGrouperEnd = this.inGrouperEnd;
//    gcGrouperSyncGroup.inGrouperInsertOrExistsDb = this.inGrouperInsertOrExistsDb;
//    gcGrouperSyncGroup.inGrouperStart = this.inGrouperStart;
    gcGrouperSyncGroup.inTargetDb = this.inTargetDb;
    gcGrouperSyncGroup.inTargetEnd = this.inTargetEnd;
    gcGrouperSyncGroup.inTargetInsertOrExistsDb = this.inTargetInsertOrExistsDb;
    gcGrouperSyncGroup.inTargetStart = this.inTargetStart;
    gcGrouperSyncGroup.lastGroupMetadataSync = this.lastGroupMetadataSync;
    gcGrouperSyncGroup.lastGroupMetadataSyncStart = this.lastGroupMetadataSyncStart;
    gcGrouperSyncGroup.lastGroupSync = this.lastGroupSync;
    gcGrouperSyncGroup.lastGroupSyncStart = this.lastGroupSyncStart;
    gcGrouperSyncGroup.lastTimeWorkWasDone = this.lastTimeWorkWasDone;
    //lastUpdated  DONT CLONE
  
    gcGrouperSyncGroup.metadataUpdated = this.metadataUpdated;
    gcGrouperSyncGroup.provisionableDb = this.provisionableDb;
    gcGrouperSyncGroup.provisionableEnd = this.provisionableEnd;
    gcGrouperSyncGroup.provisionableStart = this.provisionableStart;

    return gcGrouperSyncGroup;
  }

  /**
   *
   */
  public boolean equalsDeep(Object obj) {
    if (this==obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof GcGrouperSyncGroup)) {
      return false;
    }
    GcGrouperSyncGroup other = (GcGrouperSyncGroup) obj;

    return new EqualsBuilder()


      //connectionName  DONT EQUALS

      //dbVersion  DONT EQUALS
      .append(this.errorCodeDb, other.errorCodeDb)
      .append(this.errorMessage, other.errorMessage)
      .append(this.errorTimestamp, other.errorTimestamp)

      .append(this.groupFromId2, other.groupFromId2)
      .append(this.groupFromId3, other.groupFromId3)
      .append(this.groupId, other.groupId)
      .append(this.groupIdIndex, other.groupIdIndex)
      .append(this.groupName, other.groupName)
      .append(this.groupToId2, other.groupToId2)
      .append(this.groupToId3, other.groupToId3)
      //grouperSync  DONT EQUALS

      .append(this.grouperSyncId, other.grouperSyncId)
      .append(this.id, other.id)
//      .append(this.inGrouperDb, other.inGrouperDb)
//      .append(this.inGrouperEnd, other.inGrouperEnd)
//      .append(this.inGrouperInsertOrExistsDb, other.inGrouperInsertOrExistsDb)
//      .append(this.inGrouperStart, other.inGrouperStart)
      .append(this.inTargetDb, other.inTargetDb)
      .append(this.inTargetEnd, other.inTargetEnd)
      .append(this.inTargetInsertOrExistsDb, other.inTargetInsertOrExistsDb)
      .append(this.inTargetStart, other.inTargetStart)
      .append(this.lastGroupMetadataSync, other.lastGroupMetadataSync)
      .append(this.lastGroupMetadataSyncStart, other.lastGroupMetadataSyncStart)
      .append(this.lastGroupSync, other.lastGroupSync)
      .append(this.lastGroupSyncStart, other.lastGroupSyncStart)
      .append(this.lastTimeWorkWasDone, other.lastTimeWorkWasDone)
      //lastUpdated  DONT EQUALS

      .append(this.metadataUpdated, other.metadataUpdated)
      .append(this.provisionableDb, other.provisionableDb)
      .append(this.provisionableEnd, other.provisionableEnd)
      .append(this.provisionableStart, other.provisionableStart)
        .isEquals();

  }
  //########## END GENERATED BY GcDbVersionableGenerate.java ###########

  /**
   * when this group was last synced
   */
  private Timestamp lastGroupSync;
  
  /**
   * when this group was last sync start
   */
  private Timestamp lastGroupSyncStart;
  
  /**
   * when this group was last sync start
   * @return
   */
  public Timestamp getLastGroupSyncStart() {
    return lastGroupSyncStart;
  }

  /**
   * when this group was last sync start
   * @param lastGroupSyncStart
   */
  public void setLastGroupSyncStart(Timestamp lastGroupSyncStart) {
    this.lastGroupSyncStart = lastGroupSyncStart;
  }

  public Timestamp getLastGroupSync() {
    return this.lastGroupSync;
  }


  /**
   * when this group was last synced
   * @param lastGroupSync1
   */
  public void setLastGroupSync(Timestamp lastGroupSync1) {
    this.lastGroupSync = lastGroupSync1;
  }

  /**
   * when this groups name and description and metadata was synced, start
   */
  private Timestamp lastGroupMetadataSyncStart;
  

  /**
   * when this groups name and description and metadata was synced, start
   * @return
   */
  public Timestamp getLastGroupMetadataSyncStart() {
    return lastGroupMetadataSyncStart;
  }

  /**
   * when this groups name and description and metadata was synced, start
   * @param lastGroupMetadataSyncStart
   */
  public void setLastGroupMetadataSyncStart(Timestamp lastGroupMetadataSyncStart) {
    this.lastGroupMetadataSyncStart = lastGroupMetadataSyncStart;
  }

  /**
   * when this groups name and description and metadata was synced
   */
  private Timestamp lastGroupMetadataSync;
  
  /**
   * when this groups name and description and metadata was synced
   * @return when this group was last synced
   */
  public Timestamp getLastGroupMetadataSync() {
    return this.lastGroupMetadataSync;
  }

  /**
   * when this groups name and description and metadata was synced
   * @param lastGroupMetadataSync
   */
  public void setLastGroupMetadataSync(Timestamp lastGroupMetadataSync) {
    this.lastGroupMetadataSync = lastGroupMetadataSync;
  }


  /**
   * delete all data if table is here
   */
  public static void reset() {
    
    try {
      // if its not there forget about it... TODO remove this in 2.5+
      new GcDbAccess().connectionName("grouper").sql("select * from " + GcPersistableHelper.tableName(GcGrouperSyncGroup.class) + " where 1 != 1").select(Integer.class);
    } catch (Exception e) {
      return;
    }

    new GcDbAccess().connectionName("grouper").sql("delete from " + GcPersistableHelper.tableName(GcGrouperSyncGroup.class)).executeSql();
  }

  /**
   * prepare to store
   */
  public void storePrepare() {
    this.lastUpdated = new Timestamp(System.currentTimeMillis());
    this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
    this.errorMessage = GrouperClientUtils.abbreviate(this.errorMessage, 3700);
  }
  
  /**
   * 
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private GcGrouperSync grouperSync;
  
  /**
   * 
   * @return gc grouper sync
   */
  public GcGrouperSync getGrouperSync() {
    return this.grouperSync;
  }
  
  /**
   * 
   * @param gcGrouperSync
   */
  public void setGrouperSync(GcGrouperSync gcGrouperSync) {
    this.grouperSync = gcGrouperSync;
    this.grouperSyncId = gcGrouperSync == null ? null : gcGrouperSync.getId();
    this.connectionName = gcGrouperSync == null ? this.connectionName : gcGrouperSync.getConnectionName();

  }
  
  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   */
  @GcPersistableField(columnName="error_code")
  private String errorCodeDb;

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @return
   */
  public String getErrorCodeDb() {
    return errorCodeDb;
  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @param errorCodeDb
   */
  public void setErrorCodeDb(String errorCodeDb) {
    this.errorCodeDb = errorCodeDb;
  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @return
   */
  public GcGrouperSyncErrorCode getErrorCode() {
    if (this.errorCodeDb == null) {
      return null;
    }
    return GrouperClientUtils.enumValueOfIgnoreCase(GcGrouperSyncErrorCode.class, this.errorCodeDb, false);
  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @param gcGrouperSyncErrorCode
   */
  public void setErrorCode(GcGrouperSyncErrorCode gcGrouperSyncErrorCode) {
    this.errorCodeDb = gcGrouperSyncErrorCode == null ? null : gcGrouperSyncErrorCode.name();
  }
  
  /**
   * if the last sync had an error, this is the error message
   */
  private String errorMessage; 

  /**
   * this the last sync had an error, this was the error timestamp
   */
  private Timestamp errorTimestamp;
  
  /**
   * if the last sync had an error, this is the error message
   * @return error message
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * if the last sync had an error, this is the error message
   * @param errorMessage1
   */
  public void setErrorMessage(String errorMessage1) {
    this.errorMessage = errorMessage1;
  }

  /**
   * this the last sync had an error, this was the error timestamp
   * @return error timestamp
   */
  public Timestamp getErrorTimestamp() {
    return this.errorTimestamp;
  }

  /**
   * this the last sync had an error, this was the error timestamp
   * @param errorTimestamp1
   */
  public void setErrorTimestamp(Timestamp errorTimestamp1) {
    this.errorTimestamp = errorTimestamp1;
  }

  /**
   * connection name or null for default
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private String connectionName;

  /**
   * connection name or null for default
   * @return connection name
   */
  public String getConnectionName() {
    return this.connectionName;
  }

  /**
   * connection name or null for default
   * @param connectionName1
   */
  public void setConnectionName(String connectionName1) {
    this.connectionName = connectionName1;
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    
  }
  
  private static Set<String> toStringFieldNamesToIgnore = GrouperClientUtils.toSet("connectionName", "dbVersion", "debugMap", 
      "grouperSync", "grouperSyncId", "lastGroupMetadataSync", "lastGroupMetadataSyncStart", "lastGroupSync", "lastUpdated",
      "metadataUpdated");
  
  /**
   * 
   */
  @Override
  public String toString() {
    return GrouperClientUtils.toStringReflection(this, toStringFieldNamesToIgnore);
  }

  /**
   * last time a record was processed
   */
  private Timestamp lastTimeWorkWasDone;
  
  /**
   * last time a record was processe
   * @return last time a record was processed
   */
  public Timestamp getLastTimeWorkWasDone() {
    return this.lastTimeWorkWasDone;
  }

  /**
   * last time a record was processe
   * @param lastTimeWorkWasDone1
   */
  public void setLastTimeWorkWasDone(Timestamp lastTimeWorkWasDone1) {
    this.lastTimeWorkWasDone = lastTimeWorkWasDone1;
  }

  /**
   * 
   */
  public GcGrouperSyncGroup() {
  }
  
  /**
   * uuid of this record in this table
   */
  @GcPersistableField(primaryKey=true, primaryKeyManuallyAssigned=false)
  private String id;

  
  /**
   * uuid of this record in this table
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  
  /**
   * uuid of this record in this table
   * @param id1 the id to set
   */
  public void setId(String id1) {
    this.id = id1;
  }

  /**
   * if this group exists in the target/destination
   */
  @GcPersistableField(columnName="in_target")
  private String inTargetDb;
  
  /**
   * if this group exists in the target/destination
   * @return if in target
   */
  public String getInTargetDb() {
    return this.inTargetDb;
  }

  /**
   * if this group exists in the target/destination
   * @param inTargetDb1
   */
  public void setInTargetDb(String inTargetDb1) {
    this.inTargetDb = inTargetDb1;
  }

  /**
   * if in target
   * @return if in target
   */
  public boolean isInTarget() {
    return GrouperClientUtils.booleanValue(this.inTargetDb, false);
  }

  /**
   * if in target
   * @param in target
   */
  public void setInTarget(boolean inTarget) {
    this.inTargetDb = inTarget ? "T" : "F";
  }
  

  /**
   * if this group exists in the target/destination
   * @return if is target
   */
  public Boolean getInTarget() {
    return GrouperClientUtils.booleanObjectValue(this.inTargetDb);
  }
  
  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   */
  @GcPersistableField(columnName="in_target_insert_or_exists")
  private String inTargetInsertOrExistsDb;

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @return true or false
   */
  public String getInTargetInsertOrExistsDb() {
    return this.inTargetInsertOrExistsDb;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @param inTargetInsertOrExistsDb1
   */
  public void setInTargetInsertOrExistsDb(String inTargetInsertOrExistsDb1) {
    this.inTargetInsertOrExistsDb = inTargetInsertOrExistsDb1;
  }

  /**
   * when metadata was last updated
   */
  private Timestamp metadataUpdated;
  
  
  
  /**
   * when metadata was last updated
   * @return
   */
  public Timestamp getMetadataUpdated() {
    return this.metadataUpdated;
  }

  /**
   * when metadata was last updated
   * @param metadataUpdated1
   */
  public void setMetadataUpdated(Timestamp metadataUpdated1) {
    this.metadataUpdated = metadataUpdated1;
  }


  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @return true or false
   */
  public boolean isInTargetInsertOrExists() {
    return GrouperClientUtils.booleanValue(this.inTargetInsertOrExistsDb, false);
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @param inTargetInsertOrExists
   */
  public void setInTargetInsertOrExists(boolean inTargetInsertOrExists) {
    this.inTargetInsertOrExistsDb = inTargetInsertOrExists ? "T" : "F";
  }
  
  /**
   * uuid of the job in grouper_sync
   */
  private String grouperSyncId;
  
  /**
   * uuid of the job in grouper_sync
   * @return uuid of the job in grouper_sync
   */ 
  public String getGrouperSyncId() {
    return this.grouperSyncId;
  }

  /**
   * uuid of the job in grouper_sync
   * @param grouperSyncId1
   */
  public void setGrouperSyncId(String grouperSyncId1) {
    this.grouperSyncId = grouperSyncId1;
    if (this.grouperSync == null || !GrouperClientUtils.equals(this.grouperSync.getId(), grouperSyncId1)) {
      this.grouperSync = null;
    }
  }

  /**
   * when this record was last updated
   */
  private Timestamp lastUpdated;
  
  /**
   * when this record was last updated
   * @return the lastUpdated
   */
  public Timestamp getLastUpdated() {
    return this.lastUpdated;
  }

  /**
   * when this record was last updated
   * @param lastUpdated1
   */
  public void setLastUpdated(Timestamp lastUpdated1) {
    this.lastUpdated = lastUpdated1;
  }

  /**
   * for groups this is the group uuid, though not a real foreign key
   */
  private String groupId;
  
  /**
   * for groups this is the group uuid, though not a real foreign key
   * @return group id
   */
  public String getGroupId() {
    return this.groupId;
  }

  /**
   * for groups this is the group uuid, though not a real foreign key
   * @param groupId1
   */
  public void setGroupId(String groupId1) {
    this.groupId = groupId1;
  }

  /**
   * for groups this is the group system name
   */
  private String groupName;
  
  /**
   * for groups this is the group system name
   * @return group name
   */
  public String getGroupName() {
    return this.groupName;
  }

  /**
   * for groups this is the group system name
   * @param groupName1
   */
  public void setGroupName(String groupName1) {
    this.groupName = groupName1;
  }

  /**
   * T if provisionable and F is not
   */
  @GcPersistableField(columnName="provisionable")
  private String provisionableDb;
  
  /**
   * T if provisionable and F is not
   * @return if provisionable
   */
  public String getProvisionableDb() {
    return this.provisionableDb;
  }

  /**
   * T if provisionable and F is not
   * @param provisionableDb1
   */
  public void setProvisionableDb(String provisionableDb1) {
    this.provisionableDb = provisionableDb1;
  }

  /**
   * if provisionable
   * @return if provisionable
   */
  public boolean isProvisionable() {
    return GrouperClientUtils.booleanValue(this.provisionableDb, false);
  }

  /**
   * if provisionable
   * @param provisionable
   */
  public void setProvisionable(boolean provisionable) {
    this.provisionableDb = provisionable ? "T" : "F";
  }
  
  /**
   * millis since 1970 that this group started to be provisionable
   */
  private Timestamp provisionableStart;
    
  /**
   * millis since 1970 that this group started to be provisionable
   * @return millis
   */
  public Timestamp getProvisionableStart() {
    return this.provisionableStart;
  }

  /**
   * millis since 1970 that this group started to be provisionable
   * @param provisionableStartMillis1
   */
  public void setProvisionableStart(Timestamp provisionableStartMillis1) {
    this.provisionableStart = provisionableStartMillis1;
  }

  /**
   * when this group was provisioned to target
   */
  private Timestamp inTargetStart;
  
  /**
   * when this group was removed from target
   */
  private Timestamp inTargetEnd;

  
  
  /**
   * when this group was provisioned to target
   * @return when
   */
  public Timestamp getInTargetStart() {
    return this.inTargetStart;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetStart1
   */
  public void setInTargetStart(Timestamp inTargetStart1) {
    this.inTargetStart = inTargetStart1;
  }

  /**
   * when this group was provisioned to target
   * @return when
   */
  public Timestamp getInTargetEnd() {
    return this.inTargetEnd;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetEnd1
   */
  public void setInTargetEnd(Timestamp inTargetEnd1) {
    this.inTargetEnd = inTargetEnd1;
  }

  /**
   * millis since 1970 that this group ended being provisionable
   */
  private Timestamp provisionableEnd;

  /**
   * millis since 1970 that this group ended being provisionable
   * @return millis
   */
  public Timestamp getProvisionableEnd() {
    return this.provisionableEnd;
  }

  /**
   * millis since 1970 that this group ended being provisionable
   * @param provisionableEndMillis1
   */
  public void setProvisionableEnd(Timestamp provisionableEndMillis1) {
    this.provisionableEnd = provisionableEndMillis1;
  }

  /**
   * if group then this is the id index
   */
  private Long groupIdIndex;
  
  /**
   * if group then this is the id index
   * @return id index
   */
  public Long getGroupIdIndex() {
    return this.groupIdIndex;
  }

  /**
   * if group then this is the id index
   * @param groupIdIndex1
   */
  public void setGroupIdIndex(Long groupIdIndex1) {
    this.groupIdIndex = groupIdIndex1;
  }

  /**
   * metadata on groups
   */
  private String groupFromId2;

  /**
   * metadata on groups
   * @return group from id 2
   */
  public String getGroupFromId2() {
    return this.groupFromId2;
  }

  /**
   * metadata on groups
   * @param groupFromId2_1
   */
  public void setGroupFromId2(String groupFromId2_1) {
    this.groupFromId2 = groupFromId2_1;
  }

  /**
   * other metadata on groups
   */
  private String groupFromId3;

  /**
   * other metadata on groups
   * @return id3
   */
  public String getGroupFromId3() {
    return this.groupFromId3;
  }

  /**
   * other metadata on groups
   * @param groupFromId3_1
   */
  public void setGroupFromId3(String groupFromId3_1) {
    this.groupFromId3 = groupFromId3_1;
  }

  /**
   * other metadata on groups
   */
  private String groupToId2;
  
  /**
   * other metadata on groups
   * @return metadata
   */
  public String getGroupToId2() {
    return this.groupToId2;
  }

  /**
   * other metadata on groups
   * @param groupToId2_1
   */
  public void setGroupToId2(String groupToId2_1) {
    this.groupToId2 = groupToId2_1;
  }

  /**
   * other metadata on groups
   */
  private String groupToId3;

//  /**
//   * T if inserted on the in_grouper_start date, or F if it existed then and not sure when inserted
//   * @return true or false
//   */
//  public boolean isInGrouperInsertOrExists() {
//    return GrouperClientUtils.booleanValue(this.inGrouperInsertOrExistsDb, false);
//  }
//  
//  /**
//   * T if inserted on the in_grouper_start date, or F if it existed then and not sure when inserted
//   * @param inGrouperInsertOrExists
//   */
//  public void setInGrouperInsertOrExists(boolean inGrouperInsertOrExists) {
//    this.inGrouperInsertOrExistsDb = inGrouperInsertOrExists ? "T" : "F";
//  }
//  
//  /**
//   * if in grouper
//   * @return if in grouper
//   */
//  public boolean isInGrouper() {
//    return GrouperClientUtils.booleanValue(this.inGrouperDb, false);
//  }
//
//  /**
//   * if in grouper
//   * @param in grouper
//   */
//  public void setInGrouper(boolean inGrouper) {
//    this.inGrouperDb = inGrouper ? "T" : "F";
//  }
//  
//  /**
//   * if this group exists in grouper
//   */
//  @GcPersistableField(columnName="in_grouper")
//  private String inGrouperDb;
//
//  /**
//   * when this group was removed from grouper
//   */
//  private Timestamp inGrouperEnd;
//
//  /**
//   * when this group was added to grouper
//   */
//  private Timestamp inGrouperStart;
//
//  /**
//   * T if inserted on the in_grouper_start date, or F if it existed then and not sure when inserted
//   */
//  @GcPersistableField(columnName="in_grouper_insert_or_exists")
//  private String inGrouperInsertOrExistsDb;
//  
//  /**
//   * if this group exists in grouper
//   * @return if is target
//   */
//  public Boolean getInGrouper() {
//    return GrouperClientUtils.booleanObjectValue(this.inGrouperDb);
//  }
//
//  /**
//   * if this group exists in grouper T/F
//   * @return
//   */
//  public String getInGrouperDb() {
//    return inGrouperDb;
//  }
//
//  /**
//   * if this group exists in grouper T/F
//   * @param inGrouperDb
//   */
//  public void setInGrouperDb(String inGrouperDb) {
//    this.inGrouperDb = inGrouperDb;
//  }
//
//  /**
//   * when this group was removed from grouper
//   * @return
//   */
//  public Timestamp getInGrouperEnd() {
//    return inGrouperEnd;
//  }
//
//  /**
//   * when this group was removed from grouper
//   * @param inGrouperEnd
//   */
//  public void setInGrouperEnd(Timestamp inGrouperEnd) {
//    this.inGrouperEnd = inGrouperEnd;
//  }
//
//  /**
//   * when this group was added to grouper
//   * @return
//   */
//  public Timestamp getInGrouperStart() {
//    return inGrouperStart;
//  }
//
//  /**
//   * when this group was added to grouper
//   * @param inGrouperStart
//   */
//  public void setInGrouperStart(Timestamp inGrouperStart) {
//    this.inGrouperStart = inGrouperStart;
//  }
//
//  /**
//   * if the provisioner added to grouper or if it already existed
//   * @return
//   */
//  public String getInGrouperInsertOrExistsDb() {
//    return inGrouperInsertOrExistsDb;
//  }
//
//  /**
//   * if the provisioner added to grouper or if it already existed
//   * @param inGrouperInsertOrExistsDb
//   */
//  public void setInGrouperInsertOrExistsDb(String inGrouperInsertOrExistsDb) {
//    this.inGrouperInsertOrExistsDb = inGrouperInsertOrExistsDb;
//  }

  /**
   * other metadata on groups
   * @return group id
   */
  public String getGroupToId3() {
    return this.groupToId3;
  }

  /**
   * other metadata on groups
   * @param groupToId3_1
   */
  public void setGroupToId3(String groupToId3_1) {
    this.groupToId3 = groupToId3_1;
  }

  /**
   * 
   */
  @Override
  public boolean gcSqlAssignNewPrimaryKeyForInsert() {
    if (this.id != null) {
      return false;
    }
    this.id = GrouperClientUtils.uuid();
    return true;
  }

  /**
   * 
   * @param translateGrouperToGroupSyncField
   * @param result
   */
  public void assignField(String syncField, Object result) {
    if (GrouperClientUtils.equals("groupFromId2", syncField)) {
      this.setGroupFromId2(GrouperClientUtils.stringValue(result));
    } else if (GrouperClientUtils.equals("groupFromId3", syncField)) {
      this.setGroupFromId3(GrouperClientUtils.stringValue(result));
    } else if (GrouperClientUtils.equals("groupToId2", syncField)) {
      this.setGroupToId2(GrouperClientUtils.stringValue(result));
    } else if (GrouperClientUtils.equals("groupToId3", syncField)) {
      this.setGroupToId3(GrouperClientUtils.stringValue(result));
    } else {
      throw new RuntimeException("Not expecting groupSyncField: '" + syncField + "'");
    }

  }

}
