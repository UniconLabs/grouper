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

import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.*;
import com.unboundid.scim2.common.utils.SchemaUtils;
import com.unboundid.scim2.server.annotations.ResourceType;
import edu.internet2.middleware.grouper.scim.TierScimUtil;
import edu.internet2.middleware.grouper.scim.resources.TierGroupResource;
import edu.internet2.middleware.grouper.scim.resources.TierMetaResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;

@ResourceType(
        description = "SCIM 2.0 ResourceTypes",
        name = "ResourceTypes",
        schema = ResourceTypeResource.class,
        discoverable = false)
@Path("ResourceTypes")
public class ResourceTypesProvider {
  @GET
//  @ApiOperation(
//          value = "...",
//          notes = "...")
//  @ApiResponses(value = {
//          @ApiResponse(code = 200, message = "..."),
//          @ApiResponse(code = 404, message = "...")})
//  @Path("/{id}")
  @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
  public Response get() {


    try {
      List<ResourceTypeResource> resourceTypes = new ArrayList<>();

      /* users with TierMeta extension */
      List<ResourceTypeResource.SchemaExtension> userExtensions = new ArrayList<>();
      for (String ext: new TierMetaResource().getSchemaUrns()) {
        userExtensions.add(new ResourceTypeResource.SchemaExtension(new URI(ext), false));
      }
      SchemaResource userSchema = SchemaUtils.getSchema(UserResource.class);
      ResourceTypeResource userResource = new ResourceTypeResource("User", "User Account",
              "Top level SCIM User",
              new URI("/Users"),
              new URI(userSchema.getId()),
              userExtensions);

      resourceTypes.add(userResource);

      /* Groups with TierMeta and TierGroup extensions */
      List<ResourceTypeResource.SchemaExtension> groupExtensions = new ArrayList<>();
      for (String ext: new TierMetaResource().getSchemaUrns()) {
        groupExtensions.add(new ResourceTypeResource.SchemaExtension(new URI(ext), false));
      }
      for (String ext: new TierGroupResource().getSchemaUrns()) {
        groupExtensions.add(new ResourceTypeResource.SchemaExtension(new URI(ext), false));
      }
      SchemaResource groupSchema = SchemaUtils.getSchema(GroupResource.class);
      ResourceTypeResource groupResource = new ResourceTypeResource("Group", "Group",
              "Top level SCIM Group",
              new URI("/Groups"),
              new URI(groupSchema.getId()),
              groupExtensions);

      resourceTypes.add(groupResource);

      return Response
              .status(Response.Status.OK)
              /* ListResponse not in spec, should just be a json array */
              //.entity(new ListResponse(resourceTypes.size(), resourceTypes, 1, resourceTypes.size()))
              .entity(resourceTypes)
              .build();

    } catch (Exception e) {
      return TierScimUtil.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, "ResourceTypesProvider.get", e.getMessage());
    }
  }

}
