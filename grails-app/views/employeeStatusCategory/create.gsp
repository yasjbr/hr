<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeStatusCategory.entity', default: 'EmployeeStatusCategory List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmployeeStatusCategory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeeStatusCategory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employeeStatusCategoryForm" callLoadingFunction="performPostActionWithEncodedId" controller="employeeStatusCategory" action="save">
                <g:render template="/employeeStatusCategory/form" model="[employeeStatusCategory:employeeStatusCategory]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'employeeStatusCategory',action:'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
