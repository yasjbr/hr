<el:dataTable id="departmentContactInfoTable" searchFormName="departmentContactInfoSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="${preventDataTableTools?"false":"true"}"
              viewExtendButtons="${preventDataTableTools?"false":"true"}"
              widthClass="col-sm-12" controller="departmentContactInfo"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="departmentContactInfo"
              domainColumns="${DOMAIN_TAB_COLUMNS}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'departmentContactInfo',action: 'show')}" functionName="renderInLineShow" actionParams="id"  type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show contactInfo')}"/>
        <g:if test="${!isReadOnly && !params['isReadOnly']}">
              <el:dataTableAction accessUrl="${createLink(controller: 'departmentContactInfo',action: 'edit')}" functionName="renderInLineEdit"  actionParams="id" type="function" class="blue icon-pencil"
                                message="${message(code: 'default.edit.label', args: [entity], default: 'edit contactInfo')}"/>
            <el:dataTableAction controller="departmentContactInfo" actionParams="encodedId" action="delete" class="red icon-trash" type="confirm-delete"
                                message="${message(code: 'default.delete.label', args: [entity], default: 'delete contactInfo')}"/>
        </g:if>
    </g:if>
    <g:else>
        <el:dataTableAction controller="contactInfo" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show contactInfo')}"/>
        <el:dataTableAction controller="contactInfo" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit contactInfo')}"/>
        <el:dataTableAction controller="departmentContactInfo" actionParams="encodedId" action="delete" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete contactInfo')}"/>
    </g:else>

</el:dataTable>
