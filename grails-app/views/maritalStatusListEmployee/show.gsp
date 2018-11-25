<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusListEmployee.entity', default: 'MaritalStatusListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'MaritalStatusListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${maritalStatusListEmployee?.maritalStatusList}" type="MaritalStatusList" label="${message(code:'maritalStatusListEmployee.maritalStatusList.label',default:'maritalStatusList')}" />
    <lay:showElement value="${maritalStatusListEmployee?.maritalStatusRequest}" type="MaritalStatusRequest" label="${message(code:'maritalStatusListEmployee.maritalStatusRequest.label',default:'maritalStatusRequest')}" />
    <lay:showElement value="${maritalStatusListEmployee?.orderNo}" type="String" label="${message(code:'maritalStatusListEmployee.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${maritalStatusListEmployee?.recordStatus}" type="enum" label="${message(code:'maritalStatusListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${maritalStatusListEmployee?.statusReason}" type="String" label="${message(code:'maritalStatusListEmployee.statusReason.label',default:'statusReason')}" />
</lay:showWidget>
<el:row />

</body>
</html>