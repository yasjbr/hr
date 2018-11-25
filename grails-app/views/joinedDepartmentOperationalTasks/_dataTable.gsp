
<el:dataTable id="joinedDepartmentOperationalTasksTable" searchFormName="joinedDepartmentOperationalTasksSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="${preventDataTableTools?"false":"true"}"
              viewExtendButtons="${preventDataTableTools?"false":"true"}"
              widthClass="col-sm-12" controller="joinedDepartmentOperationalTasks"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="joinedDepartmentOperationalTasks"
              domainColumns="${domainColumns}">

    <g:if test="${!isReadOnly && !params['isReadOnly']}">
        <el:dataTableAction controller="joinedDepartmentOperationalTasks" action="delete" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label',args:[message(code: 'joinedDepartmentOperationalTasks.label',default: 'Operational Task')] , default: 'delete joinedDepartmentOperationalTasks')}"/>
    </g:if>
</el:dataTable>