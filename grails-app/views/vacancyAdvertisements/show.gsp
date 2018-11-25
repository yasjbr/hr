<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'VacancyAdvertisements List')}"/>
    <title>${title}</title>
</head>

<body>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'vacancyAdvertisements', action: 'list')}'"/>
    </div></div>
</div>

<g:render template="show" model="[vacancyAdvertisements:vacancyAdvertisements]" />

<div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'vacancyAdvertisements', action: 'edit', params: [encodedId: vacancyAdvertisements?.encodedId])}'"/>


    <btn:backButton goToPreviousLink="true" />
</div>
</body>
</html>