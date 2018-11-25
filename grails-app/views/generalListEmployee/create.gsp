<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'generalListEmployee.entity', default: 'GeneralListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'GeneralListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'generalListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="generalListEmployeeForm" callLoadingFunction="performPostActionWithEncodedId" controller="generalListEmployee" action="save">
                <g:render template="/generalListEmployee/form" model="[generalListEmployee:generalListEmployee]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
