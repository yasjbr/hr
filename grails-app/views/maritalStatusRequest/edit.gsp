<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'MaritalStatusRequest List')}" />
    <title>${title}</title>
    <g:render template="script" />
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="maritalStatusRequestForm" controller="maritalStatusRequest" action="update">
                <g:render template="/maritalStatusRequest/form" model="[maritalStatusRequest:maritalStatusRequest]"/>
                <el:hiddenField name="encodedId" value="${maritalStatusRequest?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true" withPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>