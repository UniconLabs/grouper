<%@ include file="../assetsJsp/commonTaglib.jsp"%>

<%-- for the new group or new stem button --%>
<input type="hidden" name="objectStemId" value="${grouperRequestContainer.stemContainer.guiStem.stem.id}" />

<%@ include file="../stem/stemHeader.jsp" %>

<div class="row-fluid">
  <div class="span12 tab-interface">
    <ul class="nav nav-tabs">
      <li><a role="tab" href="#" onclick="return guiV2link('operation=UiV2Stem.viewStem&stemId=${grouperRequestContainer.stemContainer.guiStem.stem.id}', {dontScrollTop: true});" >${textContainer.text['stemContents'] }</a></li>
      <c:if test="${grouperRequestContainer.stemContainer.canAdminPrivileges}">
        <li><a role="tab" href="#" onclick="return guiV2link('operation=UiV2Stem.stemPrivileges&stemId=${grouperRequestContainer.stemContainer.guiStem.stem.id}', {dontScrollTop: true});" >${textContainer.text['stemPrivileges'] }</a></li>
      </c:if>
      <c:if test="${grouperRequestContainer.stemContainer.canReadPrivilegeInheritance}">
        <%@ include file="../stem/stemMoreTab.jsp" %>
      </c:if>
    </ul>
    <div class="row-fluid">
      <div class="lead span9">${textContainer.text['groupReportOnFolderDescription'] }</div>
      <div class="span3" id="grouperReportFolderMoreActionsButtonContentsDivId">
        <%@ include file="grouperReportFolderReportViewButtonContents.jsp"%>
      </div>
    </div>
    <div class="row-fluid">
      <div class="span9"> <p>${textContainer.text['grouperReportDescription'] }</p></div>
    </div>
    <c:set var="configInstance" value="${grouperRequestContainer.grouperReportContainer.grouperReportConfigInstance}" />
    <table class="table table-condensed table-striped">
      <tbody>
        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigNameLabel']}</label></strong></td>
          <td>${configInstance.reportConfigBean.reportConfigName}</td>
        </tr>
        
        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigDescriptionLabel']}</label></strong></td>
          <td>${configInstance.reportConfigBean.reportConfigDescription}</td>
        </tr>
        
        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigTypeLabel']}</label></strong></td>
          <td>${configInstance.reportConfigBean.reportConfigType}</td>
        </tr>
        <c:if test="${grouperRequestContainer.grouperReportContainer.configBean.reportConfigType == 'SQL'}">
          <tr>
            <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigQueryLabel']}</label></strong></td>
            <td>${configInstance.reportConfigBean.reportConfigQuery}</td>
          </tr>
        </c:if>                
        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigFormatLabel']}</label></strong></td>
          <td>${configInstance.reportConfigBean.reportConfigFormat}</td>
        </tr>
        
        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigQuartzCronLabel']}</label></strong></td>
          <td>${configInstance.reportConfigBean.reportConfigQuartzCron}</td>
        </tr>

        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigFileNameLabel']}</label></strong></td>
          <td>${configInstance.reportConfigBean.reportConfigFilename}</td>
        </tr>

        <tr>
          <td style="vertical-align: top; white-space: nowrap;"><strong><label>${textContainer.text['grouperReportConfigSendEmailLabel']}</label></strong></td>
          <td>
          <c:if test="${configInstance.reportConfigBean.reportConfigSendEmail}">
            ${textContainer.text['grouperReportConfigYesSendEmailLabel']}
          </c:if>
          <c:if test="${configInstance.reportConfigBean.reportConfigSendEmail == false}">
            ${textContainer.text['grouperReportConfigNoDoNotSendEmailLabel']}
          </c:if>
          </td>
        </tr>
      </tbody>
    </table>
        
    <c:choose>
      <c:when test="${fn:length(configInstance.guiReportInstances) > 0}">
        
        <table class="table table-hover table-bordered table-striped table-condensed data-table">
          <thead>        
            <tr>
              <th>${textContainer.text['grouperReportConfigTableHeaderReportName']}</th>
              <th>${textContainer.text['grouperReportConfigTableHeaderReportLastRunTime']}</th>
              <th>${textContainer.text['grouperReportConfigTableHeaderReportStatus']}</th>
              <th>${textContainer.text['grouperReportConfigTableHeaderReportNumberOfRows']}</th>
              <th>${textContainer.text['grouperReportConfigInstanceTableViewDetails']}</th>
            </tr>
            </thead>
            <tbody>
              <c:set var="i" value="0" />
              <c:forEach items="${configInstance.guiReportInstances}" var="guiReportInstance">
              
                <tr>
                   <td style="white-space: nowrap;">
                    ${guiReportInstance.reportConfigBean.reportConfigName}
                   </td>
                   <td style="white-space: nowrap;">${guiReportInstance.runTime}</td>
                   <td style="white-space: nowrap;">${guiReportInstance.reportInstance.reportInstanceStatus}</td>
                   <td style="white-space: nowrap;">${guiReportInstance.reportInstance.reportInstanceSizeFriendly}</td>
                   <td>
                     <a href="#" onclick="return guiV2link('operation=UiV2GrouperReport.viewReportInstanceDetailsForFolder&attributeAssignId=${guiReportInstance.reportInstance.attributeAssignId}&stemId=${grouperRequestContainer.stemContainer.guiStem.stem.id}');">
                       ${textContainer.text['grouperReportConfigInstanceTableViewDetails']}
                     </a>
                   </td>
              </c:forEach>
             
             </tbody>
         </table>
        
      </c:when>
      <c:otherwise>
        <div class="row-fluid">
          <div class="span9"> <p><b>${textContainer.text['grouperReportNoInstancesFound'] }</b></p></div>
        </div>
      </c:otherwise>
    </c:choose>

  </div>
</div>
                