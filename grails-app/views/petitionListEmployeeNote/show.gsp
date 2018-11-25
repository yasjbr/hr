<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'petitionListEmployeeNote.entity', default: 'PetitionListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PetitionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'petitionListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${petitionListEmployeeNote?.note}" type="String" label="${message(code:'petitionListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${petitionListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'petitionListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${petitionListEmployeeNote?.orderNo}" type="String" label="${message(code:'petitionListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${petitionListEmployeeNote?.petitionListEmployee}" type="PetitionListEmployee" label="${message(code:'petitionListEmployeeNote.petitionListEmployee.label',default:'petitionListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>