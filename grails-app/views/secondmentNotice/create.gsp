<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'secondmentNotice.entity', default: 'SecondmentNotice List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'SecondmentNotice List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'secondmentNotice',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="secondmentNoticeForm" callLoadingFunction="performPostActionWithEncodedId" controller="secondmentNotice" action="save">
                <g:render template="/secondmentNotice/form" model="[secondmentNotice:secondmentNotice]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
