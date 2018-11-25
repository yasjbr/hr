<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firm.entity', default: 'Firm List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'Firm List')}" />
    <title>${title}</title>
    <g:render template="scripts"/>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'firm',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="firmForm" controller="firm" action="update">
                <el:hiddenField name="id" value="${firm?.id}" />
                <g:render template="/firm/form" model="[firm:firm]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>