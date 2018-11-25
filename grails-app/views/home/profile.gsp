<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'default.userInfo.label', default: 'User Info')}" />
    <g:set var="title" value="${entity}" />
    <title>${title}</title>
</head>
<body>
<el:row>
    <msg:page />
    <g:render template="/layouts/profile" />
</el:row>
</body>
</html>
