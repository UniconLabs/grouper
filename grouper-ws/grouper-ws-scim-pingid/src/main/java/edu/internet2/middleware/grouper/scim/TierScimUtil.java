/****
 * Copyright 2022 Internet2
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
 ***/

package edu.internet2.middleware.grouper.scim;

import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Meta;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.scim.resources.TierGroupResource;
import edu.internet2.middleware.grouper.scim.resources.TierMetaResource;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.core.Response;

public class TierScimUtil {

  /**
   *
   * @param status HTTP response code to return
   * @param scimType keyword for error message
   * @param detail full error text
   * @return
   */
  public static Response errorResponse(Response.Status status, String scimType, String detail) {
    ErrorResponse errorResponse = new ErrorResponse(status.getStatusCode());
    errorResponse.setScimType(scimType);
    errorResponse.setDetail(detail);
    return Response.status(status)
            .entity(errorResponse).build();
  }

  public static GroupResource convertGrouperGroupToScimGroup(Group group) {
    GroupResource groupResource = new GroupResource();
    groupResource.setId(group.getId());
    groupResource.setDisplayName(group.getName());
    groupResource.setExternalId(group.getName());

    TierMetaResource extensionMeta = new TierMetaResource()
            .setResultCode("SUCCESS")
            .setResponseDurationMillis(System.currentTimeMillis() - TierFilter.retrieveRequestStartMillis());

    TierGroupResource extensionGroup = new TierGroupResource()
            .setSystemName(group.getName())
            .setDescription(group.getDescription())
            .setIdIndex(group.getIdIndex());

    groupResource.setExtension(extensionMeta);
    groupResource.setExtension(extensionGroup);

    String groupAsString = group.getId() + "\0" +
            group.getName() + "\0" +
            group.getIdIndex();

    String version = DigestUtils.sha1Hex(groupAsString);
    Meta meta = new Meta();
    meta.setVersion(version);
    meta.setResourceType("Group");

    // Bug in Meta (or json lib?) serializes calendars as numbers; spec requires DateTime (e.g. 2008-01-23T04:56:22Z), so best leave it out
//    Calendar createTime = Calendar.getInstance();
//    createTime.setTime(foundGroup.getCreateTime());
//    meta.setCreated(createTime);
//
//    Calendar modifyTime = Calendar.getInstance();
//    modifyTime.setTime(foundGroup.getModifyTime());
//    meta.setLastModified(modifyTime);

    groupResource.setMeta(meta);

    return groupResource;
  }
}
