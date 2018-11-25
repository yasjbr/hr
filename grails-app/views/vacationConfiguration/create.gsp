<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'VacationConfiguration List')}" />
    <title>${title}</title>
<g:render template="script"/>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'vacationConfiguration',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="vacationConfigurationForm" callLoadingFunction="performPostActionWithEncodedId" controller="vacationConfiguration" action="save">
                <g:render template="/vacationConfiguration/form" model="[vacationConfiguration:vacationConfiguration]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'vacationConfiguration',action:'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>


</body>
</html>
