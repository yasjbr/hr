<el:dataTable id="joinedRecruitmentCycleDepartmentTable" searchFormName="joinedRecruitmentCycleDepartmentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="joinedRecruitmentCycleDepartment"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              domainColumns="${domainColumns}"
              serviceName="joinedRecruitmentCycleDepartment">

    <g:if test="${phaseName.toString() == ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.NEW.toString()}">
        <el:dataTableAction controller="joinedRecruitmentCycleDepartment" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete joinedRecruitmentCycleDepartment')}" />
    </g:if>
</el:dataTable>
