<%-- @annotation@
		Tile which displays a form which allows a user to change whether only immediate, or only effective 
		or all members of the active group should be displayed. If a group has custom list attributes the user can
		select the 'Default' memnership list or a custom list. 
		
		If configured, an dthe user has appropriate privileges, it is possible to import/export members from/to flat files
--%><%--
  @author Gary Brown.
  @version $Id: changeMembershipScope.jsp,v 1.6 2007-09-27 14:07:16 isgwb Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<grouper:recordTile key="Not dynamic" tile="${requestScope['javax.servlet.include.servlet_path']}">
<h2 class="actionheader">
	<fmt:message bundle="${nav}" key="groups.heading.change-members-list-scope"/>
</h2>
<div class="changeScope">
<html:form  action="/populateGroupMembers" enctype="multipart/form-data">
<html:hidden property="groupId"/>
<html:hidden property="contextSubject"/>
<html:hidden property="contextSubjectId"/>
<html:hidden property="contextSubjectType"/>
<!--html:hidden property="callerPageId"/-->
<input type="hidden" name="callerPageId" value="<c:out value="${thisPageId}"/>"/>
<fieldset>
	<span class="membershipListScope">
		<html:radio property="membershipListScope" value="imm"/> <fmt:message bundle="${nav}" key="groups.list-members.scope.imm"/>
	</span>
	<span class="membershipListScope">
		<html:radio property="membershipListScope" value="eff"/> <fmt:message bundle="${nav}" key="groups.list-members.scope.eff"/>
	</span>
	<span class="membershipListScope">
		<html:radio property="membershipListScope" value="all"/> <fmt:message bundle="${nav}" key="groups.list-members.scope.all"/>
	</span>
	<c:if test="${listFieldsSize gt 0}">
		<span class="membershipListScope">
		
			<html:select property="listField">
				<option value=""><fmt:message bundle="${nav}" key="groups.list-members.scope.ordinary-membership"/></option>
				<html:options name="listFields"/>
			</html:select> <fmt:message bundle="${nav}" key="groups.list-members.scope.select-list"/>
		</span>
	</c:if>
	<c:if test="${mediaMap['members.filter.by-source']=='true' && sourcesSize > 1}">
	<fmt:message bundle="${nav}" key="groups.list-members.filter-by-source"/> <html:select property="selectedSource">
	<html:option value="_void_"><fmt:message bundle="${nav}" key="groups.list-members.any-source"/></html:option>
	<html:options collection="sources" property="key" labelProperty="value"/>
	</html:select>
	</c:if>
	<span class="membershipListScope">
		<input name="submit.changeScope" type="submit" value="<fmt:message bundle="${nav}" key="groups.list-members.scope.submit"/>"/>
	</span>
	<c:if test="${MembershipExporter.active}">
	<h2 class="actionheader">
	<fmt:message bundle="${nav}" key="groups.heading.export-members"/>
	</h2>
	
		<c:choose>
			<c:when test="${MembershipExporter.numberOfAvailableFormats ==1}">
				<input type="hidden" name="exportFormat" value="<c:out value="${MembershipExporter.availableFormats[0]}"/>"/>
			</c:when>
			<c:otherwise>
			<select name="exportFormat" title="<c:out value="${navMap['groups.export.select-format-title']}"/>">
					<c:forEach var="format" items="${MembershipExporter.availableFormats}">
						<option<c:if test="${format==exportFormat}"> selected="selected"</c:if>><c:out value="${format}"/></option>
					</c:forEach>
			</select>
			</c:otherwise>
		</c:choose>
		<input type="submit" name="submit.export" value="<c:out value="${navMap['groups.export.submit']}"/>"/>
	</c:if>
	
<html:hidden property="groupId"/>
	<c:if test="${MembershipImportManager.active}">
	<h2 class="actionheader">
	<fmt:message bundle="${nav}" key="groups.heading.import-members"/>
	</h2>
	<html:file property="importData" title="${navMap['groups.import.select-file-title']}"/>
		<c:choose>
			<c:when test="${MembershipImportmanager.numberOfAvailableFormats ==1}">
				<input type="hidden" name="importFormat" value="<c:out value="${MembershipImportmanager.availableFormats[0]}"/>"/>
			</c:when>
			<c:otherwise>
			<select name="importFormat" title="<c:out value=
			"${navMap['groups.import.select-format-title']}"/>">
					<c:forEach var="format" items="${MembershipImportManager.availableFormats}">
						<option<c:if test="${format==importFormat}"> selected="selected"</c:if>><c:out value="${format}"/></option>
					</c:forEach>
			</select>
			</c:otherwise>
		</c:choose>
		<input type="submit" name="submit.import" value="<c:out value="${navMap['groups.import.submit']}"/>"/>
		
	</c:if>
	</fieldset>
</html:form>
</div>
</grouper:recordTile>