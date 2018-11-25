<el:validatableForm callBackFunction="callBackApplicantEducation" name="applicantEducationForm" controller="applicant" action="updateApplicantEducation">
    <el:hiddenField name="id" value="${applicantEducation?.id}" />
    <g:render template="/applicantEducation/form" model="[isApplicantDisabled:isApplicantDisabled?:params.isApplicantDisabled,
                                                       applicantEducation:applicantEducation]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="save"/>
    <el:formButton functionName="cancel" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>