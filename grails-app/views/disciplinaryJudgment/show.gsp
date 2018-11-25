<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryJudgment.entity', default: 'disciplinaryJudgment List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'disciplinaryJudgment List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'disciplinaryJudgment', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${disciplinaryJudgment?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'disciplinaryJudgment.descriptionInfo.localName.label', default: 'local name')}"/>
    <lay:showElement value="${disciplinaryJudgment?.descriptionInfo?.latinName}" type="string"
                     label="${message(code: 'disciplinaryJudgment.descriptionInfo.latinName.label', default: 'latin name')}"/>
    <lay:showElement value="${disciplinaryJudgment?.descriptionInfo?.hebrewName}" type="string"
                     label="${message(code: 'disciplinaryJudgment.descriptionInfo.hebrewName.label', default: 'hebrew name')}"/>
    <g:if test="${disciplinaryJudgment?.isCurrencyUnit == true}">
        <lay:showElement value="${disciplinaryJudgment?.transientData?.currencyNameList}" type="set"
                         label="${message(code: 'disciplinaryJudgment.currencyIds.label', default: 'currencyIds')}"/>
    </g:if>
    <g:else>
        <lay:showElement value="${disciplinaryJudgment?.transientData?.unitOfMeasurementNameList}" type="set"
                         label="${message(code: 'disciplinaryJudgment.unitIds.label', default: 'unitIds')}"/>
    </g:else>



    <lay:showElement value="${disciplinaryJudgment?.excludedFromEligiblePromotion}" type="Boolean"
                     label="${message(code: 'disciplinaryJudgment.excludedFromEligiblePromotion.label', default: 'excludedFromEligiblePromotion')}"/>


    <lay:showElement value="${disciplinaryJudgment?.universalCode}" type="String"
                     label="${message(code: 'disciplinaryJudgment.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>
<el:row/>

<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'disciplinaryReason.entity', default: 'disciplinaryReason List')}"/>
    <g:set var="tabEntity" value="${message(code: 'disciplinaryJudgment.entity', default: 'disciplinaryJudgment')}"/>
    <g:set var="tabEntities"
           value="${message(code: 'disciplinaryJudgment.entities', default: 'disciplinaryJudgment')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list disciplinaryJudgment')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create disciplinaryJudgment')}"/>


    <el:form action="#" style="display: none;" name="disciplinaryReasonSearchForm">
        <el:hiddenField name="disciplinaryJudgment.id" value="${disciplinaryJudgment?.id}"/>
    </el:form>
    <g:render template="/disciplinaryReason/dataTable"
              model="[isInLineActions: true, title: tabList, entity: entity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>

</div>

<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'disciplinaryJudgment', action: 'edit', params: [encodedId: disciplinaryJudgment?.encodedId, backFunction: 'show'])}'"/>
</div>

</body>
</html>