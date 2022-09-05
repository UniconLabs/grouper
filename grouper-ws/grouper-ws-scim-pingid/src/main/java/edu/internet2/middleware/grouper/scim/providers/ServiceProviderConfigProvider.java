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

import com.unboundid.scim2.common.types.*;
import com.unboundid.scim2.server.annotations.ResourceType;
import edu.internet2.middleware.grouper.scim.resources.TierGroupResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.Collections;

import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import static com.unboundid.scim2.common.utils.ApiConstants.QUERY_PARAMETER_FILTER;

@ResourceType(
        description = "SCIM 2.0 ServiceProviderConfig",
        name = "ServiceProviderConfig",
        schema = ServiceProviderConfigResource.class,
        discoverable = false)
@Path("ServiceProviderConfig")
public class ServiceProviderConfigProvider {
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
  public ServiceProviderConfigResource get() {
    return new ServiceProviderConfigResource("https://doc",
            new PatchConfig(true),
            new BulkConfig(true, 100, 1000),
            new FilterConfig(true, 200),
            new ChangePasswordConfig(false),
            new SortConfig(true),
            new ETagConfig(false),
            Collections.singletonList(
                    new AuthenticationScheme(
                            "Basic", "HTTP BASIC", null, null, "httpbasic", true)));
  }

}
