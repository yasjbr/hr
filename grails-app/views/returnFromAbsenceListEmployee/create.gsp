<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceListEmployee.entity', default: 'ReturnFromAbsenceListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ReturnFromAbsenceListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="returnFromAbsenceListEmployeeForm" callLoadingFunction="performPostActionWithEncodedId" controller="returnFromAbsenceListEmployee" action="save">
                <g:render template="/returnFromAbsenceListEmployee/form" model="[returnFromAbsenceListEmployee:returnFromAbsenceListEmployee]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
