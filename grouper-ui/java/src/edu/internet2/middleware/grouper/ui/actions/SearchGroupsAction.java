/*
Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
Copyright 2004-2006 The University Of Bristol

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.internet2.middleware.grouper.ui.actions;

import java.io.FilterReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import edu.internet2.middleware.grouper.GrouperHelper;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.ui.Message;
import edu.internet2.middleware.grouper.ui.RepositoryBrowser;
import edu.internet2.middleware.grouper.ui.util.CollectionPager;

/**
 * Top level Strut's action which searches groups for current browseMode. 
 * <p/>
<table width="75%" border="1">
  <tr bgcolor="#CCCCCC"> 
    <td width="51%"><strong><font face="Arial, Helvetica, sans-serif">Request 
      Parameter</font></strong></td>
    <td width="12%"><strong><font face="Arial, Helvetica, sans-serif">Direction</font></strong></td>
    <td width="37%"><strong><font face="Arial, Helvetica, sans-serif">Description</font></strong></td>
  </tr>
  <tr> 
    <td><p><font face="Arial, Helvetica, sans-serif">start</font></p></td>
    <td><font face="Arial, Helvetica, sans-serif">IN</font></td>
    <td><font face="Arial, Helvetica, sans-serif">Used by CollectionPager</font></td>
  </tr>
  <tr> 
    <td><p><font face="Arial, Helvetica, sans-serif">searchTerm</font></p></td>
    <td><font face="Arial, Helvetica, sans-serif">IN</font></td>
    <td><font face="Arial, Helvetica, sans-serif">The actual query (some times 
      -if not advanced)</font></td>
  </tr>
  <tr> 
    <td><p><font face="Arial, Helvetica, sans-serif">searchFrom</font></p></td>
    <td><font face="Arial, Helvetica, sans-serif">IN</font></td>
    <td><font face="Arial, Helvetica, sans-serif">identifies stem which scopes 
      search results</font></td>
  </tr>
  <tr> 
    <td><p><font face="Arial, Helvetica, sans-serif">groupSearchResultField</font></p></td>
    <td><font face="Arial, Helvetica, sans-serif">IN</font></td>
    <td><font face="Arial, Helvetica, sans-serif">The group field to display on 
      results page</font></td>
  </tr>
  <tr> 
    <td><p><font face="Arial, Helvetica, sans-serif">searchInNameOrExtension=name 
        or extension</font></p></td>
    <td><font face="Arial, Helvetica, sans-serif">IN</font></td>
    <td><font face="Arial, Helvetica, sans-serif">Identifies which attribute to 
      search </font></td>
  </tr>
  <tr> 
    <td><p><font face="Arial, Helvetica, sans-serif">searchInDisplayNameOrExtension=name 
        or extension</font></p></td>
    <td><font face="Arial, Helvetica, sans-serif">IN</font></td>
    <td><font face="Arial, Helvetica, sans-serif">Identifies which attribute to 
      search </font></td>
  </tr>
  <tr bgcolor="#CCCCCC"> 
    <td><strong><font face="Arial, Helvetica, sans-serif">Request Attribute</font></strong></td>
    <td><strong><font face="Arial, Helvetica, sans-serif">Direction</font></strong></td>
    <td><strong><font face="Arial, Helvetica, sans-serif">Description</font></strong></td>
  </tr>
  <tr bgcolor="#FFFFFF"> 
    <td><font face="Arial, Helvetica, sans-serif">pager</font></td>
    <td><font face="Arial, Helvetica, sans-serif">OUT</font></td>
    <td><font face="Arial, Helvetica, sans-serif">CollectionPager</font></td>
  </tr>
  <tr bgcolor="#FFFFFF"> 
    <td><font face="Arial, Helvetica, sans-serif">queryOutTerms</font></td>
    <td><font face="Arial, Helvetica, sans-serif">OUT</font></td>
    <td><font face="Arial, Helvetica, sans-serif">List of (query,field,and / or 
      / not) used to display what was searched for</font></td>
  </tr>
  <tr bgcolor="#CCCCCC"> 
    <td><strong><font face="Arial, Helvetica, sans-serif">Session Attribute</font></strong></td>
    <td><strong><font face="Arial, Helvetica, sans-serif">Direction</font></strong></td>
    <td><strong><font face="Arial, Helvetica, sans-serif">Description</font></strong></td>
  </tr>
  <tr bgcolor="#FFFFFF"> 
    <td><font face="Arial, Helvetica, sans-serif">subtitle=groups.action.search</font></td>
    <td><font face="Arial, Helvetica, sans-serif">OUT</font></td>
    <td><font face="Arial, Helvetica, sans-serif">Key resolved in nav ResourceBundle</font></td>
  </tr>
  <tr bgcolor="#FFFFFF"> 
    <td><font face="Arial, Helvetica, sans-serif">groupSearchResultField</font></td>
    <td><font face="Arial, Helvetica, sans-serif">OUT</font></td>
    <td><font face="Arial, Helvetica, sans-serif">Maintain user selection</font></td>
  </tr>
  <tr bgcolor="#CCCCCC"> 
    <td><strong><font face="Arial, Helvetica, sans-serif">Strut's Action Parameter</font></strong></td>
    <td><strong><font face="Arial, Helvetica, sans-serif">Direction</font></strong></td>
    <td><strong><font face="Arial, Helvetica, sans-serif">Description</font></strong></td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
</table> 
 * @author Gary Brown.
 * @version $Id: SearchGroupsAction.java,v 1.5 2006-07-14 11:04:11 isgwb Exp $
 */

