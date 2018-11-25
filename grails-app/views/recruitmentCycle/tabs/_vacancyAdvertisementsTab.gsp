<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle List')}" />
    <g:set var="tabEntities" value="${message(code: 'vacancyAdvertisements.entities', default: 'JobRequisition List')}" />
    <g:set var="tabEntity" value="${message(code: 'vacancyAdvertisements.entity', default: 'JobRequisition')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list vacancyAdvertisements')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create vacancyAdvertisements')}" />


    <el:form action="#" style="display: none;" name="vacancyAdvertisementsSearchForm">
        <el:hiddenField name="recruitmentCycle.id" value="${entityId}" />
    </el:form>

    <g:render template="/vacancyAdvertisements/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity]"/>
</div>