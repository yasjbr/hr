<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'petitionRequest.entity', default: 'PetitionRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PetitionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'petitionRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="petitionRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="petitionRequest" action="save">
                <g:render template="/petitionRequest/form" model="[petitionRequest:petitionRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" onClick="window.location.href='${createLink(controller: 'petitionRequest', action: 'create')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
