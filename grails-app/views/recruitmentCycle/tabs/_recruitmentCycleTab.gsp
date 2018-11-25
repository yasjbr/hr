<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle List')}"/>
    <g:set var="tabEntity"
           value="${message(code: 'recruitmentCycle.joinedRecruitmentCycleDepartment.label', default: 'department')}"/>
    <g:set var="tabEntities"
           value="${message(code: 'recruitmentCycle.joinedRecruitmentCycleDepartment.label', default: 'departments')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list department')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create departments')}"/>

    <el:form action="#" style="display: none;" name="joinedRecruitmentCycleDepartmentSearchForm">
        <el:hiddenField name="recruitmentCycle.id" value="${entityId}"/>
    </el:form>
    <g:render template="/recruitmentCycle/departments/dataTable"
              model="[isInLineActions: true, title: tabList, entity: entity,domainColumns:'DOMAIN_COLUMNS', phaseName: phaseName]"/>


<div class="clearfix form-actions text-center">
    <g:if test="${phaseName.toString() == ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.NEW.toString() || phaseName.toString() == ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.OPEN.toString()}">
            <btn:editButton onclick="renderInLineEditDepartment()" label="${tabTitle}"/>
    </g:if>
</div>

</div>