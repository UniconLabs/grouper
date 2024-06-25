/**
 * Copyright 2019 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.grouper.app.upgradeTasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.MemberFinder;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.app.loader.GrouperDaemonDeleteOldRecords;
import edu.internet2.middleware.grouper.app.loader.GrouperLoaderConfig;
import edu.internet2.middleware.grouper.app.loader.OtherJobBase.OtherJobInput;
import edu.internet2.middleware.grouper.app.loader.db.GrouperLoaderDb;
import edu.internet2.middleware.grouper.app.serviceLifecycle.GrouperRecentMemberships;
import edu.internet2.middleware.grouper.app.usdu.UsduSettings;
import edu.internet2.middleware.grouper.attr.AttributeDef;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.assign.AttributeAssign;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefFinder;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefNameFinder;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.cfg.dbConfig.GrouperDbConfig;
import edu.internet2.middleware.grouper.ddl.GrouperDdlUtils;
import edu.internet2.middleware.grouper.entity.EntityUtils;
import edu.internet2.middleware.grouper.exception.GrouperSessionException;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.hooks.examples.AttributeAutoCreateHook;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.misc.AddMissingGroupSets;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.misc.GrouperSessionHandler;
import edu.internet2.middleware.grouper.permissions.limits.PermissionLimitUtils;
import edu.internet2.middleware.grouper.privs.AttributeDefPrivilege;
import edu.internet2.middleware.grouper.rules.RuleUtils;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;

/**
 * @author shilen
 */
public enum UpgradeTasks implements UpgradeTasksInterface {
  

