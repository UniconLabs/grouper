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

import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.server.annotations.ResourceType;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupSave;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.misc.SaveMode;
import edu.internet2.middleware.grouper.scim.TierFilter;
import edu.internet2.middleware.grouper.scim.resources.TierGroupResource;
import edu.internet2.middleware.grouper.scim.resources.TierMetaResource;
import edu.internet2.middleware.grouper.scim.TierScimUtil;
import edu.internet2.middleware.subject.Subject;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_FILTER;

@ResourceType(
        description = "SCIM 2.0 Group",
        name = "Group",
        schema = TierGroupResource.class,
        discoverable = false)
@Path("Groups")
public class TierGroupProvider {

  @Context
  private Application application;

  @GET
//  @ApiOperation(
//          value = "Return the group with the given id",
//          notes = "Returns HTTP 200 if the group is found.")
//  @ApiResponses(value = {
//          @ApiResponse(code = 200, message = "Valid group is found"),
//          @ApiResponse(code = 404, message = "Valid group is not found")})
  @Path("/{id}")
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public Response get(
          @QueryParam(QUERY_PARAMETER_FILTER) final String filterString,
          @PathParam("id") String id,
          @Context final UriInfo uriInfo)
  {
    if (filterString != null) {
      return TierScimUtil.errorResponse(Response.Status.BAD_REQUEST, "Groups.get", "Filtering not allowed");
    }

    GrouperSession grouperSession = null;

    try {
      final Subject loggedInSubject = TierFilter.retrieveSubjectFromRemoteUser();

      grouperSession = GrouperSession.start(loggedInSubject);

      Group foundGroup;

      // avoid the GroupFinder builder, because currently no method to add idIndex
      if (id.startsWith("systemName:")) {
        foundGroup = GroupFinder.findByName(id.substring(11), false);
      } else if (id.startsWith("idIndex:")) {
        foundGroup = GroupFinder.findByIdIndexSecure(Long.valueOf(id.substring(8)), false, null);
      } else {
        foundGroup = GroupFinder.findByName(id, false);
      }

      if (foundGroup == null) {
        //throw new ResourceNotFoundException("Group not found");
        return TierScimUtil.errorResponse(Response.Status.NOT_FOUND, "Groups.get", "Group " + id + " not found");
      }

      GroupResource groupResource = TierScimUtil.convertGrouperGroupToScimGroup(foundGroup);

      return Response
              .status(Response.Status.OK)
              .entity(groupResource)
              .build();
    } catch (Exception e) {
      return TierScimUtil.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Groups.get", e.getMessage());
    } finally {
      GrouperSession.stopQuietly(grouperSession);
    }
  }

  @POST
//  @ApiOperation(
//          value = "Return the group with the given id",
//          notes = "Returns HTTP 200 if the group is found.")
//  @ApiResponses(value = {
//          @ApiResponse(code = 200, message = "Valid group is found"),
//          @ApiResponse(code = 404, message = "Valid group is not found")})
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public Response create(
          GroupResource groupResource,
          @Context final UriInfo uriInfo) {

    GrouperSession grouperSession = null;

    try {
      TierGroupResource tierGroupExtension = groupResource.getExtension(TierGroupResource.class);
      String groupName = tierGroupExtension != null && tierGroupExtension.getSystemName() != null ? tierGroupExtension.getSystemName() : groupResource.getDisplayName();

      if (groupName == null || !groupName.contains(":")) {
        throw new IllegalArgumentException("name must contain at least one colon (:)");
      }

      final Subject loggedInSubject = TierFilter.retrieveSubjectFromRemoteUser();

      grouperSession = GrouperSession.start(loggedInSubject);

      GroupSave groupSave = new GroupSave(grouperSession)
              .assignName(groupName)
              .assignDisplayName(groupResource.getDisplayName())
              .assignCreateParentStemsIfNotExist(true)
              .assignSaveMode(SaveMode.INSERT);
      if (groupResource.getExtension(TierGroupResource.class) != null) {
        TierGroupResource extension = groupResource.getExtension(TierGroupResource.class);
        groupSave.assignDescription(extension.getDescription());
        groupSave.assignIdIndex(extension.getIdIndex());
      }

      Group grouperGroup = groupSave.save();
      GroupResource createdGroupResource = TierScimUtil.convertGrouperGroupToScimGroup(grouperGroup);

      return Response
              .status(Response.Status.OK)
              .entity(createdGroupResource)
              .build();

    } catch (Exception e) {
      return TierScimUtil.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Groups.create", e.getMessage());
    } finally {
      GrouperSession.stopQuietly(grouperSession);
    }
  }
}
