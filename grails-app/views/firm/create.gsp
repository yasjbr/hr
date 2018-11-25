<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'firm.entity', default: 'Firm List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Firm List')}"/>
    <title>${title}</title>
    <g:render template="scripts"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'firm', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="firmForm" callLoadingFunction="performPostActionWithEncodedId" controller="firm"
                                     action="save">
                <g:render template="/firm/form" model="[firm: firm]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'firm',action:'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
