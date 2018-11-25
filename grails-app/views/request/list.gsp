<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'request.entities', default: 'Request List')}"/>
    <g:set var="entity" value="${message(code: 'request.entity', default: 'Request')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'Request List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="requestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">

    <lay:widgetBody>
        <el:form action="#" name="requestSearchForm">
            <g:render template="/request/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['requestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('requestSearchForm');_dataTables['requestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
%{--<el:dataTable id="requestTable" searchFormName="requestSearchForm"--}%
%{--title="${title}"--}%
%{--dataTableTitle="${title}"--}%
%{--hasCheckbox="true" widthClass="col-sm-12" controller="request" spaceBefore="true" hasRow="true" action="filter" serviceName="request">--}%
%{--<el:dataTableAction controller="request" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show request')}" />--}%
%{--<el:dataTableAction controller="request" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit request')}" />--}%
%{--<el:dataTableAction controller="request" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete request')}" />--}%
%{--</el:dataTable>--}%





<el:dataTable id="requestTable" searchFormName="requestSearchForm"
              dataTableTitle="${message(code: 'request.waitingApprove.label', default: 'request waiting for approval')}"
              widthClass="col-sm-12" controller="request"
              spaceBefore="true" hasRow="true"
              action="filterWorkflowRequest"
              serviceName="request"
              domainColumns="DOMAIN_WORKFLOW_COLUMNS">

    <el:dataTableAction controller="request" action="manageRequestModal" type="modal-ajax"
                        actionParams="['id', 'controllerName']" class="icon-cog"
                        message="${message(code: 'request.manageRequest.label', default: 'manage request')}"/>

</el:dataTable>

<script>

    function showFunction(encodedId, controllerName) {
        window.href = '/' + controllerName + '/show?encodedId=' + encodedId
    }
</script>

</body>
</html>