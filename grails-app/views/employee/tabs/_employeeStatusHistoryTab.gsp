<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
    <g:set var="tabEntity" value="${message(code: 'employeeStatusHistory.entity', default: 'employeeStatusHistory')}" />
    <g:set var="tabEntities" value="${message(code: 'employeeStatusHistory.entities', default: 'employeeStatusHistory')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list employeeStatusHistory')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create employeeStatusHistory')}" />


    <el:form action="#" style="display: none;" name="employeeStatusHistorySearchForm">
        <el:hiddenField name="employee.id" value="${entityId}" />
        <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
    </el:form>
    <g:render template="/employeeStatusHistory/dataTable"
              model="[isInLineActions:true,title:tabList,entity:tabEntity,
                      domainColumns:'DOMAIN_TAB_COLUMNS']"/>

    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_SUPER_ADMIN.value}">

        <div class="clearfix form-actions text-center">
            <btn:createButton class="btn btn-sm btn-info2"
                              accessUrl="${createLink(controller: tabEntityName, action: 'create')}"
                              onclick="renderInLineCreate()"
                              label="${tabTitle}">
                <i class="icon-plus"></i>
            </btn:createButton>
        </div>

    </sec:ifAnyGranted>

</div>