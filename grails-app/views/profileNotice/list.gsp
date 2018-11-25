<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'profileNotice.entities', default: 'ProfileNotice List')}"/>
    <g:set var="entity" value="${message(code: 'profileNotice.entity', default: 'ProfileNotice')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'ProfileNotice List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="profileNoticeCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'profileNotice', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="profileNoticeSearchForm">
            <g:render template="/profileNotice/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['profileNoticeTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('profileNoticeSearchForm');_dataTables['profileNoticeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="profileNoticeTable" searchFormName="profileNoticeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="profileNotice" spaceBefore="true" hasRow="true"
              action="filter" serviceName="profileNotice">
    <el:dataTableAction controller="profileNotice" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show profileNotice')}"/>
    <el:dataTableAction controller="profileNotice" action="edit" actionParams="encodedId" class="blue icon-pencil" showFunction="manageActions"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit profileNotice')}"/>
    <el:dataTableAction controller="profileNotice" action="delete" actionParams="encodedId" class="red icon-trash" showFunction="manageActions"
                        type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete profileNotice')}"/>
</el:dataTable>

<script>
    function manageActions(row){
        if(row.profileNoticeStatus == "${message(code:'EnumProfileNoticeStatus.NEW')}"){
            return true;
        }
        return false;
    }
</script>

</body>
</html>