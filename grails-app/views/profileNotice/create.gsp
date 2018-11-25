<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'profileNotice.entity', default: 'ProfileNotice List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ProfileNotice List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'profileNotice',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="profileNoticeForm" callLoadingFunction="performPostActionWithEncodedId" controller="profileNotice" action="save">
                <g:render template="/profileNotice/form" model="[profileNotice:profileNotice]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
