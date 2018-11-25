<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'notification.entities', default: 'Notification List')}" />
    <g:set var="entity" value="${message(code: 'notification.entity', default: 'Notification')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Notification List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="notificationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetBody>
        <el:form action="#" name="notificationSearchForm">
            <el:hiddenField name="orderAdditionalColumn" value="3"/>
            <el:hiddenField name="orderAdditionalDirection" value="desc"/>
            <g:render template="/notification/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['notificationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('notificationSearchForm');_dataTables['notificationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="notificationTable" searchFormName="notificationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="notification" spaceBefore="true" hasRow="true" action="filter" serviceName="notification">
    <el:dataTableAction controller="notification" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show notification')}" />
</el:dataTable>
</body>
</html>