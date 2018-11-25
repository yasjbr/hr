<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'employeeStatusCategory.entity', default: 'EmployeeStatusCategory List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'EmployeeStatusCategory List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'employeeStatusCategory', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeStatusCategory?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'employeeStatusCategory.descriptionInfo.localName.label', default: 'localName')}"/>
    <lay:showElement value="${employeeStatusCategory?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'employeeStatusCategory.descriptionInfo.latinName.label', default: 'latinName')}"/>
    <lay:showElement value="${employeeStatusCategory?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'employeeStatusCategory.descriptionInfo.hebrewName.label', default: 'hebrewName')}"/>
    <lay:showElement value="${employeeStatusCategory?.description}" type="String"
                     label="${message(code: 'employeeStatusCategory.description.label', default: 'description')}"/>
    <lay:showElement value="${employeeStatusCategory?.universalCode}" type="String"
                     label="${message(code: 'employeeStatusCategory.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>
<el:row/>

<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'employeeStatusCategory', action: 'edit', params: [encodedId: employeeStatusCategory?.encodedId])}'"/>
</div>
</body>
</html>