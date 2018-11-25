<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'legalIdentifier.entity', default: 'LegalIdentifier List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'LegalIdentifier List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'legalIdentifier',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="legalIdentifierForm" controller="legalIdentifier" action="update">
                <el:hiddenField name="id" value="${legalIdentifier?.id}" />
                <g:render template="/pcore/person/legalIdentifier/form" model="[legalIdentifier:legalIdentifier]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>