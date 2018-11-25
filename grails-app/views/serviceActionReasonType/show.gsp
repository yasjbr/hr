<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'serviceActionReasonType.entity', default: 'ServiceActionReasonType List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ServiceActionReasonType List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'serviceActionReasonType',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <g:render template="/DescriptionInfo/wrapperShow" model="[bean:serviceActionReasonType?.descriptionInfo]" />
    <lay:showElement value="${serviceActionReasonType?.isRelatedToEndOfService}" type="Boolean" label="${message(code:'serviceActionReasonType.isRelatedToEndOfService.label',default:'isRelatedToEndOfService')}" />
    <lay:showElement value="${serviceActionReasonType?.universalCode}" type="String" label="${message(code:'serviceActionReasonType.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />


<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'serviceActionReasonType', action: 'edit', params: [encodedId: serviceActionReasonType?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>