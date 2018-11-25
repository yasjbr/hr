<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            renderInLineList();
        }
    }
</script>
<g:set var="isAllowToRemove"
       value="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus == ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.NEW ? "true" : "false"}"/>
<el:validatableResetForm callBackFunction="callBackFunction" name="joinedRecruitmentCycleDepartmentForm"
                    controller="recruitmentCycle" action="updateRecruitmentCycleDepartment">
    <el:hiddenField name="encodedId" value="${recruitmentCycle?.encodedId}"/>

    <g:render template="/recruitmentCycle/departments/manageDepartment"
              model="[isAllowToRemove: isAllowToRemove]"/>

    <el:formButton functionName="save" isSubmit="true"/>
    <el:formButton functionName="cancel" accessUrl="${createLink(controller: tabEntityName, action: 'list')}"
                   onClick="renderInLineList()"/>
</el:validatableResetForm>