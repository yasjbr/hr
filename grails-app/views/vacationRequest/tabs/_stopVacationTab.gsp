<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'stopVacationRequest.entities', default: 'stopVacationRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'stopVacationRequest.entity', default: 'stopVacationRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'VacationExtensionRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>

%{--this form to get stopVacationRequest for vacationRequest --}%
<el:form action="#" style="display: none;" name="stopVacationForm">
    <el:hiddenField name="vacationRequest.id" id="vacationRequestId" value="${entityId}"/>
</el:form>

<el:dataTable id="stopVacationTable" searchFormName="stopVacationForm"
              dataTableTitle="${title}"
              hasCheckbox="false" widthClass="col-sm-12" controller="stopVacationRequest" spaceBefore="true"
              hasRow="true" action="filter" serviceName="stopVacationRequest" domainColumns="DOMAIN_TAB_COLUMNS">
</el:dataTable>
</body>
</html>