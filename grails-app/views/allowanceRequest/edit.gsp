<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'allowanceRequest.entity', default: 'AllowanceRequest List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'AllowanceRequest List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'allowanceRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="allowanceRequestForm" controller="allowanceRequest" action="update">
                <g:render template="/allowanceRequest/form" model="[allowanceRequest:allowanceRequest]"/>
                <el:hiddenField name="id" value="${allowanceRequest?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>