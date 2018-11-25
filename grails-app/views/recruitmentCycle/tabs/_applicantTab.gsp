<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle List')}" />
    <g:set var="tabEntities" value="${message(code: 'applicant.entities', default: 'JobRequisition List')}" />
    <g:set var="tabEntity" value="${message(code: 'applicant.entity', default: 'JobRequisition')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list applicant')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create applicant')}" />


    <el:form action="#" style="display: none;" name="applicantForm">
        <el:hiddenField name="recruitmentCycle.id" value="${entityId}" />
    </el:form>

    <g:render template="/applicant/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity, isRecruitmentCycleTab:true]"/>
</div>