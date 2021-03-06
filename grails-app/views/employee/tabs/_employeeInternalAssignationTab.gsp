<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'employeeInternalAssignation.entity', default: 'employeeInternalAssignation')}" />
<g:set var="tabEntities" value="${message(code: 'employeeInternalAssignation.entities', default: 'employeeInternalAssignation')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list employeeInternalAssignation')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create employeeInternalAssignation')}" />


<el:form action="#" style="display: none;" name="employeeInternalAssignationSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>
<g:render template="/employeeInternalAssignation/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName, action: 'create')}"
                          onclick="renderInLineCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>

</div>