<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'suspensionExtensionListEmployeeNote.entities', default: 'SuspensionExtensionListEmployeeNote List')}"/>
    <g:set var="entity"
           value="${message(code: 'suspensionExtensionListEmployeeNote.entity', default: 'SuspensionExtensionListEmployeeNote')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'SuspensionExtensionListEmployeeNote List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
%{--<lay:collapseWidget id="suspensionExtensionListEmployeeNoteCollapseWidget" icon="icon-search"--}%
%{--title="${message(code:'default.search.label',args:[entities])}"--}%
%{--size="12" collapsed="true" data-toggle="collapse" >--}%
%{--<lay:widgetToolBar>--}%
%{--<btn:buttonGroup>--}%
%{--<btn:createButton onClick="window.location.href='${createLink(controller:'suspensionExtensionListEmployeeNote',action:'create')}'"/>--}%
%{--</btn:buttonGroup>--}%
%{--</lay:widgetToolBar>--}%
%{--<lay:widgetBody>--}%
%{--<el:form action="#" name="suspensionExtensionListEmployeeNoteSearchForm">--}%
%{--<g:render template="/suspensionExtensionListEmployeeNote/search" model="[:]"/>--}%
%{--<el:formButton functionName="search" onClick="_dataTables['suspensionExtensionListEmployeeNoteTable'].draw()"/>--}%
%{--<el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('suspensionExtensionListEmployeeNoteSearchForm');_dataTables['suspensionExtensionListEmployeeNoteTable'].draw();"/>--}%
%{--</el:form>--}%
%{--</lay:widgetBody>--}%
%{--</lay:collapseWidget>--}%


<lay:widget>
    <lay:widgetBody>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'suspensionExtensionListEmployeeNote', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetBody>
</lay:widget>

<el:dataTable id="suspensionExtensionListEmployeeNoteTable"
              searchFormName="suspensionExtensionListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="suspensionExtensionListEmployeeNote"
              spaceBefore="true" hasRow="true" action="filter" serviceName="suspensionExtensionListEmployeeNote">
%{--<el:dataTableAction controller="suspensionExtensionListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show suspensionExtensionListEmployeeNote')}" />--}%
%{--<el:dataTableAction controller="suspensionExtensionListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit suspensionExtensionListEmployeeNote')}" />--}%
    <el:dataTableAction controller="suspensionExtensionListEmployeeNote" action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete suspensionExtensionListEmployeeNote')}"/>
</el:dataTable>
</body>
</html>