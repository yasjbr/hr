<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DisciplinaryRequest List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryRequest',action:'list')}'"/>
    </div></div>
</div>
<g:render template="/disciplinaryRequest/show" model="[disciplinaryRequest:disciplinaryRequest]" />
<div class="clearfix form-actions text-center">
    <g:if test="${disciplinaryRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'disciplinaryRequest', action: 'edit', params: [encodedId: disciplinaryRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton />
</div>
</body>
</html>