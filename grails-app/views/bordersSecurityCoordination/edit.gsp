<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'BordersSecurityCoordination List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'bordersSecurityCoordination',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="bordersSecurityCoordinationForm" controller="bordersSecurityCoordination" action="update">
                <g:render template="/bordersSecurityCoordination/form" model="[bordersSecurityCoordination:bordersSecurityCoordination]"/>
                <el:hiddenField name="id" value="${bordersSecurityCoordination?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>