/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperClient.jdbc.tableSync;

import java.sql.Timestamp;

import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import edu.internet2.middleware.grouperClient.jdbc.GcPersist;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableClass;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableField;
import edu.internet2.middleware.grouperClient.jdbc.GcSqlAssignPrimaryKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.lang3.builder.ToStringBuilder;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.logging.Log;


/**
 * if doing user level syncs, this is the metadata
 */
@GcPersistableClass(tableName="grouper_sync_membership", defaultFieldPersist=GcPersist.doPersist)
public class GcGrouperSyncMembership implements GcSqlAssignPrimaryKey {

//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "id", 
//      Types.VARCHAR, "40", true, true);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "grouper_sync_id", 
//      Types.VARCHAR, "40", false, true);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "grouper_sync_group_id", 
//      Types.VARCHAR, "40", false, true);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "grouper_sync_member_id", 
//      Types.VARCHAR, "40", false, true);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "in_target", 
//      Types.VARCHAR, "1", false, false);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "in_target_insert_or_exists", 
//      Types.VARCHAR, "1", false, false);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "in_target_start", 
//      Types.TIMESTAMP, "10", false, false);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "in_target_end", 
//      Types.TIMESTAMP, "10", false, false);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "last_updated", 
//      Types.TIMESTAMP, "10", false, true);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "membership_id", 
//      Types.VARCHAR, "800", false, false);
//
//  GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "membership_id2", 
//      Types.VARCHAR, "800", false, false);
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "id", "uuid of this record");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "grouper_sync_id", "foreign key back to the sync table");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "grouper_sync_group_id", "foreign key back to group table");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "grouper_sync_member_id", "foreign key back to user table");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "in_target", "T if exists in target/destination and F is not.  blank if not sure");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "in_target_insert_or_exists", "T if inserted on the in_target_start date, or F if it existed then and not sure when inserted");
//  
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "in_target_start", "when this was put in target");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "in_target_end", "when this was taken out of target");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "last_updated", "when this record was last updated");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "membership_id", "other metadata on membership");
//
//  GrouperDdlUtils.ddlutilsColumnComment(ddlVersionBean, tableName, "membership_id2", "other metadata on membership");
//

  /**
   * other metadata on membership
   */
  private String membershipId;
  
  /**
   * select grouper sync membership by id
   * @param theConnectionName
   * @param id
   * @return the sync
   */
  public static GcGrouperSyncMembership retrieveById(String theConnectionName, String id) {
    theConnectionName = GcGrouperSync.defaultConnectionName(theConnectionName);
    GcGrouperSyncMembership gcGrouperSyncMembership = new GcDbAccess().connectionName(theConnectionName)
        .sql("select * from grouper_sync_membership where id = ?").addBindVar(id).select(GcGrouperSyncMembership.class);
    if (gcGrouperSyncMembership != null) {
      gcGrouperSyncMembership.connectionName = theConnectionName;
    }
    return gcGrouperSyncMembership;
  }

  /**
   * 
   * @return sync
   */
  public GcGrouperSyncGroup retrieveGrouperSyncGroup() {
    if (this.grouperSyncGroup == null && this.grouperSyncGroupId != null) {
      this.grouperSyncGroup = GcGrouperSyncGroup.retrieveById(this.connectionName, this.grouperSyncGroupId);
    }
    return this.grouperSyncGroup;
  }

  /**
   * 
   * @return sync
   */
  public GcGrouperSyncMember retrieveGrouperSyncMember() {
    if (this.grouperSyncMember == null && this.grouperSyncMemberId != null) {
      this.grouperSyncMember = GcGrouperSyncMember.retrieveById(this.connectionName, this.grouperSyncMemberId);
    }
    return this.grouperSyncMember;
  }

  /**
   * other metadata on membership
   * @return metadata
   */
  public String getMembershipId() {
    return this.membershipId;
  }

  /**
   * other metadata on membership
   * @param membershipId1_1
   */
  public void setMembershipId(String membershipId1_1) {
    this.membershipId = membershipId1_1;
  }

  /**
   * other metadata on membership
   */
  private String membershipId2;
  
  
  
  /**
   * other metadata on membership
   * @return metadata
   */
  public String getMembershipId2() {
    return this.membershipId2;
  }


  /**
   * other metadata on membership
   * @param membershipId2_1
   */
  public void setMembershipId2(String membershipId2_1) {
    this.membershipId2 = membershipId2_1;
  }

  /**
   * link back to sync group
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private GcGrouperSyncGroup grouperSyncGroup = null;
  
  /**
   * link back to sync group
   * @return group
   */
  public GcGrouperSyncGroup getGrouperSyncGroup() {
    return this.grouperSyncGroup;
  }

  /**
   * link back to sync group
   * @param gcGrouperSyncGroup
   */
  public void setGrouperSyncGroup(GcGrouperSyncGroup gcGrouperSyncGroup) {
    this.grouperSyncGroup = gcGrouperSyncGroup;
    this.grouperSyncGroupId = gcGrouperSyncGroup == null ? null : gcGrouperSyncGroup.getId();
  }

  /**
   * link back to sync member
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private GcGrouperSyncMember grouperSyncMember = null;

  /**
   * link back to sync member
   * @return member
   */
  public GcGrouperSyncMember getGrouperSyncMember() {
    return this.grouperSyncMember;
  }

  /**
   * link back to sync member
   * @param gcGrouperSyncMember1
   */
  public void setGrouperSyncMember(GcGrouperSyncMember gcGrouperSyncMember1) {
    
    this.grouperSyncMember = gcGrouperSyncMember1;
    this.grouperSyncMemberId = gcGrouperSyncMember1 == null ? null : gcGrouperSyncMember1.getId();
  }

  /**
   * foreign key back to group table
   */
  private String grouperSyncGroupId;
  
  /**
   * foreign key back to group table
   * @return group id
   */
  public String getGrouperSyncGroupId() {
    return this.grouperSyncGroupId;
  }

  /**
   * foreign key back to group table
   * @param grouperSyncGroupId1
   */
  public void setGrouperSyncGroupId(String grouperSyncGroupId1) {
    this.grouperSyncGroupId = grouperSyncGroupId1;
    if (this.grouperSyncGroup == null || !GrouperClientUtils.equals(grouperSyncGroupId1, this.grouperSyncGroup.getId())) {
      this.grouperSyncGroup = null;
    }

  }

  /**
   * foreign key to the members sync table
   */
  private String grouperSyncMemberId;
  
  /**
   * foreign key to the members sync table
   * @return member id
   */
  public String getGrouperSyncMemberId() {
    return this.grouperSyncMemberId;
  }

  /**
   * foreign key to the members sync table
   * @param memberId1
   */
  public void setGrouperSyncMemberId(String memberId1) {
    this.grouperSyncMemberId = memberId1;
    if (this.grouperSyncMember == null || !GrouperClientUtils.equals(memberId1, this.grouperSyncMember.getId())) {
      this.grouperSyncMember = null;
    }
  }

  /**
   * 
   */
  private static Log LOG = GrouperClientUtils.retrieveLog(GcGrouperSyncMembership.class);

  /**
   * 
   * @param connectionName
   */
  public void store() {
    try {
      this.lastUpdated = new Timestamp(System.currentTimeMillis());
      this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
      new GcDbAccess().connectionName(this.connectionName).storeToDatabase(this);
    } catch (RuntimeException re) {
      
      LOG.info("GrouperSyncMembership uuid potential mismatch: " + this.grouperSyncGroupId + ", " + this.grouperSyncMemberId, re);

      // maybe a different uuid is there
      GcGrouperSyncMembership gcGrouperSyncMembership = retrieveByGroupIdAndMemberId(this.connectionName, this.grouperSyncGroupId, this.grouperSyncMemberId);
      if (gcGrouperSyncMembership != null) {
        this.id = gcGrouperSyncMembership.getId();
        new GcDbAccess().connectionName(connectionName).storeToDatabase(this);
        LOG.warn("GrouperSyncMembership uuid mismatch corrected: " + this.grouperSyncGroupId + ", " + this.grouperSyncMemberId);
      } else {
        throw re;
      }
    }
  }

  /**
   * select membership by group and member
   * @param theConnectionName
   * @param grouperSyncGroupId
   * @param grouperSyncMemberId
   * @return the sync
   */
  public static GcGrouperSyncMembership retrieveByGroupIdAndMemberId(String theConnectionName, String grouperSyncGroupId, String grouperSyncMemberId) {
    theConnectionName = GcGrouperSync.defaultConnectionName(theConnectionName);
    GcGrouperSyncMembership gcGrouperSyncMembership = new GcDbAccess().connectionName(theConnectionName)
        .sql("select * from grouper_sync_membership where grouper_sync_group_id = ? and grouper_sync_member_id = ?").addBindVar(grouperSyncGroupId).addBindVar(grouperSyncMemberId).select(GcGrouperSyncMembership.class);
    if (gcGrouperSyncMembership != null) {
      gcGrouperSyncMembership.connectionName = theConnectionName;
    }
    return gcGrouperSyncMembership;
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
   * @param connectionName
   */
  public void delete() {
    this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
    new GcDbAccess().connectionName(this.connectionName).deleteFromDatabase(this);
  }
  
  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    System.out.println("none");
    
    for (GcGrouperSyncMembership theGcGrouperSyncMembership : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncMembership.class)) {
      System.out.println(theGcGrouperSyncMembership.toString());
    }
    
    // foreign key
    GcGrouperSync gcGrouperSync = new GcGrouperSync();
    gcGrouperSync.setSyncEngine("temp");
    gcGrouperSync.setProvisionerName("myJob");
    gcGrouperSync.store();
    
    GcGrouperSyncGroup gcGrouperSyncGroup = new GcGrouperSyncGroup();
    gcGrouperSyncGroup.setGrouperSync(gcGrouperSync);
    gcGrouperSyncGroup.setLastTimeWorkWasDone(new Timestamp(System.currentTimeMillis() + 2000));
    gcGrouperSyncGroup.setGroupId("myId");
    gcGrouperSyncGroup.store();

    GcGrouperSyncMember gcGrouperSyncMember = new GcGrouperSyncMember();
    gcGrouperSyncMember.setGrouperSync(gcGrouperSync);
    gcGrouperSyncMember.setLastTimeWorkWasDone(new Timestamp(System.currentTimeMillis() + 2000));
    gcGrouperSyncMember.setMemberId("someId");
    gcGrouperSyncMember.store();

    GcGrouperSyncMembership gcGrouperSyncMembership = new GcGrouperSyncMembership();
    gcGrouperSyncMembership.setGrouperSyncGroup(gcGrouperSyncGroup);
    gcGrouperSyncMembership.setGrouperSyncMember(gcGrouperSyncMember);
    gcGrouperSyncMembership.inTargetDb = "T";
    gcGrouperSyncMembership.inTargetInsertOrExistsDb = "T";
    gcGrouperSyncMembership.inTargetEnd = new Timestamp(123L);
    gcGrouperSyncMembership.inTargetStart = new Timestamp(234L);
    gcGrouperSyncMembership.membershipId = "memId";
    gcGrouperSyncMembership.membershipId2 = "memId2";
    gcGrouperSyncMembership.store();

    System.out.println("stored");

    gcGrouperSyncMembership = retrieveByGroupIdAndMemberId("grouper", gcGrouperSyncGroup.getId(), gcGrouperSyncMember.getId());
    System.out.println(gcGrouperSyncMembership);
    
    gcGrouperSyncMembership.setMembershipId("memId1");
    gcGrouperSyncMembership.store();

    System.out.println("updated");

    for (GcGrouperSyncMembership theGcGrouperSyncMembership : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncMembership.class)) {
      System.out.println(theGcGrouperSyncMembership.toString());
    }

    gcGrouperSyncMembership.delete();
    gcGrouperSyncGroup.delete();
    gcGrouperSyncMember.delete();
    gcGrouperSync.delete();
    
    System.out.println("deleted");

    for (GcGrouperSyncGroup theGcGrouperSyncStatus : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncGroup.class)) {
      System.out.println(theGcGrouperSyncStatus.toString());
    }
  }
  
  
  /**
   * 
   */
  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("connectionName", this.connectionName)
        .append("grouperSyncMemberId", this.grouperSyncMemberId)
        .append("grouperSyncGroupId", this.grouperSyncGroupId)
        .append("inTargetDb", this.inTargetDb)
        .append("inTargetStart", this.inTargetStart)
        .append("inTargetEnd", this.inTargetEnd)
        .append("inTargetInsertOrExistsDb", this.inTargetInsertOrExistsDb)
        .append("lastUpdated", this.lastUpdated)
        .append("membershipId", this.membershipId)
        .append("membershipId2", this.membershipId2).build();
  }

  /**
   * 
   */
  public GcGrouperSyncMembership() {
  }
  
  /**
   * uuid of this record in this table
   */
  @GcPersistableField(primaryKey=true, primaryKeyManuallyAssigned=true)
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
   * if this group exists in the target/destination
   * @return if is target
   */
  public Boolean getInTarget() {
    return GrouperClientUtils.booleanObjectValue(this.inTargetDb);
  }
  
  /**
   * if this group exists in the target/destination
   * @param inTarget
   */
  public void setInTarget(Boolean inTarget) {
    this.inTargetDb = inTarget ? "T" : "F";
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
   * when this group was removed from target
   */
  private Timestamp inTargetEnd;
  /**
   * when this group was provisioned to target
   */
  private Timestamp inTargetStart;

  /**
   * 
   */
  @Override
  public void gcSqlAssignNewPrimaryKeyForInsert() {
    this.id = GrouperClientUtils.uuid();
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
   * @return when
   */
  public Timestamp getInTargetStart() {
    return this.inTargetStart;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetEnd1
   */
  public void setInTargetEnd(Timestamp inTargetEnd1) {
    this.inTargetEnd = inTargetEnd1;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetStart1
   */
  public void setInTargetStart(Timestamp inTargetStart1) {
    this.inTargetStart = inTargetStart1;
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

}
