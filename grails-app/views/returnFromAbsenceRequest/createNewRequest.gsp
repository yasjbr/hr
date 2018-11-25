<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ReturnFromAbsenceRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'returnFromAbsenceRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="returnFromAbsenceRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="returnFromAbsenceRequest" action="save">
                <g:render template="/returnFromAbsenceRequest/form" model="[returnFromAbsenceRequest:returnFromAbsenceRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" onClick="window.location.href='${createLink(controller: 'returnFromAbsenceRequest', action: 'create')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
