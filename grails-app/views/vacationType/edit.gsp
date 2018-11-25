<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacationType.entity', default: 'VacationType List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'VacationType List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'vacationType',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="vacationTypeForm" controller="vacationType" action="update">
                <g:render template="/vacationType/form" model="[vacationType:vacationType]"/>
                <el:hiddenField name="id" value="${vacationType?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>