<el:dataTable id="personMaritalStatusTable"
              searchFormName="personMaritalStatusSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="personMaritalStatus"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="personMaritalStatus"
              domainColumns="${domainColumns}">



    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personMaritalStatus',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personMaritalStatus')}"/>

        <g:if test="${!params.preventWrite}">
            <el:dataTableAction accessUrl="${createLink(controller: 'personMaritalStatus',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                                message="${message(code: 'default.edit.label', args: [entity], default: 'edit personMaritalStatus')}"/>
        </g:if>
    </g:if>
    <g:else>
        <el:dataTableAction controller="personMaritalStatus" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personMaritalStatus')}"/>
        <g:if test="${!params.preventWrite}">
            <el:dataTableAction controller="personMaritalStatus" action="edit" class="blue icon-pencil"
                                message="${message(code: 'default.edit.label', args: [entity],default: 'edit personMaritalStatus')}"/>
        </g:if>
    </g:else>
    <g:if test="${!params.preventWrite}">
        <el:dataTableAction controller="personMaritalStatus" action="delete" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete personMaritalStatus')}"/>
    </g:if>
</el:dataTable>

