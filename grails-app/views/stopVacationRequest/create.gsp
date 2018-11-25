<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'stopVacationRequest.entity', default: 'StopVacation List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'StopVacation List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'stopVacationRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="stopVacationForm" callLoadingFunction="performPostActionWithEncodedId" controller="stopVacationRequest" action="save">
                <g:render template="/stopVacationRequest/form" model="[stopVacationRequest:stopVacationRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
