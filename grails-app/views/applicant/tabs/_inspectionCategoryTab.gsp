<msg:page/>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'applicantInspectionCategoryResult.entity', default: 'applicantInspectionCategoryResult')}" />
    <g:set var="tabEntities" value="${message(code: 'applicantInspectionCategoryResult.entities', default: 'applicantInspectionCategoryResult')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list applicantInspectionCategoryResult')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create applicantInspectionCategoryResult')}" />


    <el:form action="#" style="display: none;" name="applicantContactInfoSearchForm">
        <el:hiddenField name="applicant.id" value="${holderEntityId}" />
    </el:form>
    <g:render template="/applicantInspectionCategoryResult/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,tabEntity:tabEntity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>

</div>