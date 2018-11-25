<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'DepartmentContactInfo List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'departmentContactInfo',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="departmentContactInfoForm" controller="departmentContactInfo" action="save">
                <g:render template="/departmentContactInfo/form" model="[departmentContactInfo:departmentContactInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
