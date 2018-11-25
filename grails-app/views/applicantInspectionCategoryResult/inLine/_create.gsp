<script type="text/javascript">
    function callBackInspectionCategoryResult(json) {
        if (json.success) {
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }
</script>

<el:validatableResetForm
        name="applicantInspectionCategoryResultForm"
        controller="applicantInspectionCategoryResult"
        action="save" callBackFunction="callBackInspectionCategoryResult">

    <g:render template="/applicantInspectionCategoryResult/form" model="[
            isApplicantDisabled              : isApplicantDisabled ?: params.isApplicantDisabled,
            isRelatedObjectTypeDisabled      : isRelatedObjectTypeDisabled ?: params.isRelatedObjectTypeDisabled,
            applicantInspectionCategoryResult: applicantInspectionCategoryResult]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName, action: 'list')}"
                   onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>