<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'stopVacationRequest.entity', default: 'StopVacation List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'StopVacation List')}" />
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
            <el:validatableForm name="stopVacationForm" controller="stopVacationRequest" action="update">
                <g:render template="/stopVacationRequest/form" model="[stopVacationRequest:stopVacationRequest]"/>
                <el:hiddenField name="id" value="${stopVacationRequest?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>