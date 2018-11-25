<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'applicantStatusHistory.entities', default: 'ApplicantStatusHistory List')}" />
    <g:set var="entity" value="${message(code: 'applicantStatusHistory.entity', default: 'ApplicantStatusHistory')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ApplicantStatusHistory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="applicantStatusHistoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicant',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantStatusHistorySearchForm">
            <el:hiddenField name="encodedApplicantId" value="${applicant?.encodedId}" />
            <g:render template="/applicantStatusHistory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['applicantStatusHistoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('applicantStatusHistorySearchForm');_dataTables['applicantStatusHistoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<br/>

<el:row/>
<el:row/>
<el:row>


    <lay:showWidget size="12">
        <lay:showElement value="${applicant?.transientData?.personDTO?.localFullName}" type="String"
                         label="${message(code: 'applicant.personName.label', default: 'personName')}"/>
        <lay:showElement value="${applicant?.applicantCurrentStatus?.applicantStatus}" type="enum"
                         label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>
    </lay:showWidget>
</el:row>


<el:dataTable id="applicantStatusHistoryTable" searchFormName="applicantStatusHistorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicantStatusHistory" spaceBefore="true" hasRow="true" action="filter" serviceName="applicantStatusHistory">
    <el:dataTableAction controller="applicantStatusHistory" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show applicantStatusHistory')}" />
</el:dataTable>
</body>
</html>