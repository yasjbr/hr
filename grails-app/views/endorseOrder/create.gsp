<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'endorseOrder.entity', default: 'EndorseOrder List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EndorseOrder List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="endorseOrderForm" callLoadingFunction="performPostActionWithEncodedId" controller="endorseOrder" action="save">
                <g:render template="/endorseOrder/form" model="[endorseOrder:endorseOrder]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
