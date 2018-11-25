<script type="text/javascript">
    function callBackApplicantEducation(json) {
        if (json.success) {
            if (json.data && json.data.id) {
                %{--$("#applicantId").val(json.data.applicant.id);--}%
                %{--var newOption = new Option("${applicantEducation?.applicant?.localFullName}", "${applicantEducation?.applicant?.id}", true, true);--}%
                %{--$('#applicantId').append(newOption);--}%
                %{--$('#applicantId').trigger('change');--}%


                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }

</script>
<el:validatableResetForm callBackFunction="callBackApplicantEducation" name="applicantEducationForm"
                         controller="applicant" action="saveApplicantEducation">
    <g:render template="/applicantEducation/form" model="[applicant:applicant]" />
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>