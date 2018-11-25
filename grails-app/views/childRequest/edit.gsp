<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'childRequest.entity', default: 'ChildRequest List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ChildRequest List')}" />
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
            <el:validatableForm name="childRequestForm" controller="childRequest" action="update">
                <g:render template="/childRequest/form" model="[childRequest:childRequest]"/>
                <el:hiddenField name="id" value="${childRequest?.id}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>