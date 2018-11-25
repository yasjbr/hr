<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'VacationConfiguration List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'vacationConfiguration', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${vacationConfiguration?.vacationType?.descriptionInfo?.localName}" type="VacationType"
                     label="${message(code: 'vacationConfiguration.vacationType.label', default: 'vacationType')}"/>
    <lay:showElement value="${vacationConfiguration?.militaryRank?.descriptionInfo?.localName}" type="MilitaryRank"
                     label="${message(code: 'vacationConfiguration.militaryRank.label', default: 'militaryRank')}"/>
    <lay:showElement value="${vacationConfiguration?.maxBalance}" type="Short"
                     label="${message(code: 'vacationConfiguration.maxBalance.label', default: 'maxBalance')}"/>
    <lay:showElement value="${vacationConfiguration?.maxAllowedValue}" type="Short"
                     label="${message(code: 'vacationConfiguration.maxAllowedValue.label', default: 'maxAllowedValue')}"/>
    <lay:showElement value="${vacationConfiguration?.allowedValue}" type="Short"
                     label="${message(code: 'vacationConfiguration.allowedValue.label', default: 'allowedValue')}"/>
    <lay:showElement value="${vacationConfiguration?.employmentPeriod}" type="Short"
                     label="${message(code: 'vacationConfiguration.employmentPeriod.label', default: 'employmentPeriod')}"/>
    <lay:showElement value="${vacationConfiguration?.frequency}" type="Short"
                     label="${message(code: 'vacationConfiguration.frequency.label', default: 'frequency')}"/>
    <lay:showElement value="${vacationConfiguration?.sexTypeAccepted}" type="enum"
                     label="${message(code: 'vacationConfiguration.sexTypeAccepted.label', default: 'sexTypeAccepted')}"
                     messagePrefix="EnumSexAccepted"/>
%{--<lay:showElement value="${vacationConfiguration?.transientData?.maritalStatusName}" type="string"--}%
%{--label="${message(code: 'vacationConfiguration.maritalStatusId.label', default: 'maritalStatusId')}"/>--}%

    <lay:showElement value="${vacationConfiguration?.transientData?.religionName}" type="string"
                     label="${message(code: 'vacationConfiguration.religionId.label', default: 'religionName')}"/>
    <lay:showElement value="${vacationConfiguration?.checkForAnnualLeave}" type="Boolean"
                     label="${message(code: 'vacationConfiguration.checkForAnnualLeave.label', default: 'checkForAnnualLeave')}"/>

    <lay:showElement value="${vacationConfiguration?.isExternal}" type="Boolean"
                     label="${message(code: 'vacationConfiguration.isExternal.label', default: 'isExternal')}"/>
    <lay:showElement value="${vacationConfiguration?.isBreakable}" type="Boolean"
                     label="${message(code: 'vacationConfiguration.isBreakable.label', default: 'isBreakable')}"/>
    <lay:showElement value="${vacationConfiguration?.takenFully}" type="Boolean"
                     label="${message(code: 'vacationConfiguration.takenFully.label', default: 'takenFully')}"/>
    <lay:showElement value="${vacationConfiguration?.isTransferableToNewYear}" type="Boolean"
                     label="${message(code: 'vacationConfiguration.isTransferableToNewYear.label', default: 'isTransferableToNewYear')}"/>


    <lay:showElement
            value="${vacationConfiguration?.vacationTransferValue ? vacationConfiguration?.vacationTransferValue : '0.0'}"
            type="Float"
            label="${message(code: 'vacationConfiguration.vacationTransferValue.label', default: 'vacationTransferValue')}"/>
</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'vacationConfiguration', action: 'edit', params: [encodedId: vacationConfiguration?.encodedId])}'"/>
</div>
</body>
</html>