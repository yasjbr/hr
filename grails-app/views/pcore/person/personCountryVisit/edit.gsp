<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonCountryVisit List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personCountryVisit',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personCountryVisitForm" controller="personCountryVisit" action="update">
                <el:hiddenField name="id" value="${personCountryVisit?.id}" />
                <g:render template="/pcore/person/personCountryVisit/form" model="[personCountryVisit:personCountryVisit]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>