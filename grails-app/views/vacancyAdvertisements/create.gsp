<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'VacancyAdvertisements List')}"/>
    <title>${title}</title>
    <g:render template="script"/>
    <script>

    </script>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'vacancyAdvertisements', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="vacancyAdvertisementsForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="vacancyAdvertisements" action="save">
                <g:render template="/vacancyAdvertisements/form"
                          model="[vacancyAdvertisements: vacancyAdvertisements]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'vacancyAdvertisements',action:'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