public class SearchGroupsAction extends GrouperCapableAction {

	//------------------------------------------------------------ Local
	// Forwards

	static final private String FORWARD_Result = "Results";

	//------------------------------------------------------------ Action
	// Methods

	public ActionForward grouperExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, GrouperSession grouperSession)
			throws Exception {
		
		session.setAttribute("subtitle", "groups.action.search");
		
		String browseMode = getBrowseMode(session);
		DynaActionForm searchForm = (DynaActionForm) form;
		//Determine what we are searching for
		String query = (String) searchForm.get("searchTerm");
		String searchFrom = (String) searchForm.get("searchFrom");
		String searchInNameOrExtension = (String) searchForm.get("searchInNameOrExtension");
		String searchInDisplayNameOrExtension = (String) searchForm.get("searchInDisplayNameOrExtension");
		String groupSearchResultField = (String) searchForm.get("groupSearchResultField");
		if(!isEmpty(groupSearchResultField)) {
			session.setAttribute("groupSearchResultField",groupSearchResultField);
		}
		
		//Take account of paging
		String startStr = request.getParameter("start");
		if (startStr == null || "".equals(startStr))
			startStr = "0";

		int start = Integer.parseInt(startStr);
		int pageSize = getPageSize(session);
		
		//Do the search
		RepositoryBrowser repositoryBrowser = getRepositoryBrowser(grouperSession,session);
		Map attr = new HashMap();
		attr.put("searchInDisplayNameOrExtension",searchInDisplayNameOrExtension);
		attr.put("searchInNameOrExtension",searchInNameOrExtension);
		List outTerms = new ArrayList();
		List groupRes=null;
		try {
			groupRes = repositoryBrowser.search(grouperSession, query,
				searchFrom, request.getParameterMap(),outTerms);
		}catch(IllegalArgumentException e) {
			request.setAttribute("message",new Message("find.results.empty-search",true));
			return new ActionForward("/populate" + getBrowseMode(session) + "Groups.do");
		}
		int end = start + pageSize;
		if (end > groupRes.size())
			end = groupRes.size();
		List groupResMaps = GrouperHelper.groups2Maps(grouperSession, groupRes
				.subList(start, end));
		//Set up the CollectionPager for the view
		CollectionPager pager = new CollectionPager(groupResMaps, groupRes
				.size(), null, start, null, pageSize);

		
		pager.setParam("searchTerm", query);
		pager.setParam("searchFrom", searchFrom);
		pager.setParam("searchInNameOrExtension", searchInNameOrExtension);
		
		Map searchFieldParams = filterParameters(request,"searchField.");
		if(!searchFieldParams.isEmpty()){
			pager.setParams(searchFieldParams);
			session.setAttribute("advancedSearchFieldParams",searchFieldParams);
			pager.setParam("advSearch", "Y");
			pager.setParam("callerPageId", searchForm.get("callerPageId"));
			pager.setParam("maxFields", searchForm.get("maxFields"));
		}
		pager.setParam("searchInDisplayNameOrExtension", searchInDisplayNameOrExtension);
		pager.setTarget(mapping.getPath());
		request.setAttribute("pager", pager);
		request.setAttribute("queryOutTerms",outTerms);
		
		if (!isEmpty(searchFrom)) {
			Stem fromStem = StemFinder.findByName(grouperSession,
					searchFrom);
			pager.setParam("searchFromDisplay", fromStem.getDisplayExtension());
		}
		
		return mapping.findForward(FORWARD_Result);
	}
}