  /**
   * add groupAttrRead/groupAttrUpdate group sets for entities
   */
  V1 {

    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      new AddMissingGroupSets().addMissingSelfGroupSetsForGroups();
      //new SyncPITTables().processMissingActivePITGroupSets();
    }
  },
  
  /**
   * move subject resolution status attributes to member table
   */
  V2 {

    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      AttributeDefName deletedMembersAttr = AttributeDefNameFinder.findByName(UsduSettings.usduStemName() + ":subjectResolutionDeleted", false);

      if (deletedMembersAttr != null) {
        Set<Member> deletedMembers = new MemberFinder()
            .assignAttributeCheckReadOnAttributeDef(false)
            .assignNameOfAttributeDefName(UsduSettings.usduStemName() + ":subjectResolutionDeleted")
            .addAttributeValuesOnAssignment("true")
            .findMembers();
        
        for (Member deletedMember : deletedMembers) {
          deletedMember.setSubjectResolutionDeleted(true);
          deletedMember.setSubjectResolutionResolvable(false);
          deletedMember.store();
        }
        
        deletedMembersAttr.delete();
      }
      
      AttributeDefName resolvableMembersAttr = AttributeDefNameFinder.findByName(UsduSettings.usduStemName() + ":subjectResolutionResolvable", false);

      if (resolvableMembersAttr != null) {
        Set<Member> unresolvableMembers = new MemberFinder()
            .assignAttributeCheckReadOnAttributeDef(false)
            .assignNameOfAttributeDefName(UsduSettings.usduStemName() + ":subjectResolutionResolvable")
            .addAttributeValuesOnAssignment("false")
            .findMembers();
        
        for (Member unresolvableMember : unresolvableMembers) {
          unresolvableMember.setSubjectResolutionResolvable(false);
          unresolvableMember.store();
        }
        
        resolvableMembersAttr.delete();
      }
    }
  },
  V3{

    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      GrouperRecentMemberships.upgradeFromV2_5_29_to_V2_5_30();
    }
    
  },
  V4{

    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {

      String recentMembershipsRootStemName = GrouperRecentMemberships.recentMembershipsStemName();
      String recentMembershipsMarkerDefName = recentMembershipsRootStemName + ":" + GrouperRecentMemberships.GROUPER_RECENT_MEMBERSHIPS_MARKER_DEF;
      AttributeDef recentMembershipsMarkerDef = GrouperDAOFactory.getFactory().getAttributeDef().findByNameSecure(
          recentMembershipsMarkerDefName, true, new QueryOptions().secondLevelCache(false));

      // these attribute tell a grouper rule to auto assign the three name value pair attributes to the assignment when the marker is assigned
      AttributeDefName autoCreateMarker = AttributeDefNameFinder.findByName(AttributeAutoCreateHook.attributeAutoCreateStemName() 
          + ":" + AttributeAutoCreateHook.GROUPER_ATTRIBUTE_AUTO_CREATE_MARKER, true);
      AttributeDefName thenNames = AttributeDefNameFinder.findByName(AttributeAutoCreateHook.attributeAutoCreateStemName() 
          + ":" + AttributeAutoCreateHook.GROUPER_ATTRIBUTE_AUTO_CREATE_ATTR_THEN_NAMES_ON_ASSIGN, true);

      AttributeAssign attributeAssign = recentMembershipsMarkerDef.getAttributeDelegate().retrieveAssignment("assign", autoCreateMarker, false, false);

      if (attributeAssign != null) {
        
        String thenNamesValue = attributeAssign.getAttributeValueDelegate().retrieveValueString(thenNames.getName());
        String shouldHaveValue = recentMembershipsRootStemName + ":" + GrouperRecentMemberships.GROUPER_RECENT_MEMBERSHIPS_ATTR_MICROS
            + ", " + recentMembershipsRootStemName + ":" + GrouperRecentMemberships.GROUPER_RECENT_MEMBERSHIPS_ATTR_GROUP_UUID_FROM 
                + ", " + recentMembershipsRootStemName + ":" + GrouperRecentMemberships.GROUPER_RECENT_MEMBERSHIPS_ATTR_INCLUDE_CURRENT;
        if (!StringUtils.equals(thenNamesValue, shouldHaveValue)) {
          attributeAssign.getAttributeValueDelegate().assignValue(thenNames.getName(), shouldHaveValue);
        }
      }

      
    }
    
  },
  V5 {

    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {

      RuleUtils.changeInheritedPrivsToActAsGrouperSystem();
      
    }
    
  },
  V6 {

    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {

      new AddMissingGroupSets().addMissingSelfGroupSetsForStems();

    }
    
  },
  V7 {
    
    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {

      Pattern gshTemplateFolderUuidsToShowPattern = Pattern.compile("^grouperGshTemplate\\.([^.]+)\\.folderUuidsToShow$");
      
      Map<String, String> properties = GrouperConfig.retrieveConfig().propertiesMap(gshTemplateFolderUuidsToShowPattern);
      
      if (GrouperUtil.length(properties) > 0) {
        
        for (String key : properties.keySet()) {
          
          Matcher matcher = gshTemplateFolderUuidsToShowPattern.matcher(key);
          if (matcher.matches()) {
            String configId = matcher.group(1);
            String folderUuidsToShow = properties.get("grouperGshTemplate." + configId + ".folderUuidsToShow");
            folderUuidsToShow = StringUtils.trim(folderUuidsToShow);
            
            String singularFolderUuidToShow = GrouperConfig.retrieveConfig().propertyValueString("grouperGshTemplate." + configId + ".folderUuidToShow");
            singularFolderUuidToShow = StringUtils.trim(singularFolderUuidToShow);
            
            if (!StringUtils.equals(folderUuidsToShow, singularFolderUuidToShow)) {
              new GrouperDbConfig().configFileName("grouper.properties").propertyName("grouperGshTemplate." + configId + ".folderUuidToShow")
              .value(folderUuidsToShow).store();
            }
            
          }
        }
        
      }

    }

  },
  V8 {
    
    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      // make sure id_index is populated in grouper_members and make column not null

      // check if grouper_members.id_index is already not null - better to use ddlutils here or check the resultset?
      /*
      Platform platform = GrouperDdlUtils.retrievePlatform(false);
      GrouperLoaderDb grouperDb = GrouperLoaderConfig.retrieveDbProfile("grouper");
      Connection connection = null;
      try {
        connection = grouperDb.connection();
        int javaVersion = GrouperDdlUtils.retrieveDdlJavaVersion("Grouper"); 
        DdlVersionable ddlVersionable = GrouperDdlUtils.retieveVersion("Grouper", javaVersion);
        DbMetadataBean dbMetadataBean = GrouperDdlUtils.findDbMetadataBean(ddlVersionable);
        platform.getModelReader().setDefaultTablePattern(dbMetadataBean.getDefaultTablePattern());
        platform.getModelReader().setDefaultSchemaPattern(dbMetadataBean.getSchema());
        
        Database database = platform.readModelFromDatabase(connection, "grouper", null, null, null);
        Table membersTable = database.findTable(Member.TABLE_GROUPER_MEMBERS);
        Column idIndexColumn = membersTable.findColumn(Member.COLUMN_ID_INDEX);
        
        if (idIndexColumn.isRequired()) {
          return;
        }
      } finally {
        GrouperUtil.closeQuietly(connection);
      }
      */
      
      GrouperLoaderDb grouperDb = GrouperLoaderConfig.retrieveDbProfile("grouper");
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try {
        connection = grouperDb.connection();
        preparedStatement = connection.prepareStatement("select id_index from grouper_members where subject_id='GrouperSystem'");
        resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metadata = resultSet.getMetaData();
        if (metadata.isNullable(1) == ResultSetMetaData.columnNoNulls) {
          return;
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      } finally {
        GrouperUtil.closeQuietly(resultSet);
        GrouperUtil.closeQuietly(preparedStatement);
        GrouperUtil.closeQuietly(connection);
      }
      
      // ok nulls are allowed so make the change
      GrouperDaemonDeleteOldRecords.verifyTableIdIndexes(null);
      
      String sql;
      
      if (GrouperDdlUtils.isOracle()) {
        sql = "ALTER TABLE grouper_members MODIFY (id_index NOT NULL)";
      } else if (GrouperDdlUtils.isMysql()) {
        sql = "ALTER TABLE grouper_members MODIFY id_index BIGINT NOT NULL";
      } else {
        // assume postgres
        sql = "ALTER TABLE grouper_members ALTER COLUMN id_index SET NOT NULL";
      }
      
      HibernateSession.bySqlStatic().executeSql(sql);
    }
  }
  , 
  V9{
    
    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      
      GrouperSession.internal_callbackRootGrouperSession(new GrouperSessionHandler() {
        
        @Override
        public Object callback(GrouperSession grouperSession) throws GrouperSessionException {
      
          String[] attributeDefNames = new String[] {
              // etc:attribute:entities:entitySubjectIdentifierDef
              EntityUtils.attributeEntityStemName() + ":entitySubjectIdentifierDef",
              // etc:attribute:permissionLimits:limitsDef
              PermissionLimitUtils.attributeLimitStemName() + ":" + PermissionLimitUtils.LIMIT_DEF,
              // etc:attribute:permissionLimits:limitsDefInt
              PermissionLimitUtils.attributeLimitStemName() + ":" + PermissionLimitUtils.LIMIT_DEF_INT,
              // etc:attribute:permissionLimits:limitsDefMarker
              PermissionLimitUtils.attributeLimitStemName() + ":" + PermissionLimitUtils.LIMIT_DEF_MARKER
              
              
          };

          for (String attributeDefName : attributeDefNames) {
            AttributeDef attributeDef = AttributeDefFinder.findByName(attributeDefName, false);
            
            if (attributeDef != null) {
              attributeDef.getPrivilegeDelegate().revokePriv(SubjectFinder.findAllSubject(), AttributeDefPrivilege.ATTR_READ, false);
              attributeDef.getPrivilegeDelegate().revokePriv(SubjectFinder.findAllSubject(), AttributeDefPrivilege.ATTR_UPDATE, false);
            }
          }          
          
          
          return null;
        }
      });
    }
  }, 
  V10{
    
    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      
      GrouperSession.internal_callbackRootGrouperSession(new GrouperSessionHandler() {
        
        @Override
        public Object callback(GrouperSession grouperSession) throws GrouperSessionException {
      
          try {
            if (!GrouperDdlUtils.assertIndexExists("grouper_loader_log", "grouper_loader_log_temp_st_idx")) {
              new GcDbAccess().sql("CREATE INDEX grouper_loader_log_temp_st_idx ON grouper_loader_log (job_name,started_time)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_loader_log_temp_st_idx");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_loader_log_temp_st_idx exists already");
              }
            }
  
            if (!GrouperDdlUtils.assertIndexExists("grouper_loader_log", "grouper_loader_log_temp_s2_idx")) {
              new GcDbAccess().sql("CREATE INDEX grouper_loader_log_temp_s2_idx ON grouper_loader_log (job_name,status,last_updated)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_loader_log_temp_s2");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_loader_log_temp_s2 exists already");
              }
            }
            if (!GrouperDdlUtils.assertIndexExists("grouper_loader_log", "grouper_loader_log_temp_s3_idx")) {
              new GcDbAccess().sql("CREATE INDEX grouper_loader_log_temp_s3_idx ON grouper_loader_log (status,last_updated)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_loader_log_temp_s3");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_loader_log_temp_s3 exists already");
              }
            }
            if (!GrouperDdlUtils.assertIndexExists("grouper_loader_log", "grouper_loader_log_temp_s4_idx")) {
              new GcDbAccess().sql("CREATE INDEX grouper_loader_log_temp_s4_idx ON grouper_loader_log (parent_job_name)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_loader_log_temp_s4");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_loader_log_temp_s4 exists already");
              }
            }
          } catch (Throwable t) {
            String message = "Could not perform upgrade task V10 on grouper_loader_log adding indexes for GRP-5195!  Skipping this upgrade task, install the indexes manually";
            LOG.error(message, t);
            if (otherJobInput != null) {
              otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", " + message);
            }
          }
          return null;
        }
      });
    }
  }, 
  
  V11 {
    
    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      
      GrouperSession.internal_callbackRootGrouperSession(new GrouperSessionHandler() {
        
        @Override
        public Object callback(GrouperSession grouperSession) throws GrouperSessionException {
          
          try {
            
            if (!GrouperDdlUtils.assertTableThere(true, "grouper_sync_dep_group_user")) {
              if (GrouperDdlUtils.isPostgres()) {
                new GcDbAccess().sql("CREATE TABLE grouper_sync_dep_group_user (id_index BIGINT NOT NULL, grouper_sync_id varchar(40) NOT NULL, group_id varchar(40) NOT NULL, field_id varchar(40) NOT NULL, PRIMARY KEY (id_index) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isOracle()) {
                new GcDbAccess().sql("CREATE TABLE grouper_sync_dep_group_user ( id_index NUMBER(38) NOT NULL, grouper_sync_id VARCHAR2(40) NOT NULL, group_id VARCHAR2(40) NOT NULL, field_id VARCHAR2(40) NOT NULL, PRIMARY KEY (id_index) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isMysql()) {
                new GcDbAccess().sql("CREATE TABLE grouper_sync_dep_group_user ( id_index BIGINT NOT NULL, grouper_sync_id VARCHAR(40) NOT NULL, group_id VARCHAR(40) NOT NULL, field_id VARCHAR(40) NOT NULL, PRIMARY KEY (id_index))").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              }
              if (GrouperDdlUtils.isPostgres() || GrouperDdlUtils.isOracle()) {
                new GcDbAccess().sql("COMMENT ON TABLE grouper_sync_dep_group_user IS 'Groups are listed that are used in user translations.  Users will need to be recalced if there are changes (not membership recalc)'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_user.id_index IS 'primary key'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_user.grouper_sync_id IS 'provisioner'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_user.group_id IS 'group uuid'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_user.field_id IS 'field uuid'").executeSql();

              }
              new GcDbAccess().sql("alter table grouper_sync_dep_group_user add CONSTRAINT grouper_sync_dep_grp_user_fk_2 FOREIGN KEY (grouper_sync_id) REFERENCES grouper_sync(id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added table grouper_sync_dep_group_user");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", table grouper_sync_dep_group_user exists already");
              }
            }

            if (!GrouperDdlUtils.assertIndexExists("grouper_sync_dep_group_user", "grouper_sync_dep_grp_user_idx0")) {
              new GcDbAccess().sql("CREATE INDEX grouper_sync_dep_grp_user_idx0 ON grouper_sync_dep_group_user (grouper_sync_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_sync_dep_grp_user_idx0");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_sync_dep_grp_user_idx0 exists already");
              }
            }
            if (!GrouperDdlUtils.assertIndexExists("grouper_sync_dep_group_user", "grouper_sync_dep_grp_user_idx1")) {
              new GcDbAccess().sql("CREATE UNIQUE INDEX grouper_sync_dep_grp_user_idx1 ON grouper_sync_dep_group_user (grouper_sync_id,group_id,field_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_sync_dep_grp_user_idx1");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_sync_dep_grp_user_idx1 exists already");
              }
            }

            if (!GrouperDdlUtils.assertTableThere(true, "grouper_sync_dep_group_group")) {
              if (GrouperDdlUtils.isPostgres()) {
                new GcDbAccess().sql("CREATE TABLE grouper_sync_dep_group_group (id_index BIGINT NOT NULL, grouper_sync_id varchar(40) NOT NULL, group_id varchar(40) NOT NULL, field_id varchar(40) NOT NULL, provisionable_group_id varchar(40) NOT NULL, PRIMARY KEY (id_index) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isOracle()) {
                new GcDbAccess().sql("CREATE TABLE grouper_sync_dep_group_group ( id_index NUMBER(38) NOT NULL,  grouper_sync_id VARCHAR2(40) NOT NULL, group_id VARCHAR2(40) NOT NULL, field_id VARCHAR2(40) NOT NULL, provisionable_group_id VARCHAR2(40) NOT NULL, PRIMARY KEY (id_index) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isMysql()) {
                new GcDbAccess().sql("CREATE TABLE grouper_sync_dep_group_group ( id_index BIGINT NOT NULL, grouper_sync_id VARCHAR(40) NOT NULL, group_id VARCHAR(40) NOT NULL, field_id VARCHAR(40) NOT NULL, provisionable_group_id VARCHAR(40) NOT NULL, PRIMARY KEY (id_index) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              }
              if (GrouperDdlUtils.isPostgres() || GrouperDdlUtils.isOracle()) {
                new GcDbAccess().sql("COMMENT ON TABLE grouper_sync_dep_group_group IS 'Groups are listed that are used in group translations.  Provisionable groups will need to be recalced if there are changes (not membership recalc)'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_group.id_index IS 'primary key'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_group.grouper_sync_id IS 'provisioner'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_group.group_id IS 'group uuid'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_group.field_id IS 'field uuid'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_sync_dep_group_group.provisionable_group_id IS 'group uuid of the provisionable group that uses this other group as a role'").executeSql();
              }
                
              new GcDbAccess().sql("alter table grouper_sync_dep_group_group add CONSTRAINT grouper_sync_dep_grp_grp_fk_1 FOREIGN KEY (provisionable_group_id) REFERENCES grouper_groups(id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              
              new GcDbAccess().sql("alter table grouper_sync_dep_group_group add CONSTRAINT grouper_sync_dep_grp_grp_fk_3 FOREIGN KEY (grouper_sync_id) REFERENCES grouper_sync(id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added table grouper_sync_dep_group_group");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", table grouper_sync_dep_group_group exists already");
              }
            }

            if (!GrouperDdlUtils.assertIndexExists("grouper_sync_dep_group_group", "grouper_sync_dep_grp_grp_idx0")) {
              new GcDbAccess().sql("CREATE INDEX grouper_sync_dep_grp_grp_idx0 ON grouper_sync_dep_group_group (grouper_sync_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_sync_dep_grp_grp_idx0 exists already");
              }
            }
            if (!GrouperDdlUtils.assertIndexExists("grouper_sync_dep_group_group", "grouper_sync_dep_grp_grp_idx1")) {
              new GcDbAccess().sql("CREATE UNIQUE INDEX grouper_sync_dep_grp_grp_idx1 ON grouper_sync_dep_group_group (grouper_sync_id,group_id,field_id,provisionable_group_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_sync_dep_grp_grp_idx1 exists already");
              }
            }
            if (!GrouperDdlUtils.assertIndexExists("grouper_sync_dep_group_group", "grouper_sync_dep_grp_grp_idx2")) {
              new GcDbAccess().sql("CREATE INDEX grouper_sync_dep_grp_grp_idx2 ON grouper_sync_dep_group_group (grouper_sync_id,provisionable_group_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_sync_dep_grp_grp_idx2 exists already");
              }
            }
            if (!GrouperDdlUtils.assertIndexExists("grouper_sync_dep_group_group", "grouper_sync_dep_grp_grp_idx3")) {
              new GcDbAccess().sql("CREATE INDEX grouper_sync_dep_grp_grp_idx3 ON grouper_sync_dep_group_group (grouper_sync_id,group_id,field_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_sync_dep_grp_grp_idx3 exists already");
              }
            }
              
            
          } catch (Throwable t) {
            String message = "Could not perform upgrade task V11 on grouper_loader_log adding tables/indexes for GRP-5302 for provisioning attributes!  "
                + "Skipping this upgrade task, install the tables/foreign keys/indexes manually";
            LOG.error(message, t);
            if (otherJobInput != null) {
              otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", " + message);
            }
          }
          return null;
        }
      });
    }
  }, 
  
  V12 {
    
    @Override
    public void updateVersionFromPrevious(OtherJobInput otherJobInput) {
      
      GrouperSession.internal_callbackRootGrouperSession(new GrouperSessionHandler() {
        
        @Override
        public Object callback(GrouperSession grouperSession) throws GrouperSessionException {
          
          try {
            
            if (!GrouperDdlUtils.assertTableThere(true, "grouper_prov_scim_user")) {
              if (GrouperDdlUtils.isPostgres()) {
                new GcDbAccess().sql("CREATE TABLE grouper_prov_scim_user ( config_id VARCHAR(50) NOT NULL, active VARCHAR(1), cost_center VARCHAR(256), department VARCHAR(256), display_name VARCHAR(256), division VARCHAR(256), email_type VARCHAR(256), email_value VARCHAR(256), email_type2 VARCHAR(256), email_value2 VARCHAR(256), employee_number VARCHAR(256), external_id VARCHAR(256), family_name VARCHAR(256), formatted_name VARCHAR(256), given_name VARCHAR(256), id VARCHAR(256) NOT NULL, middle_name VARCHAR(256), phone_number VARCHAR(256), phone_number_type VARCHAR(256), phone_number2 VARCHAR(256), phone_number_type2 VARCHAR(256), the_schemas VARCHAR(256), title VARCHAR(256), user_name VARCHAR(256), user_type VARCHAR(256), PRIMARY KEY (config_id, id) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isOracle()) {
                new GcDbAccess().sql("CREATE TABLE grouper_prov_scim_user ( config_id VARCHAR2(50) NOT NULL, active VARCHAR2(1), cost_center VARCHAR2(256), department VARCHAR2(256), display_name VARCHAR2(256), division VARCHAR2(256), email_type VARCHAR2(256), email_value VARCHAR2(256), email_type2 VARCHAR2(256), email_value2 VARCHAR2(256), employee_number VARCHAR2(256), external_id VARCHAR2(256), family_name VARCHAR2(256), formatted_name VARCHAR2(256), given_name VARCHAR2(256), id VARCHAR2(256) NOT NULL, middle_name VARCHAR2(256), phone_number VARCHAR2(256), phone_number_type VARCHAR2(256), phone_number2 VARCHAR2(256), phone_number_type2 VARCHAR2(256), the_schemas VARCHAR2(256), title VARCHAR2(256), user_name VARCHAR2(256), user_type VARCHAR2(256), PRIMARY KEY (config_id, id) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isMysql()) {
                new GcDbAccess().sql("CREATE TABLE grouper_prov_scim_user ( config_id VARCHAR(50) NOT NULL, active VARCHAR(1) NULL, cost_center VARCHAR(256) NULL, department VARCHAR(256) NULL, display_name VARCHAR(256) NULL, division VARCHAR(256) NULL, email_type VARCHAR(256) NULL, email_value VARCHAR(256) NULL, email_type2 VARCHAR(256) NULL, email_value2 VARCHAR(256) NULL, employee_number VARCHAR(256) NULL, external_id VARCHAR(256) NULL, family_name VARCHAR(256) NULL, formatted_name VARCHAR(256) NULL, given_name VARCHAR(256) NULL, id VARCHAR(256) NOT NULL, middle_name VARCHAR(256) NULL, phone_number VARCHAR(256) NULL, phone_number_type VARCHAR(256) NULL, phone_number2 VARCHAR(256) NULL, phone_number_type2 VARCHAR(256) NULL, the_schemas VARCHAR(256) NULL, title VARCHAR(256) NULL, user_name VARCHAR(256) NULL, user_type VARCHAR(256) NULL, PRIMARY KEY (config_id, id) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              }
              if (GrouperDdlUtils.isPostgres() || GrouperDdlUtils.isOracle()) {
                
                new GcDbAccess().sql("COMMENT ON TABLE grouper_prov_scim_user IS 'table to load scim users into a sql for reporting, provisioning, and deprovisioning'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.config_id IS 'scim config id identifies which scim external system is being loaded'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.id IS 'scim internal ID for this user (used in web services)'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.active IS 'Is user active'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.cost_center IS 'cost center for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.department IS 'department for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.display_name IS 'display name for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.division IS 'divsion for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.email_type IS 'email type for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.email_value IS 'email value for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.email_type2 IS 'email type2 for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.email_value2 IS 'email value2 for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.employee_number IS 'employee number for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.external_id IS 'external id for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.family_name IS 'family name for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.formatted_name IS 'formatted name for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.given_name IS 'given name for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.middle_name IS 'middle name for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.phone_number IS 'phone number for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.phone_number_type IS 'phone number type for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.phone_number2 IS 'phone number2 for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.phone_number_type2 IS 'phone number type2 for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.the_schemas IS 'schemas for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.title IS 'title for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.user_name IS 'user name for the user'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user.user_type IS 'user type for the user'").executeSql();
          
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added table grouper_prov_scim_user");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", table grouper_prov_scim_user exists already");
              }
            }
            
            if (!GrouperDdlUtils.assertIndexExists("grouper_prov_scim_user", "grouper_prov_scim_user_idx1")) {
              new GcDbAccess().sql("CREATE INDEX grouper_prov_scim_user_idx1 ON grouper_prov_scim_user (email_value, config_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_prov_scim_user_idx1");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_prov_scim_user_idx1 exists already");
              }
            }
            
            if (!GrouperDdlUtils.assertIndexExists("grouper_prov_scim_user", "grouper_prov_scim_user_idx2")) {
              new GcDbAccess().sql("CREATE INDEX grouper_prov_scim_user_idx2 ON grouper_prov_scim_user (user_name, config_id)").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_prov_scim_user_idx2");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_prov_scim_user_idx2 exists already");
              }
            }
            
            
            if (!GrouperDdlUtils.assertTableThere(true, "grouper_prov_scim_user_attr")) {
              if (GrouperDdlUtils.isPostgres()) {
                new GcDbAccess().sql("CREATE TABLE grouper_prov_scim_user_attr ( config_id VARCHAR(50) NOT NULL, id VARCHAR(256) NOT NULL, attribute_name VARCHAR(256) NULL, attribute_value VARCHAR(4000) NULL, PRIMARY KEY (config_id, id, attribute_name, attribute_value) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isOracle()) {
                new GcDbAccess().sql("CREATE TABLE grouper_prov_scim_user_attr ( config_id VARCHAR2(50) NOT NULL, id VARCHAR2(256) NOT NULL, attribute_name VARCHAR2(256) NULL, attribute_value VARCHAR2(4000) NULL, PRIMARY KEY (config_id, id, attribute_name, attribute_value) )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              } else if (GrouperDdlUtils.isMysql()) {
                new GcDbAccess().sql("CREATE TABLE grouper_prov_scim_user_attr ( config_id VARCHAR(50) NOT NULL, id VARCHAR(256) NOT NULL, attribute_name VARCHAR(256) NULL, attribute_value VARCHAR(4000) NULL )").executeSql();
                if (otherJobInput != null) {
                  otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
                }
              }
              
              new GcDbAccess().sql("ALTER TABLE grouper_prov_scim_user_attr ADD CONSTRAINT grouper_prov_scim_usat_fk FOREIGN KEY (config_id, id) REFERENCES grouper_prov_scim_user(config_id, id) on delete cascade").executeSql();
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              
              if (GrouperDdlUtils.isPostgres() || GrouperDdlUtils.isOracle()) {
                
                new GcDbAccess().sql("COMMENT ON TABLE grouper_prov_scim_user_attr IS 'table to load scim user attributes into a sql for reporting, provisioning, and deprovisioning'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user_attr.config_id IS 'scim config id identifies which scim external system is being loaded'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user_attr.id IS 'scim internal ID for this user (used in web services)'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user_attr.attribute_name IS 'scim user attribute name'").executeSql();
                new GcDbAccess().sql("COMMENT ON COLUMN grouper_prov_scim_user_attr.attribute_value IS 'scim user attribute value'").executeSql();
          
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added table grouper_prov_scim_user_attr");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", table grouper_prov_scim_user_attr exists already");
              }
            }
            
            if (!GrouperDdlUtils.assertIndexExists("grouper_prov_scim_user_attr", "grouper_prov_scim_usat_idx1")) {
              if (GrouperDdlUtils.isMysql()) {
                new GcDbAccess().sql("CREATE INDEX grouper_prov_scim_usat_idx1 ON grouper_prov_scim_user_attr (id(100), config_id, attribute_name(100))").executeSql();
              } else {
                new GcDbAccess().sql("CREATE INDEX grouper_prov_scim_usat_idx1 ON grouper_prov_scim_user_attr (id, config_id, attribute_name)").executeSql();
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_prov_scim_user_idx1");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_prov_scim_user_idx1 exists already");
              }
            }
            
            if (!GrouperDdlUtils.assertIndexExists("grouper_prov_scim_user_attr", "grouper_prov_scim_usat_idx2")) {
              if (GrouperDdlUtils.isMysql()) {
                new GcDbAccess().sql("CREATE INDEX grouper_prov_scim_usat_idx2 ON grouper_prov_scim_user_attr (id(100), config_id, attribute_value(100))").executeSql();
              } else {
                new GcDbAccess().sql("CREATE INDEX grouper_prov_scim_usat_idx2 ON grouper_prov_scim_user_attr (id, config_id, attribute_value)").executeSql();
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().addInsertCount(1);
              }
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", added index grouper_prov_scim_usat_idx2");
              }
            } else {
              if (otherJobInput != null) {
                otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", index grouper_prov_scim_usat_idx2 exists already");
              }
            }              
            
          } catch (Throwable t) {
            String message = "Could not perform upgrade task V12 adding tables/foreign keys/indexes for GRP-5514 scim loading!  "
                + "Skipping this upgrade task, install the tables/foreign keys/indexes manually";
            LOG.error(message, t);
            if (otherJobInput != null) {
              otherJobInput.getHib3GrouperLoaderLog().appendJobMessage(", " + message);
            }
          }
          return null;
        }
      });
    }
  };
  
  /** logger */
  private static final Log LOG = GrouperUtil.getLog(UpgradeTasks.class);

  private static int currentVersion = -1;
  
  /**
   * keep the current version here, increment as things change
   * @return the current version
   */
  public static int currentVersion() {
    if (currentVersion == -1) {
      int max = -1;
      for (UpgradeTasks task : UpgradeTasks.values()) {
        String number = task.name().substring(1);
        int theInt = Integer.parseInt(number);
        max = Math.max(max, theInt);
      }
      currentVersion = max;
    }
    return currentVersion;
  }

  public static final Set<String> v8_entityResolverSuffixesToRefactor = GrouperUtil.toSet("entityAttributesNotInSubjectSource",
      "resolveAttributesWithSQL",
      "useGlobalSQLResolver",
      "globalSQLResolver",
      "sqlConfigId",
      "tableOrViewName",
      "columnNames",
      "subjectSourceIdColumn",
      "subjectSearchMatchingColumn",
      "sqlMappingType",
      "sqlMappingEntityAttribute",
      "sqlMappingExpression",
      "lastUpdatedColumn",
      "lastUpdatedType",
      "selectAllSQLOnFull",
      "resolveAttributesWithLDAP",
      "useGlobalLDAPResolver",
      "globalLDAPResolver",
      "ldapConfigId",
      "baseDN",
      "subjectSourceId",
      "searchScope",
      "filterPart",
      "attributes",
      "multiValuedLdapAttributes",
      "ldapMatchingSearchAttribute",
      "ldapMappingType",
      "ldapMappingEntityAttribute",
      "ldapMatchingExpression",
      "filterAllLDAPOnFull",
      "lastUpdatedAttribute",
      "lastUpdatedFormat" );

}
