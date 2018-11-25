<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'petitionListEmployee.entity', default: 'PetitionListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PetitionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'petitionListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${petitionListEmployee?.petitionList}" type="PetitionList" label="${message(code:'petitionListEmployee.petitionList.label',default:'petitionList')}" />
    <lay:showElement value="${petitionListEmployee?.petitionListEmployeeNotes}" type="Set" label="${message(code:'petitionListEmployee.petitionListEmployeeNotes.label',default:'petitionListEmployeeNotes')}" />
    <lay:showElement value="${petitionListEmployee?.petitionRequest}" type="PetitionRequest" label="${message(code:'petitionListEmployee.petitionRequest.label',default:'petitionRequest')}" />
    <lay:showElement value="${petitionListEmployee?.recordStatus}" type="enum" label="${message(code:'petitionListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
</lay:showWidget>
<el:row />

</body>
</html>