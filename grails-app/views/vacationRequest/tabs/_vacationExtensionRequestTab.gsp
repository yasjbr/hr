<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'vacationExtensionRequest.entities', default: 'vacationExtensionRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'vacationExtensionRequest.entity', default: 'vacationExtensionRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'vacationExtensionRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>

%{--this form to get vacationExtensionRequest for vacationRequest --}%
<el:form action="#" style="display: none;" name="vacationExtensionRequestForm">
    <el:hiddenField name="vacationRequest.id" id="vacationRequestId" value="${entityId}"/>
</el:form>

<el:dataTable id="vacationExtensionRequestTable" searchFormName="vacationExtensionRequestForm"
              dataTableTitle="${title}"
              hasCheckbox="false" widthClass="col-sm-12" controller="vacationExtensionRequest" spaceBefore="true"
              hasRow="true" action="filter" serviceName="vacationExtensionRequest" domainColumns="DOMAIN_TAB_COLUMNS">
</el:dataTable>
</body>
</html>