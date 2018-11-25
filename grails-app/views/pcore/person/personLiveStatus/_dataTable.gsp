<el:dataTable id="personLiveStatusTable"
              searchFormName="personLiveStatusSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personLiveStatus"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personLiveStatus"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personLiveStatus',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personLiveStatus')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personLiveStatus',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personLiveStatus')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personLiveStatus" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personLiveStatus')}"/>
        <el:dataTableAction controller="personLiveStatus" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personLiveStatus')}"/>
    </g:else>
    <el:dataTableAction controller="personLiveStatus" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personLiveStatus')}"/>
</el:dataTable>
