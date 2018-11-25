<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'MaritalStatusRequest List')}" />
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
            <msg:warning label="${message(code: 'maritalStatusRequest.createNew.warning.message', default: 'Warning')}" />
            <el:validatableResetForm name="maritalStatusRequestForm" callBackFunction="successCreateRequest" controller="maritalStatusRequest" action="save">
                <g:render template="/maritalStatusRequest/form" model="[maritalStatusRequest:maritalStatusRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
