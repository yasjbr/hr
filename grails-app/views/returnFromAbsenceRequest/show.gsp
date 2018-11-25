<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ReturnFromAbsenceRequest List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/returnFromAbsenceRequest/show" model="[returnFromAbsenceRequest:returnFromAbsenceRequest]" />

</body>
</html>