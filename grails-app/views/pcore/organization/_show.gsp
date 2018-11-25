<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${organization?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'organization.descriptionInfo.label',default:'descriptionInfo')}" />
    <lay:showElement value="${organization?.parentOrganization}" type="String" label="${message(code:'organization.parentOrganization.label',default:'corporationClassification')}" />


    <lay:showElement value="${organization?.corporationClassification}" type="CorporationClassification" label="${message(code:'organization.corporationClassification.label',default:'corporationClassification')}" />
    <lay:showElement value="${organization?.organizationType}" type="OrganizationType" label="${message(code:'organization.organizationType.label',default:'organizationType')}" />
    <lay:showElement value="${organization?.organizationMainActivity}" type="OrganizationActivity" label="${message(code:'organization.organizationMainActivity.label',default:'organizationMainActivity')}" />

    <lay:showElement value="${organization?.registrationNumber}" type="String" label="${message(code:'organization.registrationNumber.label',default:'registrationNumber')}" />

    <lay:showElement value="${organization?.missionStatement}" type="String" label="${message(code:'organization.missionStatement.label',default:'missionStatement')}" />
    <lay:showElement value="${organization?.taxId}" type="String" label="${message(code:'organization.taxId.label',default:'taxId')}" />
    <lay:showElement value="${organization?.workingSector}" type="WorkingSector" label="${message(code:'organization.workingSector.label',default:'workingSector')}" />
    <lay:showElement value="${organization?.localDescription}" type="String" label="${message(code:'organization.localDescription.label',default:'localDescription')}" />
    <lay:showElement value="${organization?.latinDescription}" type="String" label="${message(code:'organization.latinDescription.label',default:'latinDescription')}" />
    <lay:showElement value="${organization?.needRevision}" type="Boolean" label="${message(code:'organization.needRevision.label',default:'needRevision')}" />

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'organization',action:'edit',id:organization?.id)}'"/>
    <btn:listButton onClick="window.location.href='${createLink(controller:'organization',action:'list')}'"/>
</div>