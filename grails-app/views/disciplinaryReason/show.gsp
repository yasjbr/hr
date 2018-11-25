<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'DisciplinaryReason List')}"/>
    <title>${title}</title>
</head>

<body>



<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'disciplinaryReason', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${disciplinaryReason?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'disciplinaryReason.descriptionInfo.localName.label', default: 'local name')}"/>
    <lay:showElement value="${disciplinaryReason?.descriptionInfo?.latinName}" type="string"
                     label="${message(code: 'disciplinaryReason.descriptionInfo.latinName.label', default: 'latin name')}"/>
    <lay:showElement value="${disciplinaryReason?.descriptionInfo?.hebrewName}" type="string"
                     label="${message(code: 'disciplinaryReason.descriptionInfo.hebrewName.label', default: 'hebrew name')}"/>
    <lay:showElement value="${disciplinaryReason?.disciplinaryCategories?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'disciplinaryReason.disciplinaryCategories.label', default: 'disciplinaryCategories')}"/>
    <lay:showElement value="${disciplinaryReason?.universalCode}" type="String"
                     label="${message(code: 'disciplinaryReason.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>

<el:row/>


<el:form action="#" style="display: none;" name="disciplinaryJudgmentSearchForm">
    <el:hiddenField name="disciplinaryReason.id" value="${disciplinaryReason?.id}"/>
</el:form>
<g:render template="/disciplinaryJudgment/dataTable"
          model="[isInLineActions: true, title: tabList, entity: entity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'disciplinaryReason', action: 'edit', params: [encodedId: disciplinaryReason?.encodedId])}'"/>
</div>

</body>
</html>