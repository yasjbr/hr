<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'dispatchRequest.entity', default: 'DispatchRequest List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'DispatchRequest List')}" />
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
            <msg:page />
            <el:validatableForm name="dispatchRequestForm" controller="dispatchRequest" action="update">
                <g:render template="/dispatchRequest/form" model="[dispatchRequest:dispatchRequest]"/>
                <el:hiddenField name="id" value="${dispatchRequest?.id}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>