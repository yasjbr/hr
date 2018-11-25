<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusListEmployee.entity', default: 'MaritalStatusListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'MaritalStatusListEmployee List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="maritalStatusListEmployeeForm" controller="maritalStatusListEmployee" action="update">
                <g:render template="/maritalStatusListEmployee/form" model="[maritalStatusListEmployee:maritalStatusListEmployee]"/>
                <el:hiddenField name="id" value="${maritalStatusListEmployee?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>