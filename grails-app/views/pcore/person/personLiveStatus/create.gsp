<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonLiveStatus List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personLiveStatus',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="personLiveStatusForm" controller="personLiveStatus" action="save">
                <g:render template="/pcore/person/personLiveStatus/form" model="[personLiveStatus:personLiveStatus]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
