<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'aocCorrespondenceList.entities', default: 'AocCorrespondenceList List')}"/>
    %{--<g:set var="entity" value="${message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList')}" />--}%
    <g:set var="entity" value="${message(code: 'EnumCorrespondenceDirection.INCOMING', default: 'Incoming')}"/>
    <g:set var="title"
           value="${message(code: 'aocCorrespondenceList.list.label', args: [message(code: 'EnumCorrespondenceDirection.' + correspondenceDirection.toString()),
                                                                             message(code: 'EnumCorrespondenceType.' + correspondenceType.toString())], default: 'AocCorrespondenceList List')}"/>
    %{--<g:set var="title" value="${title}" />--}%
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="aocCorrespondenceListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'aocCorrespondenceList', action: createAction, params: [correspondenceType:correspondenceType])}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="aocCorrespondenceListSearchForm">
            <g:render template="/aocCorrespondenceList/search"
                      model="[correspondenceDirection: correspondenceDirection]"/>
            <el:formButton functionName="search" onClick="_dataTables['aocCorrespondenceListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('aocCorrespondenceListSearchForm');_dataTables['aocCorrespondenceListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="aocCorrespondenceListTable" searchFormName="aocCorrespondenceListSearchForm"
              dataTableTitle="${title}" hasCheckbox="true" widthClass="col-sm-12" controller="aocCorrespondenceList"
              spaceBefore="true" hasRow="true" action="filter" serviceName="aocCorrespondenceList">

    <el:dataTableAction controller="aocCorrespondenceList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show aocCorrespondenceList')}"/>

    <el:dataTableAction controller="aocCorrespondenceList" action="edit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit aocCorrespondenceList')}"
                        showFunction="manageEditAction"/>
    <el:dataTableAction controller="aocCorrespondenceList" action="delete" actionParams="encodedId"
                        class="red icon-trash"
                        type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete aocCorrespondenceList')}"
                        showFunction="manageDeleteAction"/>
    <el:dataTableAction controller="aocCorrespondenceList" action="manageList" class="icon-cog"
                        message="${message(code: 'allowanceList.manage.label')}" actionParams="encodedId" showFunction="showManageList"/>
    <el:dataTableAction controller="aocCorrespondenceList" action="createOutgoing" class="icon-plus-circled"
                        message="${message(code: 'aocCorrespondenceList.createOutgoing.label')}"
                        actionParams="['encodedId', 'correspondenceType']" showFunction="showCreateOutgoing"/>
    <el:dataTableAction controller="aocCorrespondenceList" action="createIncoming" class="icon-plus-circled"
                        message="${message(code: 'aocCorrespondenceList.createIncoming.label')}"
                        actionParams="['encodedId', 'correspondenceType']" showFunction="showCreateIncoming"/>
    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="['id', 'threadId']"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>
</el:dataTable>


<g:render template="/attachment/attachmentSharedTemplate" model="[
        referenceObject:referenceObject ,
        operationType:operationType,
        sharedOperationType:sharedOperationType,
        attachmentTypeList:attachmentTypeList,
        isNonSharedObject:true
]"/>

<script type="text/javascript">

    function manageEditAction(row) {
        if (row.enumCurrentStatus == "${EnumCorrespondenceStatus.CREATED.toString()}" ||
                row.enumCurrentStatus == "${EnumCorrespondenceStatus.NEW.toString()}") {
            return true;
        }
        return false;
    }

    function manageDeleteAction(row) {
        return row.canDeleteList;
    }

    function showCreateOutgoing(row){
        if (row.correspondenceDirection == "${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection.INCOMING.toString()}" &&
                row.enumCurrentStatus != "${EnumCorrespondenceStatus.NEW.toString()}") {
            return true;
        }
        return false;
    }

    function showCreateIncoming(row){
        if (row.correspondenceDirection == "${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection.OUTGOING.toString()}") {
            return true;
        }
        return false;
    }

    function showManageList(row){
        if (row.enumCurrentStatus == "${EnumCorrespondenceStatus.NEW.toString()}") {
            return false;
        }
        return true;
    }
</script>
</body>
</html>