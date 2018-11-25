<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'employee List')}" />
    <g:set var="tabEntity" value="${message(code: 'maritalStatusRequest.entity', default: 'employeeContactInfo')}" />
    <g:set var="tabEntities" value="${message(code: 'maritalStatusRequest.entities', default: 'employeeContactInfo')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list employeeContactInfo')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create employeeMaritalStatusRequest')}" />


    <el:form action="#" style="display: none;" name="maritalStatusRequestSearchForm">
        <el:hiddenField name="id" value="${entityId}" />
    </el:form>

    <g:render template="/maritalStatusRequest/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_COLUMNS']"/>

    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineMaritalStatusRequestCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>
</div>