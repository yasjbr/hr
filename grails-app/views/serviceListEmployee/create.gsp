<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'serviceListEmployee.entity', default: 'ServiceListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ServiceListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'serviceListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="serviceListEmployeeForm" callLoadingFunction="performPostActionWithEncodedId" controller="serviceListEmployee" action="save">
                <g:render template="/serviceListEmployee/form" model="[serviceListEmployee:serviceListEmployee]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
