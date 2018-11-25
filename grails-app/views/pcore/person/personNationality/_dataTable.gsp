<el:dataTable id="personNationalityTable"
              searchFormName="personNationalitySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personNationality"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personNationality"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personNationality',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personNationality')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personNationality',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personNationality')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personNationality" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personNationality')}"/>
        <el:dataTableAction controller="personNationality" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personNationality')}"/>
    </g:else>
    <el:dataTableAction controller="personNationality" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personNationality')}"/>
</el:dataTable>