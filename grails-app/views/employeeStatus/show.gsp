<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employeeStatus.entity', default: 'EmployeeStatus List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'EmployeeStatus List')}"/>
    <title>${title}</title>
</head>

<body>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'employeeStatus', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<br/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeStatus?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'employeeStatus.descriptionInfo.localName.label', default: 'localName')}"/>
    <lay:showElement value="${employeeStatus?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'employeeStatus.descriptionInfo.latinName.label', default: 'latinName')}"/>
    <lay:showElement value="${employeeStatus?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'employeeStatus.descriptionInfo.hebrewName.label', default: 'hebrewName')}"/>
    <lay:showElement value="${employeeStatus?.description}" type="String"
                     label="${message(code: 'employeeStatus.description.label', default: 'description')}"/>
    <lay:showElement value="${employeeStatus?.employeeStatusCategory?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'employeeStatus.employeeStatusCategory.label', default: 'employeeStatusCategory')}"/>

    <g:if test="${employeeStatus?.employeeStatusCategory?.id == ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.UNCOMMITTED.value}">
        <lay:showElement value="${employeeStatus?.allowReturnToService}" type="Boolean"
                         label="${message(code: 'employeeStatus.allowReturnToService.label', default: 'allowReturnToService')}"/>
    </g:if>

    <lay:showElement value="${employeeStatus?.universalCode}" type="String"
                     label="${message(code: 'employeeStatus.universalCode.label', default: 'universalCode')}"/>

</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'employeeStatus', action: 'edit', params: [encodedId: employeeStatus?.encodedId])}'"/>
</div>
</body>
</html>