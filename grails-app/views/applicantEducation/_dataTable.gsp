<msg:page/>
<el:dataTable id="applicantEducationTable"
              searchFormName="applicantEducationSearchForm"
              dataTableTitle="${title}"

              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="applicant"
              spaceBefore="true"
              hasRow="true"
              action="filterEducation"
              serviceName="applicant"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'applicant',action: 'showApplicantEducation')}" functionName="renderInLineEducationShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show applicantEducation')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'applicant',action: 'edit')}" functionName="renderInLineEducationEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit applicantEducation')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="applicant" action="showApplicantEducation" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show applicantEducation')}"/>
        <el:dataTableAction controller="applicant" action="editApplicantEducation" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit applicantEducation')}"/>
    </g:else>
    %{--<el:dataTableAction controller="applicant" action="deleteApplicantEducation" class="red icon-trash" type="confirm-delete"--}%
                        %{--message="${message(code: 'default.delete.label', args: [entity], default: 'delete applicantEducation')}"/>--}%
</el:dataTable>