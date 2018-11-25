<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'organization.entities', default: 'Organization List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Organization List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="organizationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'organization',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="organizationSearchForm">
            <g:render template="/organization/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['organizationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('organizationSearchForm');_dataTables['organizationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="organizationTable" searchFormName="organizationSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="organization" spaceBefore="true" hasRow="true" action="filter" serviceName="organization">
    <el:dataTableAction controller="organization" action="show" class="green icon-eye" message="${message(code:'organization.show.label',default:'show organization')}" />
    <el:dataTableAction controller="organization" action="edit" class="blue icon-pencil" message="${message(code:'organization.edit.label',default:'edit organization')}" />
    <el:dataTableAction controller="organization" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'organization.delete.label',default:'delete organization')}" />
</el:dataTable>
</body>
</html>