<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DisciplinaryCategory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${disciplinaryCategory?.descriptionInfo?.localName}" type="string" label="${message(code:'disciplinaryCategory.descriptionInfo.localName.label',default:'local name')}" />
    <lay:showElement value="${disciplinaryCategory?.descriptionInfo?.latinName}" type="string" label="${message(code:'disciplinaryCategory.descriptionInfo.latinName.label',default:'latin name')}" />
    <lay:showElement value="${disciplinaryCategory?.descriptionInfo?.hebrewName}" type="string" label="${message(code:'disciplinaryCategory.descriptionInfo.hebrewName.label',default:'hebrew name')}" />
    <lay:showElement value="${disciplinaryCategory?.universalCode}" type="String" label="${message(code:'disciplinaryCategory.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'disciplinaryCategory',action:'edit',params: [encodedId:disciplinaryCategory?.encodedId,backFunction:'show'])}'"/>
    <btn:backButton />
</div>
</body>
</html>