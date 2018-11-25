<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'correspondenceTemplate.entity', default: 'CorrespondenceTemplate List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'CorrespondenceTemplate List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'correspondenceTemplate',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <g:render template="/DescriptionInfo/wrapperShow" model="[bean:correspondenceTemplate?.descriptionInfo]" />
    <lay:showElement value="${correspondenceTemplate?.coverLetter}" type="String" label="${message(code:'correspondenceTemplate.coverLetter.label',default:'coverLetter')}" />
</lay:showWidget>
<el:row />

</body>
</html>