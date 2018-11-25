<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'firm.entity', default: 'Firm List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Firm List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${firm?.code}" type="String" label="${message(code: 'firm.code.label', default: 'code')}"/>
    <lay:showElement value="${firm?.name}" type="String" label="${message(code: 'firm.name.label', default: 'name')}"/>
    <lay:showElement value="${firm?.transientData?.coreName}" type="Long"
                     label="${message(code: 'firm.coreOrganizationId.label', default: 'coreOrganizationId')}"/>
    <lay:showElement value="${firm?.note}" type="String" label="${message(code: 'firm.note.label', default: 'note')}"/>
    <lay:showElement value="${firm?.provinceFirms?.province?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'firm.name.label', default: 'name')}"/>

</lay:showWidget>
<g:if test="${firm?.supportContactInfo}">
    <lay:showWidget size="12" title="${message(code: 'firm.supportContactInfo.label', default: 'contact info')}">
        <lay:showElement value="${firm?.supportContactInfo?.name}"
                         label="${message(code: 'firmSupportContactInfo.name.label', default: 'name')}"/>
        <lay:showElement value="${firm?.supportContactInfo?.phoneNumber}"
                         label="${message(code: 'firmSupportContactInfo.phoneNumber.label', default: 'phone number')}"/>
        <lay:showElement value="${firm?.supportContactInfo?.faxNumber}"
                         label="${message(code: 'firmSupportContactInfo.faxNumber.label', default: 'fax number')}"/>
        <lay:showElement value="${firm?.supportContactInfo?.email}"
                         label="${message(code: 'firmSupportContactInfo.email.label', default: 'email')}"/>
    </lay:showWidget>
</g:if>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'firm', action: 'edit', params: [encodedId: firm?.encodedId])}'"/>
</div>
</body>
</html>