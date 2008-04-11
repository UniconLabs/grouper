<%-- @annotation@
		Tile which displays the generic subject search functionality. Designed to be embedded in actual forms
--%>
<%--
  @author Gary Brown.
  @version $Id: subjectSearchFragment.jsp,v 1.7 2008-04-11 05:53:47 mchyzer Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:importAttribute />
<grouper:recordTile key="Not dynamic"
  tile="${requestScope['javax.servlet.include.servlet_path']}"
>
  <%-- if something in advanced is entered, then show advanced panel --%>
  <c:set var="advancedMode" value="false" />
  <c:if test="${ ! empty subjectSource && subjectSource != 'all' }">
    <c:set var="advancedMode" value="true" />
   
  </c:if>
  <c:if test="${'all' == subjectSource}">
    <c:set var="checked"> checked="checked"</c:set>
  </c:if>
  <tr class="formTableRow" 
    <grouper:hideShowTarget hideShowHtmlId="advancedSubjectSearch" showInitially="${advancedMode}"/> >
    <td class="formTableLeft"><grouper:message bundle="${nav}"
      key="find.search-source"
    /></td>
    <td class="formTableRight"><input type="radio" value="all"
      <c:out value="${checked}"/> name="subjectSource" id="allRadio"
    />
    <label for="<c:out value="allRadio"/>"><grouper:message
      bundle="${nav}" key="find.search-all-sources"
    /></label>
    </td>

    </tr>
    <c:remove var="checked" />
  
    <c:forEach var="source" items="${subjectSources}" varStatus="sourceStatus">

      <c:if test="${source.id == subjectSource}">
        <c:set var="checked"> checked="checked"</c:set>
      </c:if>
      <tr class="formTableRow"
      <grouper:hideShowTarget hideShowHtmlId="advancedSubjectSearch" showInitially="${advancedMode}"/> >
        <td class="formTableLeft">&nbsp;</td>
        <td class="formTableRight"><input type="radio"
          value="<c:out value="${source.id}"/>" <c:out value="${checked}"/>
          name="subjectSource" id="<c:out value="${source.id}Radio"/>"
        />
        <label for="<c:out value="${source.id}Radio"/>"><c:out
          value="${source.name}"
        /> ( <c:forEach var="subjectType" items="${source.subjectTypes}"
          varStatus="typeStatus"
        >
          <c:if test="${typeStatus.count>1}">, </c:if>
          <c:out value="${subjectType}" />
        </c:forEach> )</label>
        </td>
        </tr>
        <c:set var="insertFragmentKey" value="subject.search.form-fragment.${source.id}" />
        <c:set var="insertFragment" value="${mediaMap[insertFragmentKey]}" /> <%
 if (!pageContext.getAttribute("insertFragment").toString().matches("^\\?\\?.*")) {
 %> <tr <grouper:hideShowTarget hideShowHtmlId="advancedSubjectSearch" showInitially="${advancedMode}"/> ><td class="formTableLeft">&nbsp;</td><td><table class="formSubtable"><tiles:insert definition="${insertFragment}" /></table></td></tr> <%
 }
 %> <c:remove var="checked" />
    </c:forEach>
</grouper:recordTile>