<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmployeeStatusHistory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeeStatusHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employeeStatusHistoryForm" callLoadingFunction="performPostActionWithEncodedId" controller="employeeStatusHistory" action="save">
                <g:render template="/employeeStatusHistory/form" model="[employeeStatusHistory:employeeStatusHistory]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
