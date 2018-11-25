<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'childRequest.entity', default: 'ChildRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ChildRequest List')}" />
    <title>${title}</title>
    <g:render template="script" />
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'childRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <msg:warning label="${message(code: 'childRequest.createNew.warning.message', default: 'Warning')}" />
            <el:validatableResetForm name="childRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="childRequest" action="save">
                <g:render template="/childRequest/form" model="[childRequest:childRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
