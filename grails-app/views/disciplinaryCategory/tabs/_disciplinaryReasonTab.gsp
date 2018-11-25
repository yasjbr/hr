<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'disciplinaryCategory.entity', default: 'disciplinaryCategory List')}" />
    <g:set var="tabEntity" value="${message(code: 'disciplinaryReason.entity', default: 'disciplinaryReason')}" />
    <g:set var="tabEntities" value="${message(code: 'disciplinaryReason.entities', default: 'disciplinaryReason')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list disciplinaryReason')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create disciplinaryReason')}" />


    <el:form action="#" style="display: none;" name="disciplinaryReasonSearchForm">
        <el:hiddenField name="disciplinaryCategory.id" value="${entityId}" />
    </el:form>
    <g:render template="/disciplinaryReason/dataTable"
                 model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>

</div>