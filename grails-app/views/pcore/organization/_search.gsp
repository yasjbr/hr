<g:render template="/organization/wrapper" model="[messageValue:message(code: 'organization.parentOrganization.label'),name:'parentOrganization.id',isRequired:false]" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" multiple="multiple" controller="contactInfo" action="autocomplete" name="contactInfos.id" label="${message(code:'organization.contactInfos.label',default:'contactInfos')}"  />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="corporationClassification" action="autocomplete" name="corporationClassification.id" label="${message(code:'organization.corporationClassification.label',default:'corporationClassification')}"   />
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:organization?.descriptionInfo,isSearch:true]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="location" action="autocomplete" name="latestAddress.id" label="${message(code:'organization.latestAddress.label',default:'latestAddress')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="latinDescription" size="8"  class="" label="${message(code:'organization.latinDescription.label',default:'latinDescription')}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="localDescription" size="8"  class="" label="${message(code:'organization.localDescription.label',default:'localDescription')}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="missionStatement" size="8"  class="" label="${message(code:'organization.missionStatement.label',default:'missionStatement')}" />
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="needRevision" size="8"  class="" label="${message(code:'organization.needRevision.label',default:'needRevision')}"  isChecked="${organization?.needRevision}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organizationFocalPoint" action="autocomplete" name="organizationFocalPoint.id" label="${message(code:'organization.organizationFocalPoint.label',default:'organizationFocalPoint')}"   />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organizationActivity" action="autocomplete" name="organizationMainActivity.id" label="${message(code:'organization.organizationMainActivity.label',default:'organizationMainActivity')}"  />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" multiple="multiple" controller="joinedOrganizationRelatedActivity" action="autocomplete" name="organizationRelatedActivities.id" label="${message(code:'organization.organizationRelatedActivities.label',default:'organizationRelatedActivities')}"  />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organizationType" action="autocomplete" name="organizationType.id" label="${message(code:'organization.organizationType.label',default:'organizationType')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="registrationNumber" size="8"  class="" label="${message(code:'organization.registrationNumber.label',default:'registrationNumber')}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="taxId" size="8"  class="" label="${message(code:'organization.taxId.label',default:'taxId')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="workingSector" action="autocomplete" name="workingSector.id" label="${message(code:'organization.workingSector.label',default:'workingSector')}"  />
</el:formGroup>