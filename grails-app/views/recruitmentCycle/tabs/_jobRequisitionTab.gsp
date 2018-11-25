<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle List')}" />
    <g:set var="tabEntities" value="${message(code: 'jobRequisition.entities', default: 'JobRequisition List')}" />
    <g:set var="tabEntity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list jobRequisition')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create jobRequisition')}" />


    <el:form action="#" style="display: none;" name="jobRequisitionSearchForm">
        <el:hiddenField name="recruitmentCycle.id" value="${entityId}" />
    </el:form>

    <g:render template="/jobRequisition/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,phaseName:phaseName, recruitmentCycleId:entityId]"/>
</div>