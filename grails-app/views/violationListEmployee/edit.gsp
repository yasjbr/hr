<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'violationListEmployee.entity', default: 'ViolationListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ViolationListEmployee List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'violationListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="violationListEmployeeForm" controller="violationListEmployee" action="update">
                <g:render template="/violationListEmployee/form" model="[violationListEmployee:violationListEmployee]"/>
                <el:hiddenField name="id" value="${violationListEmployee?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>