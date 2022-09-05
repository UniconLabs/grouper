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

package edu.internet2.middleware.grouper.scim.providers;

import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.server.annotations.ResourceType;
import edu.internet2.middleware.grouper.*;
import edu.internet2.middleware.grouper.scim.TierFilter;
import edu.internet2.middleware.grouper.scim.TierScimUtil;
import edu.internet2.middleware.grouper.scim.resources.TierGroupResource;
import edu.internet2.middleware.grouper.scim.resources.TierMetaResource;
import edu.internet2.middleware.subject.Subject;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_FILTER;

@ResourceType(
        description = "SCIM 2.0 User",
        name = "User",
        schema = TierGroupResource.class,
        discoverable = false)
@Path("Users")
public class TierUserProvider {

  @Context
  private Application application;

  @GET
//  @ApiOperation(
//          value = "Return the user with the given id",
//          notes = "Returns HTTP 200 if the user is found.")
//  @ApiResponses(value = {
//          @ApiResponse(code = 200, message = "Valid user is found"),
//          @ApiResponse(code = 404, message = "Valid user is not found")})
  @Path("/{id}")
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public Response search(
          @QueryParam(QUERY_PARAMETER_FILTER) final String filterString,
          @PathParam("id") String id,
          @Context final UriInfo uriInfo)
  {
    if (filterString != null) {
      return TierScimUtil.errorResponse(Response.Status.BAD_REQUEST, "Users.get", "Filtering not allowed");
    }

    GrouperSession grouperSession = null;

    try {
      final Subject loggedInSubject = TierFilter.retrieveSubjectFromRemoteUser();

      grouperSession = GrouperSession.start(loggedInSubject);

      Member foundMember = MemberFinder.findByUuid(grouperSession, id, false);

      if (foundMember == null) {
        return TierScimUtil.errorResponse(Response.Status.NOT_FOUND, "Users.get", "User " + id + " not found");
      }

      UserResource userResource = new UserResource();
      userResource.setId(foundMember.getUuid());
      userResource.setExternalId(foundMember.getSubjectId());
      userResource.setUserName(foundMember.getSubjectIdentifier0()); // todo should the identifier to use be configurable?
      userResource.setUserType(foundMember.getSubjectSourceId());
      userResource.setDisplayName(foundMember.getName());

      TierMetaResource extensionMeta = new TierMetaResource()
              .setResultCode("SUCCESS")
              .setResponseDurationMillis(System.currentTimeMillis() - TierFilter.retrieveRequestStartMillis());

      userResource.setExtension(extensionMeta);

      EnterpriseUserExtension entUserExtension = new EnterpriseUserExtension();
      entUserExtension.setEmployeeNumber(foundMember.getSubjectId());
      userResource.setExtension(entUserExtension);

      String subjectAsString = new StringBuilder()
              .append(foundMember.getUuid()).append("\0")
              .append(foundMember.getSubjectSourceId()).append("\0")
              .append(foundMember.getSubjectId()).append("\0")
              .append(foundMember.getSubjectIdentifier0()).toString();

      String version = DigestUtils.sha1Hex(subjectAsString);
      Meta meta = new Meta();
      meta.setVersion(version);
      meta.setResourceType("User");

      // Bug in Meta (or json lib?) serializes calendars as numbers; spec requires DateTime (e.g. 2008-01-23T04:56:22Z), so best leave it out
//    Calendar createTime = Calendar.getInstance();
//    createTime.setTime(foundGroup.getCreateTime());
//    meta.setCreated(createTime);
//
//    Calendar modifyTime = Calendar.getInstance();
//    modifyTime.setTime(foundGroup.getModifyTime());
//    meta.setLastModified(modifyTime);

      userResource.setMeta(meta);

      return Response
              .status(Response.Status.OK)
              .entity(userResource)
              .build();
    } catch (Exception e) {
      return TierScimUtil.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Users.get", e.getMessage());
    } finally {
      GrouperSession.stopQuietly(grouperSession);
    }
  }
}
