<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'VacancyAdvertisements List')}" />
    <title>${title}</title>
    <g:render template="script"/>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'vacancyAdvertisements',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="vacancyAdvertisementsForm" controller="vacancyAdvertisements" action="update">
                <el:hiddenField name="id" value="${vacancyAdvertisements?.id}" />
                <g:render template="/vacancyAdvertisements/form" model="[vacancyAdvertisements:vacancyAdvertisements]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>