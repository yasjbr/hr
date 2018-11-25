<el:dataTable id="applicantTable" searchFormName="applicantForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicant"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="applicant" >

    <el:dataTableAction accessUrl="${createLink(controller: 'applicant', action: 'show')}"
                        functionName="renderInLineShow" actionParams="id" type="function"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show applicant')}"/>

    <g:if test="${params.interviewStatus != 'CLOSED' && !isRecruitmentCycleTab}">
    <el:dataTableAction actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" controller="interview" action="deleteApplicantFromInterview"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete applicant')}"/>
    </g:if>
</el:dataTable>


<script>
    //to allow prevent delete applicant where the applicant status is not UNDER_INTERVIEW
    function manageApplicantStatus(row) {
        if (row.applicantCurrentStatus.applicantStatus == 'مرحلة المقابلة') {
            return true;
        }
        return false;
    }
</script>