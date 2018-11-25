<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'jobRequirement.entity', default: 'JobRequirement List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'JobRequirement List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'jobRequirement',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${jobRequirement?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'jobRequirement.descriptionInfo.localName.label',default:'localName')}" />
    <lay:showElement value="${jobRequirement?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'jobRequirement.descriptionInfo.latinName.label',default:'latinName')}" />
    <lay:showElement value="${jobRequirement?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'jobRequirement.descriptionInfo.hebrewName.label',default:'hebrewName')}" />
    <lay:showElement value="${jobRequirement?.jobTitle?.descriptionInfo?.localName}" type="JobTitle" label="${message(code:'jobRequirement.jobTitle.label',default:'jobTitle')}" />
    <lay:showElement value="${jobRequirement?.note}" type="String" label="${message(code:'jobRequirement.note.label',default:'note')}" />
    <lay:showElement value="${jobRequirement?.universalCode}" type="String" label="${message(code:'jobRequirement.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'jobRequirement',action:'edit',params: [encodedId:"${jobRequirement?.encodedId}"] )}'"/>
</div>

</body>
</html>