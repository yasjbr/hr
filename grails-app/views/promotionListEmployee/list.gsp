<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'promotionListEmployee.entities', default: 'PromotionListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'promotionListEmployee.entity', default: 'PromotionListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PromotionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="promotionListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'promotionListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="promotionListEmployeeSearchForm">
            <g:render template="/promotionListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['promotionListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('promotionListEmployeeSearchForm');_dataTables['promotionListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="promotionListEmployeeTable" searchFormName="promotionListEmployeeSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="promotionListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="promotionListEmployee">
    <el:dataTableAction controller="promotionListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show promotionListEmployee')}" />
    <el:dataTableAction controller="promotionListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit promotionListEmployee')}" />
    <el:dataTableAction controller="promotionListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete promotionListEmployee')}" />
</el:dataTable>
</body>
</html>