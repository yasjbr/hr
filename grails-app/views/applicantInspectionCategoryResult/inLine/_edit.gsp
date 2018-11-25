<el:validatableForm name="applicantInspectionCategoryResult" controller="applicantInspectionCategoryResult" action="update">
    <el:hiddenField name="encodedId" value="${applicantInspectionCategoryResult?.encodedId}" />
    <el:hiddenField name="applicant.id" value="${applicantInspectionCategoryResult?.applicant.id}"/>
    <g:render template="/applicantInspectionCategoryResult/form" model="[applicantInspectionCategoryResult:applicantInspectionCategoryResult]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>
