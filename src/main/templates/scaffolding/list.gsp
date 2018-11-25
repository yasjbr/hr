<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="\${message(code: '${propertyName}.entities', default: '${className} List')}" />
    <g:set var="entity" value="\${message(code: '${propertyName}.entity', default: '${className}')}" />
    <g:set var="title" value="\${message(code: 'default.list.label',args:[entities], default: '${className} List')}" />
    <title>\${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="${propertyName}CollapseWidget" icon="icon-search"
                    title="\${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='\${createLink(controller:'${propertyName}',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="${propertyName}SearchForm">
            <g:render template="/${propertyName}/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['${propertyName}Table'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('${propertyName}SearchForm');_dataTables['${propertyName}Table'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="${propertyName}Table" searchFormName="${propertyName}SearchForm"
              dataTableTitle="\${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="${propertyName}" spaceBefore="true" hasRow="true" action="filter" serviceName="${propertyName}">
    <el:dataTableAction controller="${propertyName}" action="show" actionParams="encodedId" class="green icon-eye" message="\${message(code:'default.show.label',args:[entity],default:'show ${propertyName}')}" />
    <el:dataTableAction controller="${propertyName}" action="edit" actionParams="encodedId" class="blue icon-pencil" message="\${message(code:'default.edit.label',args:[entity],default:'edit ${propertyName}')}" />
    <el:dataTableAction controller="${propertyName}" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="\${message(code:'default.delete.label',args:[entity],default:'delete ${propertyName}')}" />
</el:dataTable>
</body>
</html>