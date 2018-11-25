<msg:page/>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'traineeListEmployee.result.label', default: 'traineeListEmployee')}" />
    <g:set var="tabEntities" value="${message(code: 'traineeListEmployee.result.label', default: 'traineeListEmployee')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list traineeListEmployee')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create traineeListEmployee')}" />


    <el:form controller="applicant" action="getTraineeListEmployee" style="display: none;" name="traineeListEmployeeSearchForm">
        <el:hiddenField name="applicant.id" value="${entityId}" />
    </el:form>
    <g:render template="/traineeListEmployee/inLine/show"
              model="[traineeListEmployee:traineeListEmployee, isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>

</div>