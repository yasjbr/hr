<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'dispatchRequest.entity', default: 'DispatchRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'DispatchRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'dispatchRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:warning label="${message(code: 'dispatchRequest.warning.message', default: 'dispatchRequest warning')}" />
            <msg:page />
            <el:validatableResetForm name="dispatchRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="dispatchRequest" action="save">
                <g:render template="/dispatchRequest/form" model="[dispatchRequest:dispatchRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
