<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacancy.entity', default: 'Vacancy List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Vacancy List')}"/>
    <title>${title}</title>
</head>

<body>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'vacancy', action: 'list')}'"/>
    </div></div>
</div>

<g:render template="show" model="[vacancy:vacancy]" />

<div class="clearfix form-actions text-center">
    <g:if test="${vacancy?.vacancyStatus != ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.POSTED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'vacancy', action: 'edit', params: [encodedId: vacancy?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" />
</div>
</body>
</html>








