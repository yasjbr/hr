<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'endorseOrder.entity', default: 'EndorseOrder List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EndorseOrder List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'loanNominatedEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="endorseOrderForm" controller="endorseOrder" action="update">
                <el:hiddenField name="encodedId" value="${endorseOrder?.encodedId}"/>
                <g:render template="/endorseOrder/form" model="[endorseOrder:endorseOrder]"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>