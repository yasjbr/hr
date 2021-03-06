<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle List')}" />
    <g:set var="tabEntities" value="${message(code: 'recruitmentCyclePhase.entities', default: 'recruitmentCyclePhase List')}" />
    <g:set var="tabEntity" value="${message(code: 'recruitmentCyclePhase.entity', default: 'recruitmentCyclePhase')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list recruitmentCyclePhase')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create recruitmentCyclePhase')}" />


    <el:form action="#" style="display: none;" name="recruitmentCyclePhaseSearchForm">
        <el:hiddenField name="recruitmentCycle.id" value="${entityId}" />
    </el:form>
    <g:render template="/recruitmentCyclePhase/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity, recruitmentCycle:recruitmentCycle]"/>
</div>
