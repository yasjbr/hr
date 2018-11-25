<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'returnFromAbsenceList.entities', default: 'returnFromAbsenceList List')}"/>
    <g:set var="entity" value="${message(code: 'returnFromAbsenceList.entity', default: 'returnFromAbsenceList')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'returnFromAbsenceList List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="returnFromAbsenceListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'returnFromAbsenceList', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="returnFromAbsenceListSearchForm">
            <g:render template="/returnFromAbsenceList/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['returnFromAbsenceListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('returnFromAbsenceListSearchForm');_dataTables['returnFromAbsenceListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="returnFromAbsenceListTable" searchFormName="returnFromAbsenceListSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="returnFromAbsenceList" spaceBefore="true" hasRow="true"
              action="filter" serviceName="returnFromAbsenceList">
    <el:dataTableAction controller="returnFromAbsenceList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show returnFromAbsenceList')}"/>

    <el:dataTableAction controller="returnFromAbsenceList" action="edit" actionParams="encodedId"
                        showFunction="manageExecuteEdit"  class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit returnFromAbsenceList')}"/>

    <el:dataTableAction controller="returnFromAbsenceList" action="delete" actionParams="encodedId"
                        showFunction="manageExecuteDelete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete returnFromAbsenceList')}"/>

    <el:dataTableAction controller="returnFromAbsenceList" action="manageReturnFromAbsenceList"
                        class="icon-cog"
                        message="${message(code: 'returnFromAbsenceList.manage.label')}"
                        actionParams="['encodedId']"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}" />

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>