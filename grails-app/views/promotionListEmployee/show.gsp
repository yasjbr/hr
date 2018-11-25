<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'promotionListEmployee.entity', default: 'PromotionListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PromotionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'promotionListEmployee',action:'list')}'"/>
    </div></div>
</div>
<el:row/><br/><el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${promotionListEmployee?.employee}" type="Employee" label="${message(code:'promotionListEmployee.employee.label',default:'employee')}" />
    <lay:showElement value="${promotionListEmployee?.actualDueDate}" type="ZonedDate" label="${message(code:'promotionListEmployee.actualDueDate.label',default:'rankDate')}" />
    <lay:showElement value="${promotionListEmployee?.militaryRank}" type="MilitaryRank" label="${message(code:'promotionListEmployee.militaryRank.label',default:'rankDate')}" />
    <lay:showElement value="${promotionListEmployee?.militaryRankType}" type="MilitaryRankType" label="${message(code:'promotionListEmployee.militaryRankType.label',default:'rankDate')}" />
    <lay:showElement value="${promotionListEmployee?.promotionReason}" type="enum" label="${message(code:'promotionListEmployee.promotionReason.label',default:'promotionReason')}" messagePrefix="EnumPromotionReason" />
    <lay:showElement value="${promotionListEmployee?.recordStatus}" type="enum" label="${message(code:'promotionListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${promotionListEmployee?.statusReason}" type="String" label="${message(code:'promotionListEmployee.statusReason.label',default:'statusReason')}" />
</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>