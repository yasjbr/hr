<el:validatableForm name="applicantContactInfoForm" controller="applicant" action="updateContactInfo">
    <el:hiddenField name="id" value="${applicantContactInfo?.id}"/>
    <g:render template="/applicantContactInfo/form" model="[applicantContactInfo: applicantContactInfo, applicant:applicant]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="cancel" accessUrl="${createLink(controller: tabEntityName, action: 'list')}"
                   onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>
