<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'department.entity', default: 'department List')}" />
    <g:set var="tabEntity" value="${message(code: 'joinedDepartmentOperationalTasks.entity', default: 'joinedDepartmentOperationalTasks')}" />
    <g:set var="tabEntities" value="${message(code: 'joinedDepartmentOperationalTasks.entities', default: 'joinedDepartmentOperationalTasks')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list joinedDepartmentOperationalTasks')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create joinedDepartmentOperationalTasks')}" />


    <el:form action="#" style="display: none;" name="joinedDepartmentOperationalTasksSearchForm">
        <el:hiddenField name="department.id" value="${entityId}" />
        <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS"/>
    </el:form>

    <g:render template="/joinedDepartmentOperationalTasks/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,
                      domainColumns:'DOMAIN_TAB_COLUMNS',isReadOnly:isReadOnly,preventDataTableTools:preventDataTableTools]"/>

    <g:if test="${!isReadOnly && !params['isReadOnly']}">

        <div class="clearfix form-actions text-center">
            <btn:createButton class="btn btn-sm btn-info2"
                              accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineCreate()"
                              label="${tabTitle}">
                <i class="icon-plus"></i>
            </btn:createButton>
        </div>
    </g:if>

    </div>