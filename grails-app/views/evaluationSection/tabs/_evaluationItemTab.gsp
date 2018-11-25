<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'evaluationSection.entity', default: 'evaluationSection List')}" />
    <g:set var="tabEntity" value="${message(code: 'evaluationItem.entity', default: 'evaluationItem')}" />
    <g:set var="tabEntities" value="${message(code: 'evaluationItem.entities', default: 'evaluationItem')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list evaluationItem')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create evaluationItem')}" />

    <el:form action="#" style="display: none;" name="evaluationItemSearchForm">
        <el:hiddenField name="evaluationSection.id" value="${entityId}" />
    </el:form>
    <g:render template="/evaluationItem/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,tabEntity:tabEntity,domainColumns:'DOMAIN_COLUMNS', evaluationSectionId:entityId]"/>

</div>

