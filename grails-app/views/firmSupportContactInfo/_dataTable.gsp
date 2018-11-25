<el:dataTable id="firmSupportContactInfoTable" searchFormName="firmSupportContactInfoSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="firmSupportContactInfo"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="firmSupportContactInfo"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'firmSupportContactInfo',action: 'show')}" functionName="renderInLineShow" actionParams="id"  type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show contactInfo')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'firmSupportContactInfo',action: 'edit')}" functionName="renderInLineEdit"  actionParams="id" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit contactInfo')}"/>
    </g:if>
    <g:else>
        <el:dataTableAction controller="contactInfo" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show contactInfo')}"/>
        <el:dataTableAction controller="contactInfo" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit contactInfo')}"/>
    </g:else>
    <el:dataTableAction controller="contactInfo" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete contactInfo')}"/>
</el:dataTable>
